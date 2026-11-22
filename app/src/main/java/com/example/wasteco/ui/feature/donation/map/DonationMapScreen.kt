package com.example.wasteco.ui.feature.donation.map

import android.Manifest
import android.content.Context
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.stringResource
import com.example.wasteco.R
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
import coil.compose.AsyncImage
import com.example.wasteco.domain.model.Donation
import com.example.wasteco.ui.components.BottomTab
import com.example.wasteco.ui.components.WasteCoBottomBar
import com.example.wasteco.ui.components.WasteCoLogoBadge
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryDarker
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private fun formatPinsJson(donations: List<Donation>): String =
    JSONArray().apply {
        donations.forEach { donation ->
            put(
                JSONObject()
                    .put("id", donation.id)
                    .put("title", donation.title)
                    .put("itemsCount", donation.items.size)
                    .put("lat", donation.latitude)
                    .put("lng", donation.longitude)
            )
        }
    }.toString()

internal fun leafletDocument(context: Context, template: String): String {
    val stylesheet = context.assets.open("leaflet/leaflet.css").bufferedReader().use { it.readText() }
    val script = context.assets.open("leaflet/leaflet.js").bufferedReader().use { it.readText() }
    return template.replace("<!-- leaflet-styles -->", "<style>$stylesheet</style>")
        .replace("<!-- leaflet-script -->", "<script>$script</script>")
}

private const val LEAFLET_HTML = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <!-- leaflet-styles -->
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        html, body {
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
            overflow: hidden;
            background-color: #EBF2EB;
        }
        #map {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            width: 100vw;
            height: 100vh;
            min-height: 400px;
            background-color: #EBF2EB;
            z-index: 1;
        }
        .leaflet-control-zoom, .leaflet-control-attribution { display: none !important; }
        .user-marker {
            width: 24px;
            height: 24px;
            background: #1E5336;
            border: 3px solid white;
            border-radius: 50%;
            box-shadow: 0 0 0 6px rgba(30, 83, 54, 0.4);
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0% { box-shadow: 0 0 0 0 rgba(30, 83, 54, 0.7); }
            70% { box-shadow: 0 0 0 12px rgba(30, 83, 54, 0); }
            100% { box-shadow: 0 0 0 0 rgba(30, 83, 54, 0); }
        }
        .wasteco-pin-container {
            display: inline-block;
        }
        .wasteco-pin {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: #FFFFFF;
            border: 2px solid #1E5336;
            color: #1E5336;
            padding: 6px 12px;
            border-radius: 20px;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            font-size: 11px;
            font-weight: 700;
            box-shadow: 0 3px 8px rgba(0,0,0,0.25);
            white-space: nowrap;
            cursor: pointer;
            transition: transform 0.2s, background 0.2s, color 0.2s;
        }
        .wasteco-pin.selected {
            background: #1E5336;
            color: #FFFFFF;
            border-color: #143B26;
            box-shadow: 0 6px 16px rgba(30,83,54,0.5);
            transform: scale(1.15);
            z-index: 9999 !important;
        }
        .wasteco-badge {
            background: #E8F5E9;
            color: #1E5336;
            padding: 2px 6px;
            border-radius: 10px;
            font-size: 10px;
            font-weight: 800;
        }
        .wasteco-pin.selected .wasteco-badge {
            background: #2E7D32;
            color: #FFFFFF;
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <!-- leaflet-script -->
    <script>
        var map = null;
        var markers = {};
        var currentSelectedId = null;
        var userMarker = null;

        function ensureMapHeight() {
            var h = window.innerHeight || document.documentElement.clientHeight || 800;
            var w = window.innerWidth || document.documentElement.clientWidth || 400;
            var el = document.getElementById('map');
            if (el) {
                el.style.width = w + 'px';
                el.style.height = h + 'px';
            }
            return h;
        }

        function initMap(userLat, userLng, hasLocation) {
            if (map) return;
            if (typeof L === 'undefined') {
                console.log("Leaflet script still loading, retrying...");
                setTimeout(function() { initMap(userLat, userLng, hasLocation); }, 150);
                return;
            }

            var h = ensureMapHeight();
            console.log("Initializing Leaflet on container height: " + h);

            map = L.map('map', {
                zoomControl: false,
                attributionControl: false
            }).setView([userLat, userLng], 15);
            window.map = map;

            // Tile Layer (OpenStreetMap Standard with HOT fallback - 100% Free & No API Key Required)
            var tileLayer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            });

            tileLayer.on('tileerror', function(e) {
                console.warn("OSM tile error, trying HOT fallback: " + (e.tile ? e.tile.src : 'unknown'));
                if (e.tile && !e.tile._hasRetried && e.coords) {
                    e.tile._hasRetried = true;
                    e.tile.src = 'https://a.tile.openstreetmap.fr/hot/' + e.coords.z + '/' + e.coords.x + '/' + e.coords.y + '.png';
                }
            });
            tileLayer.on('tileload', function(e) {
                console.log("Tile loaded: " + (e.tile ? e.tile.src : 'ok'));
            });

            tileLayer.addTo(map);

            // User Location Marker
            var userIcon = L.divIcon({
                className: 'user-marker-icon',
                html: '<div class="user-marker"></div>',
                iconSize: [24, 24],
                iconAnchor: [12, 12]
            });
            if (hasLocation) userMarker = L.marker([userLat, userLng], { icon: userIcon }).addTo(map);

            // Click anywhere on map to set user location baseline
            map.on('click', function(e) {
                if (window.AndroidWasteCo) {
                    window.AndroidWasteCo.onMapClicked(e.latlng.lat, e.latlng.lng);
                }
            });

            setTimeout(function() {
                ensureMapHeight();
                if (map) map.invalidateSize();
            }, 200);
            setTimeout(function() {
                ensureMapHeight();
                if (map) map.invalidateSize();
            }, 600);

            console.log("Leaflet initialized successfully at " + userLat + ", " + userLng);
        }

        function setDonations(pins) {
            if (!pins) return;
            console.log("setDonations called with " + pins.length + " pins");

            if (!map) {
                setTimeout(function() { setDonations(pins); }, 200);
                return;
            }

            Object.keys(markers).forEach(function(k) {
                map.removeLayer(markers[k]);
            });
            markers = {};

            pins.forEach(function(pin) {
                var container = document.createElement('div');
                container.className = 'wasteco-pin-container';
                container.id = 'pin-' + pin.id;
                var marker = document.createElement('div');
                marker.className = 'wasteco-pin';
                var title = document.createElement('span');
                title.textContent = pin.title;
                var count = document.createElement('span');
                count.className = 'wasteco-badge';
                count.textContent = pin.itemsCount + ' item';
                marker.append(title, count);
                container.append(marker);

                var icon = L.divIcon({
                    className: 'wasteco-custom-marker',
                    html: container,
                    iconSize: [120, 32],
                    iconAnchor: [60, 16]
                });

                var m = L.marker([pin.lat, pin.lng], { icon: icon }).addTo(map);
                markers[pin.id] = m;

                m.on('click', function(e) {
                    if (e && e.originalEvent) {
                        e.originalEvent.stopPropagation();
                    }
                    highlightMarker(pin.id);
                    if (window.AndroidWasteCo) {
                        window.AndroidWasteCo.onPinClicked(pin.id);
                    }
                });
            });

            if (currentSelectedId) {
                highlightMarker(currentSelectedId);
            }
        }

        function highlightMarker(id) {
            if (!map) return;
            if (currentSelectedId && markers[currentSelectedId]) {
                var prev = document.getElementById('pin-' + currentSelectedId);
                if (prev) {
                    var box = prev.querySelector('.wasteco-pin');
                    if (box) box.classList.remove('selected');
                }
            }
            currentSelectedId = id;
            if (id && markers[id]) {
                var cur = document.getElementById('pin-' + id);
                if (cur) {
                    var box = cur.querySelector('.wasteco-pin');
                    if (box) box.classList.add('selected');
                }
                map.panTo(markers[id].getLatLng(), { animate: true, duration: 0.5 });
            }
        }

        function zoomInMap() { if (map) map.zoomIn(); }
        function zoomOutMap() { if (map) map.zoomOut(); }
        function centerUser(lat, lng) { if (map) map.setView([lat, lng], 15); }

        function updateUserLocation(lat, lng) {
            if (!map) return;
            if (userMarker) {
                userMarker.setLatLng([lat, lng]);
            } else {
                var userIcon = L.divIcon({
                    className: 'user-marker-icon',
                    html: '<div class="user-marker"></div>',
                    iconSize: [24, 24],
                    iconAnchor: [12, 12]
                });
                userMarker = L.marker([lat, lng], { icon: userIcon }).addTo(map);
            }
            map.panTo([lat, lng], { animate: true, duration: 0.5 });
        }

        window.ensureMapHeight = ensureMapHeight;
        window.initMap = initMap;
        window.setDonations = setDonations;
        window.highlightMarker = highlightMarker;
        window.zoomInMap = zoomInMap;
        window.zoomOutMap = zoomOutMap;
        window.centerUser = centerUser;
        window.updateUserLocation = updateUserLocation;

        window.addEventListener('resize', ensureMapHeight);
        window.addEventListener('load', function() {
            initMap(-7.9535, 112.608);
        });
    </script>
</body>
</html>
"""

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationMapScreen(
    uiState: DonationMapUiState,
    onRetry: () -> Unit,
    onDonationSelected: (String) -> Unit,
    onLocationChanged: (Double, Double, String) -> Unit,
    onOpenLocationSheet: () -> Unit,
    onDismissLocationSheet: () -> Unit,
    onGpsLoadingChanged: (Boolean) -> Unit,
    onListViewClick: () -> Unit,
    onDonationDetailClick: (String) -> Unit,
    onHomeTabClick: () -> Unit,
    onRecipeTabClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    val selectedDonation = uiState.selectedDonation

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchGpsLocation(
                context = context,
                onLoading = { onGpsLoadingChanged(true) },
                onSuccess = { lat, lng, addr ->
                    onLocationChanged(lat, lng, addr)
                    onGpsLoadingChanged(false)
                    webViewRef?.evaluateJavascript("window.updateUserLocation($lat, $lng);", null)
                },
                onError = {
                    onGpsLoadingChanged(false)
                }
            )
        } else {
            onGpsLoadingChanged(false)
        }
    }

    // Auto-detect GPS baseline on start if permission is already granted
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            fetchGpsLocation(
                context = context,
                onLoading = { onGpsLoadingChanged(true) },
                onSuccess = { lat, lng, addr ->
                    onLocationChanged(lat, lng, addr)
                    onGpsLoadingChanged(false)
                    webViewRef?.evaluateJavascript("window.updateUserLocation($lat, $lng);", null)
                },
                onError = {
                    onGpsLoadingChanged(false)
                }
            )
        }
    }

    LaunchedEffect(uiState.userLatitude, uiState.userLongitude, uiState.hasLocation, isMapLoaded) {
        if (isMapLoaded && uiState.hasLocation) {
            webViewRef?.evaluateJavascript(
                "window.updateUserLocation(${uiState.userLatitude}, ${uiState.userLongitude});", null
            )
        }
    }

    // Keep map markers synchronized when donations list updates or map loads
    LaunchedEffect(uiState.donations, isMapLoaded) {
        if (isMapLoaded) {
            val pinsJs = formatPinsJson(uiState.donations)
            webViewRef?.evaluateJavascript("window.setDonations($pinsJs);", null)
            selectedDonation?.let {
                webViewRef?.evaluateJavascript("window.highlightMarker(${JSONObject.quote(it.id)});", null)
            }
        }
    }

    // Keep active highlight synchronized when selected donation changes
    LaunchedEffect(selectedDonation?.id, isMapLoaded) {
        if (isMapLoaded && selectedDonation != null) {
            webViewRef?.evaluateJavascript("window.highlightMarker(${JSONObject.quote(selectedDonation.id)});", null)
        }
    }

    // Keep user location pin synchronized when location changes
    LaunchedEffect(uiState.userLatitude, uiState.userLongitude, isMapLoaded) {
        if (isMapLoaded) {
            webViewRef?.evaluateJavascript(
                "window.updateUserLocation(${uiState.userLatitude}, ${uiState.userLongitude});",
                null
            )
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
                            text = "Donation Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifikasi",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = Spacing.medium)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryDarker)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            WasteCoBottomBar(
                currentTab = BottomTab.DONATIONS,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomTab.HOME -> onHomeTabClick()
                        BottomTab.RECIPE -> onRecipeTabClick()
                        BottomTab.DONATIONS -> { /* Already here */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive Leaflet Map via HTTPS base URL
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
                            fun onPinClicked(id: String) {
                                post {
                                    onDonationSelected(id)
                                }
                            }

                            @JavascriptInterface
                            fun onMapClicked(lat: Double, lng: Double) {
                                post {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val addr = resolveAddress(context, lat, lng)
                                        withContext(Dispatchers.Main) {
                                            onLocationChanged(lat, lng, addr)
                                            webViewRef?.evaluateJavascript("window.updateUserLocation($lat, $lng);", null)
                                        }
                                    }
                                }
                            }
                        }, "AndroidWasteCo")

                        webChromeClient = object : WebChromeClient() {
                            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                Log.d("WasteCoMap", "JS: ${consoleMessage?.message()} (${consoleMessage?.lineNumber()})")
                                return true
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                Log.d("WasteCoMap", "onPageFinished: $url")
                                isMapLoaded = true
                                val pinsJs = formatPinsJson(uiState.donations)
                                view?.evaluateJavascript(
                                    """
                                    window.ensureMapHeight();
                                    window.initMap(${uiState.userLatitude}, ${uiState.userLongitude}, ${uiState.hasLocation});
                                    window.setDonations($pinsJs);
                                    """.trimIndent(),
                                    null
                                )
                                selectedDonation?.let {
                                    view?.evaluateJavascript("window.highlightMarker(${JSONObject.quote(it.id)});", null)
                                }
                            }
                        }

                        loadDataWithBaseURL("https://wasteco.app", leafletDocument(ctx, LEAFLET_HTML), "text/html", "UTF-8", null)
                        webViewRef = this
                    }
                },
                update = { webView -> webViewRef = webView }
            )

            // Top Floating Search / Location & View Toggle Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.large, vertical = Spacing.compact)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(Radius.medium),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = Elevation.small
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.small))
                            .clickable { onOpenLocationSheet() }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryMain,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xxs))
                        Text(
                            text = uiState.userAddress,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(Spacing.xxs))
                        Text(
                            text = "(Ubah)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryMain
                        )
                    }

                    // View Mode Toggle (Switch back to List view)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.medium))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium))
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        IconButton(onClick = onListViewClick, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                contentDescription = "Tampilan Daftar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Tampilan Peta Leaflet",
                                tint = PrimaryMain,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Floating Map Controls (GPS Locate + Zoom In/Out)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 76.dp, end = Spacing.large),
                verticalArrangement = Arrangement.spacedBy(Spacing.compact)
            ) {
                // My Location Button
                Surface(
                    shape = RoundedCornerShape(Radius.small),
                    color = Color.White,
                    shadowElevation = Elevation.small,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(Radius.small))
                        .clickable {
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            if (hasFine || hasCoarse) {
                                fetchGpsLocation(
                                    context = context,
                                    onLoading = { onGpsLoadingChanged(true) },
                                    onSuccess = { lat, lng, addr ->
                                        onLocationChanged(lat, lng, addr)
                                        onGpsLoadingChanged(false)
                                        webViewRef?.evaluateJavascript("window.updateUserLocation($lat, $lng);", null)
                                    },
                                    onError = {
                                        onGpsLoadingChanged(false)
                                        webViewRef?.evaluateJavascript("window.centerUser(${uiState.userLatitude}, ${uiState.userLongitude});", null)
                                    }
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
                    Box(contentAlignment = Alignment.Center) {
                        if (uiState.isGpsLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PrimaryMain,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Pusatkan Lokasi Saya",
                                tint = PrimaryDarker,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Zoom In / Out Controls
                Surface(
                    shape = RoundedCornerShape(Radius.small),
                    color = Color.White,
                    shadowElevation = Elevation.small,
                    modifier = Modifier.clip(RoundedCornerShape(Radius.small))
                ) {
                    Column {
                        IconButton(
                            onClick = { webViewRef?.evaluateJavascript("window.zoomInMap();", null) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Perbesar Peta",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(1.dp)
                                .background(Color(0xFFE5E7EB))
                        )
                        IconButton(
                            onClick = { webViewRef?.evaluateJavascript("window.zoomOutMap();", null) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Perkecil Peta",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Floating Card (Selected Donation - S10)
            if (selectedDonation != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = Spacing.large, vertical = Spacing.medium)
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium)),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium)
                ) {
                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Photo with badge
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(Radius.small))
                            ) {
                                AsyncImage(
                                    model = selectedDonation.photoUrl,
                                    contentDescription = selectedDonation.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PrimaryDarker)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${selectedDonation.items.size.coerceAtLeast(1)} item",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(Spacing.compact))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedDonation.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                // Requester + Verified Check
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = PrimaryMain,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = selectedDonation.user.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = Color(0xFF1976D2),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                // Distance and Address
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Navigation,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${selectedDonation.areaName} • ${selectedDonation.distanceDesc}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Time Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFF3E0))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = Color(0xFFE87A24),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = selectedDonation.createdTimeAgo,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFE87A24)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.compact))

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.compact),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onDonationDetailClick(selectedDonation.id) },
                                shape = RoundedCornerShape(Radius.medium),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryDarker,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "Lihat Permintaan",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(Spacing.compact))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Directions Button: Launches Google Maps intent
                            Surface(
                                shape = RoundedCornerShape(Radius.medium),
                                color = Color(0xFFE8F5E9),
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(Radius.medium))
                                    .clickable {
                                        val geoUri = Uri.parse("geo:${selectedDonation.latitude},${selectedDonation.longitude}?q=${selectedDonation.latitude},${selectedDonation.longitude}(${Uri.encode(selectedDonation.title)})")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                        try {
                                            context.startActivity(mapIntent)
                                        } catch (_: Exception) {
                                            webViewRef?.evaluateJavascript("window.highlightMarker(${JSONObject.quote(selectedDonation.id)});", null)
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.NearMe,
                                        contentDescription = "Petunjuk Arah",
                                        tint = PrimaryDarker,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryMain)
                }
            } else {
                androidx.compose.material3.Surface(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        Text(uiState.errorMessage ?: stringResource(R.string.donation_empty))
                        if (uiState.errorMessage != null) {
                            androidx.compose.material3.TextButton(onClick = onRetry) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.isLocationSheetVisible) {
        LocationPickerBottomSheet(
            currentAddress = uiState.userAddress,
            isGpsLoading = uiState.isGpsLoading,
            onSelectLocation = { lat, lng, addr ->
                onLocationChanged(lat, lng, addr)
                webViewRef?.evaluateJavascript("window.updateUserLocation($lat, $lng);", null)
            },
            onRequestGps = {
                val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (hasFine || hasCoarse) {
                    fetchGpsLocation(
                        context = context,
                        onLoading = { onGpsLoadingChanged(true) },
                        onSuccess = { lat, lng, addr ->
                            onLocationChanged(lat, lng, addr)
                            onGpsLoadingChanged(false)
                            onDismissLocationSheet()
                            webViewRef?.evaluateJavascript("window.updateUserLocation($lat, $lng);", null)
                        },
                        onError = {
                            onGpsLoadingChanged(false)
                        }
                    )
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            onDismiss = onDismissLocationSheet
        )
    }
}

data class LocationPreset(
    val name: String,
    val subtitle: String,
    val latitude: Double,
    val longitude: Double
)

private val MALANG_PRESETS = listOf(
    LocationPreset("Dinoyo", "Kec. Lowokwaru, Kota Malang", -7.9535, 112.6080),
    LocationPreset("Lowokwaru (Suhat)", "Jl. Soekarno Hatta, Kota Malang", -7.9450, 112.6150),
    LocationPreset("Klojen (Alun-Alun)", "Pusat Kota Malang", -7.9826, 112.6308),
    LocationPreset("Blimbing", "Kec. Blimbing, Kota Malang", -7.9400, 112.6450),
    LocationPreset("Sukun", "Kec. Sukun, Kota Malang", -7.9950, 112.6180),
    LocationPreset("Kota Batu", "Alun-Alun Kota Wisata Batu", -7.8700, 112.5250)
)

private fun resolveAddress(context: Context, lat: Double, lng: Double): String {
    return try {
        if (!Geocoder.isPresent()) {
            return "%.4f, %.4f".format(Locale.US, lat, lng)
        }
        val geocoder = Geocoder(context, Locale.forLanguageTag("id-ID"))
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

private fun fetchGpsLocation(
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
        val addr = resolveAddress(context, last.latitude, last.longitude)
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
                    val addr = resolveAddress(context, loc.latitude, loc.longitude)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerBottomSheet(
    currentAddress: String,
    isGpsLoading: Boolean,
    onSelectLocation: (Double, Double, String) -> Unit,
    onRequestGps: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large)
                .padding(bottom = Spacing.xxlarge)
        ) {
            Text(
                text = "Pilih Titik Lokasimu",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = "Tentukan titik acuan untuk mencari donasi makanan di sekitarmu.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Option 1: Live GPS
            Surface(
                shape = RoundedCornerShape(Radius.medium),
                color = Color(0xFFE8F5E9),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.medium))
                    .clickable { onRequestGps() }
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryMain),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isGpsLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(Spacing.medium))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gunakan Lokasi GPS Saat Ini",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarker
                        )
                        Text(
                            text = if (isGpsLoading) "Sedang mencari koordinat GPS..." else "Deteksi posisi otomatis dari perangkat",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Option 2: Area Populer di Malang
            Text(
                text = "Area Populer di Malang & Sekitar",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Spacing.small))

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                MALANG_PRESETS.forEach { preset ->
                    val isSelected = currentAddress.contains(preset.name, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(Radius.medium),
                        color = if (isSelected) Color(0xFFF1F8F1) else Color.Transparent,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryMain) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Radius.medium))
                            .clickable {
                                onSelectLocation(preset.latitude, preset.longitude, "${preset.name}, Malang")
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.medium, vertical = Spacing.compact),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = preset.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = preset.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Dipilih",
                                    tint = PrimaryMain,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Tip: Tap on Map
            Surface(
                shape = RoundedCornerShape(Radius.medium),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text(
                        text = "Tip: Kamu juga bisa langsung mengetuk titik mana saja di peta untuk memindahkan lokasimu secara bebas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
