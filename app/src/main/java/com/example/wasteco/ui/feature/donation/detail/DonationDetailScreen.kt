package com.example.wasteco.ui.feature.donation.detail

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.stringResource
import com.example.wasteco.R
import com.example.wasteco.ui.feature.donation.DonationDirectionsButton
import com.example.wasteco.ui.feature.donation.DonationWhatsAppButton
import com.example.wasteco.ui.feature.donation.donationStatusLabel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wasteco.domain.model.Donation
import com.example.wasteco.ui.components.WasteCoLogoBadge
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryContainer
import com.example.wasteco.ui.theme.PrimaryDarker
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.BorderSubtle
import com.example.wasteco.ui.theme.BrandSolid
import com.example.wasteco.ui.theme.SuccessTint
import com.example.wasteco.ui.theme.SurfaceSubtle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationDetailScreen(
    uiState: DonationDetailUiState,
    onBackClick: () -> Unit,
    onToggleItem: (String) -> Unit,
    onSubmitHandover: () -> Unit,
    onDismissHandoverSheet: () -> Unit,
    onReturnToDonationHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // S11A Modal Bottom Sheet (Bahan Siap Diserahkan)
    val handedOver = uiState.donation
    if (uiState.showHandoverSheet && handedOver != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissHandoverSheet,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = Radius.large, topEnd = Radius.large)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.large, vertical = Spacing.medium)
                    .padding(bottom = Spacing.xlarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Gift / Package Box Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SuccessTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = PrimaryMain,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.large))

                Text(
                    text = stringResource(R.string.donation_handover_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.compact))

                Text(
                    text = stringResource(
                        R.string.donation_handover_instructions,
                        handedOver.items.count { it.selected },
                        handedOver.user.name,
                        handedOver.meetingPoint,
                        handedOver.pickupDeadline
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                DonationDirectionsButton(handedOver)
                DonationWhatsAppButton(
                    url = handedOver.user.whatsappUrl,
                    label = stringResource(R.string.donation_contact_requester)
                )

                Spacer(modifier = Modifier.height(Spacing.xlarge))

                // Primary Button: "Kembali ke Donation Hub ->"
                Button(
                    onClick = onReturnToDonationHub,
                    shape = RoundedCornerShape(Radius.medium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandSolid,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Kembali ke Donation Hub",
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

                Spacer(modifier = Modifier.height(Spacing.small))

                // Text Button: "Tutup"
                TextButton(onClick = onDismissHandoverSheet) {
                    Text(
                        text = "Tutup",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
                            text = "Detail Permintaan",
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
            if (uiState.donation?.status == "open") {
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
                        val count = uiState.selectedItemIds.size
                        WasteCoPrimaryButton(
                            text = if (count > 0) "Serahkan $count Bahan Terpilih" else "Pilih Bahan yang Diserahkan",
                            onClick = onSubmitHandover,
                            enabled = count > 0 && !uiState.isSubmitting
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryMain)
            }
        } else if (uiState.donation == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.errorMessage ?: stringResource(R.string.donation_not_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val donation = uiState.donation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.large, vertical = Spacing.small)
            ) {
                uiState.errorMessage?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error)
                }
                // Card 1: Request Overview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(Radius.medium)),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
                ) {
                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(donationStatusLabel(donation.status)),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryMain
                            )
                            Text(
                                text = donation.createdTimeAgo,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        Text(
                            text = donation.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        if (donation.conditionNote.isNotBlank()) {
                            Text(
                                text = donation.conditionNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Card 2: Requester Profile & Location Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(Radius.medium)),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
                ) {
                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        // Requester Profile Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = donation.user.name.firstOrNull()?.uppercase() ?: "B",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarker
                                )
                            }

                            Spacer(modifier = Modifier.width(Spacing.compact))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = donation.user.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = PrimaryMain,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "Peminta • ${donation.user.areaName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.medium))

                        // Location Details Container
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Radius.small))
                                .background(SurfaceSubtle)
                                .padding(Spacing.medium),
                            verticalArrangement = Arrangement.spacedBy(Spacing.compact)
                        ) {
                            // Location row
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = PrimaryMain,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(Spacing.compact))
                                Column {
                                    Text(
                                        text = "Titik Lokasi Peminta",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarker
                                    )
                                    Text(
                                        text = donation.meetingPoint,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = donation.distanceDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Pickup Time row
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = PrimaryMain,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(Spacing.compact))
                                Column {
                                    Text(
                                        text = "Waktu Penerimaan",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDarker
                                    )
                                    Text(
                                        text = donation.pickupDeadline,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.medium))

                        DonationDirectionsButton(donation)
                        DonationWhatsAppButton(
                            url = donation.user.whatsappUrl,
                            label = stringResource(R.string.donation_contact_requester)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Card 3: "Pilih Bahan yang Diserahkan"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(Radius.medium)),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
                ) {
                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.compact))
                            Text(
                                text = "Pilih Bahan yang Diserahkan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.compact))

                        donation.items.forEach { item ->
                            val isChecked = uiState.selectedItemIds.contains(item.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(Radius.small))
                                    .clickable(enabled = donation.status == "open" && !uiState.isSubmitting) { onToggleItem(item.id) }
                                    .padding(vertical = Spacing.xxs),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { onToggleItem(item.id) },
                                    enabled = donation.status == "open" && !uiState.isSubmitting,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = BrandSolid,
                                        checkmarkColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(Spacing.xxs))
                                Text(
                                    text = "${item.name} — ${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xlarge))
            }
        }
    }
}
