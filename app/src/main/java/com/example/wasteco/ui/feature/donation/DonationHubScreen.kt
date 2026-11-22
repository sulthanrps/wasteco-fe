package com.example.wasteco.ui.feature.donation

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wasteco.domain.model.Donation
import com.example.wasteco.domain.model.DonationRole
import com.example.wasteco.ui.components.BottomTab
import com.example.wasteco.ui.components.WasteCoBottomBar
import com.example.wasteco.ui.components.WasteCoLogoBadge
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryContainer
import com.example.wasteco.ui.theme.PrimaryDarker
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.SecondaryMain
import com.example.wasteco.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationHubScreen(
    uiState: DonationHubUiState,
    onTabSelected: (DonationTab) -> Unit,
    onActivityFilterSelected: (ActivityFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onMapViewClick: () -> Unit,
    onCreateRequestClick: () -> Unit,
    onDonationClick: (String) -> Unit,
    onConfirmReceiptClick: (String) -> Unit,
    onConfirmHandoverAsDonor: (String) -> Unit,
    onCancelMyDonationClick: (String) -> Unit,
    onHomeTabClick: () -> Unit,
    onRecipeTabClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.large, vertical = Spacing.small)
        ) {
            // Location Subheader & "+ Minta Bahan" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = PrimaryMain,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xxs))
                    Text(
                        text = uiState.currentLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(Spacing.xxs))
                    Text(
                        text = "(Ubah)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryMain
                    )
                }

                // "+ Minta Bahan" Button
                Button(
                    onClick = onCreateRequestClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDarker,
                        contentColor = Color.White
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Minta Bahan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Segmented Control Tabs: Sekitar Saya vs Aktivitas Saya (Opsi B)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFEFF3EE))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tab 1: Sekitar Saya
                val isNearby = uiState.selectedTab == DonationTab.NEARBY
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onTabSelected(DonationTab.NEARBY) },
                    color = if (isNearby) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = if (isNearby) Elevation.small else Elevation.none
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isNearby) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sekitar Saya",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isNearby) FontWeight.Bold else FontWeight.Medium,
                            color = if (isNearby) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Tab 2: Aktivitas Saya (Opsi B)
                val isActivity = uiState.selectedTab == DonationTab.MY_ACTIVITIES
                val totalActivities = uiState.myDonations.size + uiState.givenDonations.size
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onTabSelected(DonationTab.MY_ACTIVITIES) },
                    color = if (isActivity) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = if (isActivity) Elevation.small else Elevation.none
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = if (isActivity) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Aktivitas Saya",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isActivity) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActivity) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (totalActivities > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryMain)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryMain)
                }
            } else if (uiState.selectedTab == DonationTab.NEARBY) {
                // S09 — Tab Sekitar Saya
                // Search & View Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Cari permintaan bahan...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(Radius.medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = PrimaryMain
                        )
                    )

                    // View Mode Toggle (List vs Map)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.medium))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(2.dp)
                    ) {
                        IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                contentDescription = "Tampilan Daftar",
                                tint = PrimaryMain,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = onMapViewClick, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Tampilan Peta Interaktif Leaflet",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Permintaan Aktif Sekitarmu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${uiState.filteredNearbyDonations.size} Permintaan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.compact))

                // List of Nearby Requests
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                    uiState.filteredNearbyDonations.forEach { donation ->
                        NearbyDonationCard(
                            donation = donation,
                            onClick = { onDonationClick(donation.id) }
                        )
                    }
                }
            } else {
                // S12 & Opsi B — Tab Aktivitas Saya (Sedang Memberi & Sedang Meminta)
                // Sub-filter Chips: Semua, Sedang Memberi, Sedang Meminta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    FilterChip(
                        selected = uiState.selectedActivityFilter == ActivityFilter.ALL,
                        onClick = { onActivityFilterSelected(ActivityFilter.ALL) },
                        label = {
                            Text(
                                text = "Semua (${uiState.givenDonations.size + uiState.myDonations.size})",
                                fontSize = 12.sp,
                                fontWeight = if (uiState.selectedActivityFilter == ActivityFilter.ALL) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryDarker,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uiState.selectedActivityFilter == ActivityFilter.ALL,
                            borderColor = Color(0xFFE5E7EB),
                            selectedBorderColor = PrimaryDarker
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    FilterChip(
                        selected = uiState.selectedActivityFilter == ActivityFilter.GIVING,
                        onClick = { onActivityFilterSelected(ActivityFilter.GIVING) },
                        label = {
                            Text(
                                text = "Sedang Memberi (${uiState.givenDonations.size})",
                                fontSize = 12.sp,
                                fontWeight = if (uiState.selectedActivityFilter == ActivityFilter.GIVING) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryDarker,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uiState.selectedActivityFilter == ActivityFilter.GIVING,
                            borderColor = Color(0xFFE5E7EB),
                            selectedBorderColor = PrimaryDarker
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    FilterChip(
                        selected = uiState.selectedActivityFilter == ActivityFilter.REQUESTING,
                        onClick = { onActivityFilterSelected(ActivityFilter.REQUESTING) },
                        label = {
                            Text(
                                text = "Sedang Meminta (${uiState.myDonations.size})",
                                fontSize = 12.sp,
                                fontWeight = if (uiState.selectedActivityFilter == ActivityFilter.REQUESTING) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryDarker,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uiState.selectedActivityFilter == ActivityFilter.REQUESTING,
                            borderColor = Color(0xFFE5E7EB),
                            selectedBorderColor = PrimaryDarker
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                val activities = uiState.filteredActivities
                if (activities.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.compact)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = when (uiState.selectedActivityFilter) {
                                    ActivityFilter.GIVING -> "Belum ada donasi yang sedang kamu berikan.\nJelajahi tab 'Sekitar Saya' untuk membantu tetangga!"
                                    ActivityFilter.REQUESTING -> "Belum ada permintaan bahan dari kamu.\nKlik '+ Minta Bahan' di atas untuk membuat permintaan."
                                    ActivityFilter.ALL -> "Belum ada aktivitas donasi aktif.\nKamu bisa memberi atau meminta bahan makanan di komunitas."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.large)) {
                        activities.forEach { item ->
                            if (item.role == DonationRole.DONOR) {
                                GivingDonationCard(
                                    donation = item.donation,
                                    onConfirmHandover = { onConfirmHandoverAsDonor(item.donation.id) }
                                )
                            } else {
                                MyDonationCard(
                                    donation = item.donation,
                                    onConfirmReceipt = { onConfirmReceiptClick(item.donation.id) },
                                    onCancel = { onCancelMyDonationClick(item.donation.id) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xlarge))
        }
    }
}

@Composable
private fun NearbyDonationCard(
    donation: Donation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.medium))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium))
            .clickable { onClick() },
        shape = RoundedCornerShape(Radius.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo with item count badge
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(Radius.small))
            ) {
                AsyncImage(
                    model = donation.photoUrl,
                    contentDescription = donation.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Item Count Badge
                Box(
                    modifier = Modifier
                        .padding(Spacing.xs)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryDarker)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${donation.items.size.coerceAtLeast(1)} ITEM",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = donation.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = donation.itemsSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Requester row: Avatar + Name + Distance
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = donation.user.name.firstOrNull()?.uppercase() ?: "W",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarker
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = donation.user.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${donation.distanceDesc}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Time Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF3E0))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFE87A24),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = donation.createdTimeAgo,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE87A24)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GivingDonationCard(
    donation: Donation,
    onConfirmHandover: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium)),
        shape = RoundedCornerShape(Radius.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Photo Header with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = donation.photoUrl,
                    contentDescription = donation.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Floating Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Sebagai Donor" Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryDarker)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sebagai Donor",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (donation.step >= 3) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (donation.step >= 3) "Selesai" else "Menunggu Penyerahan",
                            color = if (donation.step >= 3) PrimaryDarker else Color(0xFFE87A24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // In-Photo Bottom Text Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(Color(0x99000000))
                        .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                ) {
                    Column {
                        Text(
                            text = donation.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = donation.itemsSummary,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(Spacing.medium)) {
                // Recipient Info Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.small))
                        .background(Color(0xFFE8F5E9))
                        .padding(Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryMain),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = donation.user.name.firstOrNull()?.uppercase() ?: "P",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.compact))

                    Column {
                        Text(
                            text = "Diberikan kepada: ${donation.user.name}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarker
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Titik temu: ${donation.meetingPoint}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Waktu: ${donation.pickupDeadline}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Milestone Progress Tracker Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Radius.small),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAF7))
                ) {
                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status Penyerahan Bahan",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tahap ${donation.step} dari 3",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarker
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.compact))

                        LinearProgressIndicator(
                            progress = { donation.step / 3f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PrimaryMain,
                            trackColor = Color(0xFFE5E7EB)
                        )

                        Spacer(modifier = Modifier.height(Spacing.compact))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "✓ Disetujui",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryMain
                            )
                            Text(
                                text = if (donation.step >= 2) "✓ Serah Terima" else "Serah Terima",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (donation.step >= 2) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (donation.step >= 3) "✓ Selesai" else "Selesai",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (donation.step >= 3) FontWeight.Bold else FontWeight.Normal,
                                color = if (donation.step >= 3) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // WhatsApp Contact Button
                Button(
                    onClick = {
                        val cleanPhone = donation.whatsappNumber.replace("+", "").replace(" ", "").replace("-", "")
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=Halo%20${donation.user.name},%20saya%20sudah%20menyiapkan%20donasi%20bahan%20dari%20WasteCo.")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.compact))
                    Text(
                        text = "Hubungi Penerima via WhatsApp",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (donation.step < 3) {
                    Spacer(modifier = Modifier.height(Spacing.compact))

                    // Mark Handed Over
                    WasteCoPrimaryButton(
                        text = "Konfirmasi Bahan Sudah Diserahkan",
                        onClick = onConfirmHandover,
                        leadingIcon = Icons.Default.Check
                    )
                }
            }
        }
    }
}

@Composable
private fun MyDonationCard(
    donation: Donation,
    onConfirmReceipt: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium)),
        shape = RoundedCornerShape(Radius.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Photo Header with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = donation.photoUrl,
                    contentDescription = donation.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Floating Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Sebagai Pemohon" Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2E7D32))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sebagai Pemohon",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Time Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = donation.createdTimeAgo,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // In-Photo Bottom Text Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(Color(0x99000000))
                        .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                ) {
                    Column {
                        Text(
                            text = donation.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = donation.itemsSummary,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(Spacing.medium)) {
                val hasDonor = donation.donor != null && donation.step >= 2
                val isCompleted = donation.step >= 3 || donation.status == "completed"

                // Dynamic Status Banner based on Lifecycle
                if (isCompleted) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Radius.small))
                            .background(Color(0xFFE8F5E9))
                            .padding(Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.compact))

                        Column {
                            Text(
                                text = "Permintaan Selesai",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Bahan makanan telah berhasil diterima.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else if (hasDonor) {
                    val donorName = donation.donor?.name ?: "Donatur Sekitar"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Radius.small))
                            .background(Color(0xFFE8F5E9))
                            .padding(Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryMain),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.compact))

                        Column {
                            Text(
                                text = "$donorName memenuhi permintaanmu",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarker
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Diantar ke titik lokasi • ${donation.pickupDeadline}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    // Tahap 1: Menunggu donatur sekitar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Radius.small))
                            .background(Color(0xFFFFF8E1))
                            .padding(Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF57F17)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.compact))

                        Column {
                            Text(
                                text = "Menunggu Donatur Sekitar",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Permintaanmu sedang disiarkan ke tetangga terdekat.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Milestone Progress Tracker Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Radius.small),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAF7))
                ) {
                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status Permintaan",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tahap ${donation.step} dari 3",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarker
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.compact))

                        // Progress Bar (step 1 = 33%, step 2 = 66%, step 3 = 100%)
                        LinearProgressIndicator(
                            progress = { donation.step / 3f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PrimaryMain,
                            trackColor = Color(0xFFE5E7EB)
                        )

                        Spacer(modifier = Modifier.height(Spacing.compact))

                        // 3 Milestones
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "✓ Dibuat",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryMain
                            )
                            Text(
                                text = if (donation.step >= 2) "📦 Diserahkan" else "Diserahkan",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (donation.step >= 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (donation.step >= 2) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (donation.step >= 3) "✓ Diterima" else "Diterima",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (donation.step >= 3) FontWeight.Bold else FontWeight.Normal,
                                color = if (donation.step >= 3) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Action Buttons: Only show "Sudah Saya Terima" if donor has accepted and handed over (step 2)
                if (hasDonor && !isCompleted) {
                    WasteCoPrimaryButton(
                        text = "Sudah Saya Terima",
                        onClick = onConfirmReceipt,
                        leadingIcon = Icons.Default.Handshake
                    )
                    Spacer(modifier = Modifier.height(Spacing.compact))
                }

                // If not completed yet, allow cancelling
                if (!isCompleted) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(Radius.medium),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFEBEE),
                            contentColor = Color(0xFFC62828)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.compact))
                        Text(
                            text = "Batalkan Permintaan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
