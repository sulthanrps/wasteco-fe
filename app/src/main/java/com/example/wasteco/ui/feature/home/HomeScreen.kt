package com.example.wasteco.ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.example.wasteco.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wasteco.ui.components.BottomTab
import com.example.wasteco.ui.components.WasteCoBottomBar
import com.example.wasteco.ui.components.WasteCoLogoBadge
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryContainer
import com.example.wasteco.ui.theme.PrimaryDarker
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.SecondaryDark
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.WasteCoTheme
import com.example.wasteco.ui.theme.BrandSolid
import com.example.wasteco.ui.theme.Surface

data class NearbyRequestItem(
    val id: String,
    val title: String,
    val itemCount: Int,
    val requesterName: String,
    val area: String,
    val distance: String,
    val timeAgo: String
)

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPantryClick: () -> Unit,
    onDonationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRefresh: () -> Unit,
    onDonationClick: (String) -> Unit,
    onDonationMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            WasteCoBottomBar(
                currentTab = BottomTab.HOME,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomTab.HOME -> { /* Already here */ }
                        BottomTab.RECIPE -> onPantryClick()
                        BottomTab.DONATIONS -> onDonationsClick()
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
                .padding(horizontal = Spacing.large, vertical = Spacing.medium)
        ) {
            // Top Header: Logo + "Beranda" / "WasteCo" on left, Green Circular Profile avatar on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WasteCoLogoBadge(size = 40.dp)
                    Spacer(modifier = Modifier.width(Spacing.compact))
                    Column {
                        Text(
                            text = "Beranda",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "WasteCo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Green circular profile button (Figma Header)
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BrandSolid)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            val userName = uiState.currentUser?.name?.substringBefore(' ').orEmpty()
            Text(
                text = if (userName.isBlank()) stringResource(R.string.home_greeting) else stringResource(R.string.home_greeting_name, userName),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryMain,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xxs))
                Text(
                    text = uiState.currentUser?.areaName?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.profile_area_unspecified),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "Ubah",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryMain,
                    modifier = Modifier.clickable { onProfileClick() }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Hero Banner Card (Ubah bahan sisa, jadi masakan lezat)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.large),
                colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.large)
                ) {
                    Text(
                        text = "Ubah bahan sisa, jadi masakan lezat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(Spacing.small))

                    Text(
                        text = "Olah bahan sisa dengan resep AI atau salurkan ke tetangga terdekat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(Spacing.large))

                    // 2 Buttons side-by-side: "Cari Ide Masak" & "Donation Hub"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                    ) {
                        Button(
                            onClick = onPantryClick,
                            shape = RoundedCornerShape(Radius.medium),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandSolid,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = Spacing.compact, vertical = Spacing.compact),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SoupKitchen,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text(
                                text = "Cari Ide Masak",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onDonationsClick,
                            shape = RoundedCornerShape(Radius.medium),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Surface,
                                contentColor = PrimaryDarker
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
                            ),
                            contentPadding = PaddingValues(horizontal = Spacing.compact, vertical = Spacing.compact),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text(
                                text = "Donation Hub",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xlarge))

            // Section "Permintaan di Sekitarmu" + Link "Peta >"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Permintaan di Sekitarmu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Peta >",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryMain,
                    modifier = Modifier.clickable(onClick = onDonationMapClick)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Nearby Donation Requests List
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                if (uiState.nearbyDonations.isEmpty() && !uiState.isLoading) {
                    Text(uiState.nearbyError ?: stringResource(R.string.donation_empty))
                }
                uiState.nearbyDonations.take(3).forEach { donation ->
                    NearbyRequestCard(
                        item = NearbyRequestItem(
                            donation.id, donation.title, donation.items.size,
                            donation.user.name, donation.areaName, donation.distanceDesc, donation.createdTimeAgo
                        ),
                        onClick = { onDonationClick(donation.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NearbyRequestCard(
    item: NearbyRequestItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Radius.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image / Thumbnail with green badge "Diminta"
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(Radius.medium))
                    .background(PrimaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.SoupKitchen,
                    contentDescription = null,
                    tint = PrimaryMain,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                )
                // "Diminta" Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(bottomEnd = Radius.small))
                        .background(BrandSolid)
                        .padding(horizontal = Spacing.small, vertical = Spacing.xxs)
                ) {
                    Text(
                        text = "Diminta",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            // Info details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Pill "3 item"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.small))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = Spacing.small, vertical = Spacing.xxs)
                    ) {
                        Text(
                            text = "${item.itemCount} item",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Requester name & distance
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xxs))
                    Text(
                        text = "${item.requesterName} • ${item.area} (${item.distance})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xxs))

                // Time ago
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = SecondaryDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xxs))
                    Text(
                        text = item.timeAgo,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryDark
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    WasteCoTheme {
        HomeScreen(
            uiState = HomeUiState(),
            onPantryClick = {},
            onDonationsClick = {},
            onProfileClick = {},
            onRefresh = {},
            onDonationClick = {},
            onDonationMapClick = {}
        )
    }
}
