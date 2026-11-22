package com.example.wasteco.ui.feature.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

@Composable
fun ProfileRoute(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locate = rememberDeviceLocationRequest(
        onStart = viewModel::locating,
        onLocation = viewModel::setLocation,
        onUnavailable = viewModel::locationUnavailable
    )
    ProfileScreen(
        state = state,
        onBack = onBack,
        onNameChange = viewModel::changeName,
        onPhoneChange = viewModel::changePhone,
        onAreaChange = viewModel::changeArea,
        onLocation = locate,
        onSave = viewModel::save,
        onRetry = viewModel::load,
        onLogout = viewModel::logout
    )
}

@Composable
internal fun rememberDeviceLocationRequest(
    onStart: () -> Unit,
    onLocation: (Double, Double, String?) -> Unit,
    onUnavailable: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnLocation by rememberUpdatedState(onLocation)
    val currentOnUnavailable by rememberUpdatedState(onUnavailable)
    val activeRequest = remember { mutableStateOf<CancellationSignal?>(null) }
    DisposableEffect(context) {
        onDispose {
            val request = activeRequest.value
            activeRequest.value = null
            request?.cancel()
            if (request != null) currentOnUnavailable()
        }
    }

    val locate = {
        onStart()
        activeRequest.value?.cancel()
        val signal = CancellationSignal()
        activeRequest.value = signal
        requestProfileLocation(
            context,
            signal,
            onLocation = { latitude, longitude ->
                if (activeRequest.value === signal) {
                    scope.launch {
                        val area = withTimeoutOrNull(6_000) {
                            findAreaName(context, latitude, longitude)
                        }
                        if (activeRequest.value === signal) {
                            activeRequest.value = null
                            currentOnLocation(latitude, longitude, area)
                        }
                    }
                }
            },
            onUnavailable = {
                if (activeRequest.value === signal) {
                    activeRequest.value = null
                    currentOnUnavailable()
                }
            }
        )
    }
    val permissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        if (results.values.any { it }) locate() else currentOnUnavailable()
    }
    return {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (locationPermissions.any {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }) {
            locate()
        } else {
            permissions.launch(locationPermissions)
        }
    }
}

@SuppressLint("MissingPermission")
private fun requestProfileLocation(
    context: Context,
    signal: CancellationSignal,
    onLocation: (Double, Double) -> Unit,
    onUnavailable: () -> Unit
) {
    val manager = context.getSystemService(LocationManager::class.java) ?: return onUnavailable()
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
    val provider = when {
        fine && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> return onUnavailable()
    }
    try {
        manager.getCurrentLocation(provider, signal, context.mainExecutor) { location ->
            if (location == null) onUnavailable() else onLocation(location.latitude, location.longitude)
        }
    } catch (_: SecurityException) {
        onUnavailable()
    } catch (_: IllegalArgumentException) {
        onUnavailable()
    }
}

private suspend fun findAreaName(context: Context, latitude: Double, longitude: Double): String? =
    suspendCancellableCoroutine { continuation ->
        if (!Geocoder.isPresent()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        try {
            Geocoder(context, Locale.forLanguageTag("id-ID")).getFromLocation(
                latitude, longitude, 1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<android.location.Address>) {
                        val area = addresses.firstOrNull()?.let { address ->
                            val neighborhood = address.subLocality?.takeIf { it.isNotBlank() }
                            val city = address.locality?.takeIf { it.isNotBlank() }
                                ?: address.subAdminArea?.takeIf { it.isNotBlank() }
                            listOfNotNull(neighborhood, city).distinct().joinToString(", ")
                                .ifBlank { address.adminArea.orEmpty() }
                                .ifBlank { null }
                        }
                        continuation.resume(area)
                    }

                    override fun onError(errorMessage: String?) {
                        continuation.resume(null)
                    }
                }
            )
        } catch (_: IllegalArgumentException) {
            continuation.resume(null)
        } catch (_: SecurityException) {
            continuation.resume(null)
        }
    }
