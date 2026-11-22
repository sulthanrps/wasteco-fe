package com.example.wasteco.ui.feature.donation.create

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.wasteco.domain.model.DonationItem
import com.example.wasteco.ui.components.WasteCoLogoBadge
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryContainer
import com.example.wasteco.ui.theme.PrimaryDarker
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private const val LEAFLET_PICKER_HTML = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.css" />
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        html, body { width: 100%; height: 100%; margin: 0; padding: 0; overflow: hidden; background: #EBF2EB; }
        #map { width: 100%; height: 100%; position: absolute; top: 0; left: 0; }
        .leaflet-control-zoom, .leaflet-control-attribution { display: none !important; }
        .picker-pin {
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #1E5336;
            color: #FFFFFF;
            border: 3px solid #FFFFFF;
            border-radius: 50% 50% 50% 0;
            transform: rotate(-45deg);
            box-shadow: 0 4px 10px rgba(0,0,0,0.35);
        }
        .picker-pin span {
            transform: rotate(45deg);
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.js"></script>
    <script>
        var map = null;
        var pinMarker = null;

        function initPickerMap(lat, lng) {
            if (map) return;
            if (typeof L === 'undefined') {
                setTimeout(function() { initPickerMap(lat, lng); }, 150);
                return;
            }
            map = L.map('map', { zoomControl: false, attributionControl: false }).setView([lat, lng], 16);
            
            var tileLayer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 });
            tileLayer.on('tileerror', function(e) {
                if (e.tile && !e.tile._hasRetried && e.coords) {
                    e.tile._hasRetried = true;
                    e.tile.src = 'https://a.tile.openstreetmap.fr/hot/' + e.coords.z + '/' + e.coords.x + '/' + e.coords.y + '.png';
                }
            });
            tileLayer.addTo(map);

            var pinIcon = L.divIcon({
                className: 'picker-pin-wrapper',
                html: '<div class="picker-pin"><span>📍</span></div>',
                iconSize: [32, 32],
                iconAnchor: [16, 32]
            });

            pinMarker = L.marker([lat, lng], { icon: pinIcon }).addTo(map);

            map.on('click', function(e) {
                setPinPosition(e.latlng.lat, e.latlng.lng);
                if (window.AndroidPicker) {
                    window.AndroidPicker.onLocationSelected(e.latlng.lat, e.latlng.lng);
                }
            });

            setTimeout(function() { if (map) map.invalidateSize(); }, 300);
        }

        function setPinPosition(lat, lng) {
            if (!map) return;
            if (pinMarker) {
                pinMarker.setLatLng([lat, lng]);
            }
            map.panTo([lat, lng], { animate: true, duration: 0.5 });
        }

        window.initPickerMap = initPickerMap;
        window.setPinPosition = setPinPosition;
        window.addEventListener('load', function() {
            initPickerMap(-7.9535, 112.6080);
        });
    </script>
</body>
</html>
"""

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDonationScreen(
    uiState: CreateDonationUiState,
    onBackClick: () -> Unit,
    onIngredientNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onMeetingPointChange: (String) -> Unit,
    onLocationChange: (Double, Double, String?) -> Unit,
    onGpsLoadingChanged: (Boolean) -> Unit,
    onPickupTimeChange: (String) -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (DonationItem) -> Unit,
    onSubmit: () -> Unit,
    onSuccessFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    var unitMenuExpanded by remember { mutableStateOf(false) }
    val units = listOf("porsi", "gram", "kg", "buah", "butir", "ikat", "bungkus")

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchPickerGpsLocation(
                context = context,
                onLoading = { onGpsLoadingChanged(true) },
                onSuccess = { lat, lng, addr ->
                    onLocationChange(lat, lng, addr)
                    onGpsLoadingChanged(false)
                    webViewRef?.evaluateJavascript("window.setPinPosition($lat, $lng);", null)
                },
                onError = {
                    onGpsLoadingChanged(false)
                }
            )
        } else {
            onGpsLoadingChanged(false)
        }
    }

    LaunchedEffect(uiState.latitude, uiState.longitude, isMapLoaded) {
        if (isMapLoaded) {
            webViewRef?.evaluateJavascript(
                "window.setPinPosition(${uiState.latitude}, ${uiState.longitude});",
                null
            )
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccessFinish()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        WasteCoLogoBadge(size = 32.dp)
                        Text(
                            text = "Buat Permintaan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = Elevation.small,
                shadowElevation = Elevation.small
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.large, vertical = Spacing.medium)
                ) {
                    WasteCoPrimaryButton(
                        text = if (uiState.isSubmitting) "Mengirimkan..." else "Kirim Permintaan",
                        onClick = onSubmit,
                        enabled = !uiState.isSubmitting && uiState.addedItems.isNotEmpty(),
                        leadingIcon = Icons.Default.NearMe
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.large, vertical = Spacing.small)
        ) {
            // Card 1: Bahan yang Dibutuhkan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium)),
                shape = RoundedCornerShape(Radius.medium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
            ) {
                Column(modifier = Modifier.padding(Spacing.medium)) {
                    Text(
                        text = "Bahan yang Dibutuhkan *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    OutlinedTextField(
                        value = uiState.inputIngredientName,
                        onValueChange = onIngredientNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Contoh: Bawang merah", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(Radius.small),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF7FAF7),
                            focusedContainerColor = Color(0xFFF7FAF7),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = PrimaryMain
                        )
                    )

                    Spacer(modifier = Modifier.height(Spacing.compact))

                    // Row: Jumlah & Satuan Dropdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Jumlah *",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = uiState.inputQuantity,
                                onValueChange = onQuantityChange,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(Radius.small),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(0xFFF7FAF7),
                                    focusedContainerColor = Color(0xFFF7FAF7),
                                    unfocusedBorderColor = Color(0xFFE5E7EB),
                                    focusedBorderColor = PrimaryMain
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Satuan *",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                OutlinedTextField(
                                    value = uiState.inputUnit,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { unitMenuExpanded = true },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            modifier = Modifier.clickable { unitMenuExpanded = true }
                                        )
                                    },
                                    shape = RoundedCornerShape(Radius.small),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Color(0xFFF7FAF7),
                                        focusedContainerColor = Color(0xFFF7FAF7),
                                        unfocusedBorderColor = Color(0xFFE5E7EB),
                                        focusedBorderColor = PrimaryMain
                                    )
                                )

                                DropdownMenu(
                                    expanded = unitMenuExpanded,
                                    onDismissRequest = { unitMenuExpanded = false }
                                ) {
                                    units.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                onUnitChange(unit)
                                                unitMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.medium))

                    // Button: "+ Tambah ke Daftar"
                    Button(
                        onClick = onAddIngredient,
                        shape = RoundedCornerShape(Radius.medium),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = PrimaryDarker
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = "+ Tambah ke Daftar",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.medium))

                    // Section: Daftar Bahan Diminta
                    Text(
                        text = "Daftar Bahan Diminta (${uiState.addedItems.size} item)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(Spacing.compact))

                    // List of added chips
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        uiState.addedItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(Radius.small))
                                    .background(Color(0xFFEBF4EB))
                                    .padding(horizontal = Spacing.compact, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val icon = when {
                                        item.name.contains("telur", true) -> Icons.Default.Egg
                                        item.name.contains("nasi", true) -> Icons.Default.RiceBowl
                                        else -> Icons.Default.Spa
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = PrimaryMain,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.compact))
                                    Text(
                                        text = "${item.name} — ${item.quantity}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                IconButton(
                                    onClick = { onRemoveIngredient(item) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Card 2: Titik Lokasi Penerimaan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium)),
                shape = RoundedCornerShape(Radius.medium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
            ) {
                Column(modifier = Modifier.padding(Spacing.medium)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Titik Lokasi Penerimaan *",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryMain,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = uiState.areaName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryMain
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.compact))

                    // Interactive Leaflet Mini Map Picker
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(Radius.medium))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium))
                    ) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                                    settings.userAgentString = "WasteCo-Android/1.0 (com.example.wasteco; contact@wasteco.app; Mobile; Android 15)"

                                    addJavascriptInterface(object {
                                        @JavascriptInterface
                                        fun onLocationSelected(lat: Double, lng: Double) {
                                            post {
                                                coroutineScope.launch(Dispatchers.IO) {
                                                    val addr = resolvePickerAddress(context, lat, lng)
                                                    withContext(Dispatchers.Main) {
                                                        onLocationChange(lat, lng, addr)
                                                    }
                                                }
                                            }
                                        }
                                    }, "AndroidPicker")

                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            isMapLoaded = true
                                            view?.evaluateJavascript(
                                                "window.initPickerMap(${uiState.latitude}, ${uiState.longitude});",
                                                null
                                            )
                                        }
                                    }

                                    loadDataWithBaseURL("https://wasteco.app", LEAFLET_PICKER_HTML, "text/html", "UTF-8", null)
                                    webViewRef = this
                                }
                            },
                            update = { webView ->
                                webViewRef = webView
                            }
                        )

                        // Floating Badge: "Ketuk peta untuk memindahkan pin"
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = Elevation.small,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(Spacing.small)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = PrimaryMain,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Ketuk peta untuk geser pin",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Floating GPS Button
                        Surface(
                            shape = RoundedCornerShape(Radius.small),
                            color = Color.White,
                            shadowElevation = Elevation.small,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(Spacing.small)
                                .clip(RoundedCornerShape(Radius.small))
                                .clickable {
                                    val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    if (hasFine || hasCoarse) {
                                        fetchPickerGpsLocation(
                                            context = context,
                                            onLoading = { onGpsLoadingChanged(true) },
                                            onSuccess = { lat, lng, addr ->
                                                onLocationChange(lat, lng, addr)
                                                onGpsLoadingChanged(false)
                                                webViewRef?.evaluateJavascript("window.setPinPosition($lat, $lng);", null)
                                            },
                                            onError = { onGpsLoadingChanged(false) }
                                        )
                                    } else {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (uiState.isGpsLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = PrimaryMain,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = "Gunakan GPS",
                                        tint = PrimaryDarker,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "Gunakan GPS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarker
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.compact))

                    // Detail Lokasi Input
                    OutlinedTextField(
                        value = uiState.meetingPoint,
                        onValueChange = onMeetingPointChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(Radius.small),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF7FAF7),
                            focusedContainerColor = Color(0xFFF7FAF7),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = PrimaryMain
                        )
                    )

                    Spacer(modifier = Modifier.height(Spacing.medium))

                    // Waktu Bisa Menerima
                    Text(
                        text = "Waktu Bisa Menerima *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    OutlinedTextField(
                        value = uiState.pickupTime,
                        onValueChange = onPickupTimeChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(Radius.small),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF7FAF7),
                            focusedContainerColor = Color(0xFFF7FAF7),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = PrimaryMain
                        )
                    )
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(Spacing.medium))
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xlarge))
        }
    }
}

private fun resolvePickerAddress(context: Context, lat: Double, lng: Double): String {
    return try {
        if (!Geocoder.isPresent()) {
            return "%.4f, %.4f".format(Locale.US, lat, lng)
        }
        val geocoder = Geocoder(context, Locale("id", "ID"))
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        val addr = addresses?.firstOrNull()
        if (addr != null) {
            val subLoc = addr.subLocality ?: addr.locality ?: addr.subAdminArea ?: "Malang"
            val city = addr.subAdminArea ?: addr.adminArea ?: "Kota Malang"
            "$subLoc, $city"
        } else {
            "%.4f, %.4f".format(Locale.US, lat, lng)
        }
    } catch (_: Exception) {
        "%.4f, %.4f".format(Locale.US, lat, lng)
    }
}

private fun fetchPickerGpsLocation(
    context: Context,
    onLoading: () -> Unit,
    onSuccess: (Double, Double, String) -> Unit,
    onError: () -> Unit
) {
    onLoading()
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    if (locationManager == null) {
        onError()
        return
    }

    val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    if (!hasFine && !hasCoarse) {
        onError()
        return
    }

    val last = try {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    } catch (_: SecurityException) {
        null
    }

    if (last != null) {
        val addr = resolvePickerAddress(context, last.latitude, last.longitude)
        onSuccess(last.latitude, last.longitude, addr)
        return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            locationManager.getCurrentLocation(
                LocationManager.NETWORK_PROVIDER,
                null,
                context.mainExecutor
            ) { loc ->
                if (loc != null) {
                    val addr = resolvePickerAddress(context, loc.latitude, loc.longitude)
                    onSuccess(loc.latitude, loc.longitude, addr)
                } else {
                    onError()
                }
            }
        } catch (_: SecurityException) {
            onError()
        }
    } else {
        onError()
    }
}
