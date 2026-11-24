package com.example.wasteco.ui.feature.pantry

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wasteco.domain.model.PantryItem
import com.example.wasteco.ui.components.BottomTab
import com.example.wasteco.ui.components.WasteCoBottomBar
import com.example.wasteco.ui.components.WasteCoLogoBadge
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.components.WasteCoTextField
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryContainer
import com.example.wasteco.ui.theme.PrimaryDarker
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.WasteCoTheme
import com.example.wasteco.ui.theme.BrandSolid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    uiState: PantryUiState,
    onHomeTabClick: () -> Unit,
    onDonationsTabClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCameraClick: () -> Unit,
    onAddConfirm: (name: String, quantity: Double, unit: String, category: String, storageLocation: String, expiryDate: String, estimatedPrice: Int) -> Unit,
    onDeleteIngredient: (String) -> Unit,
    onGenerateRecipesClick: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPhotoBanner by remember { mutableStateOf(false) }
    var manualName by remember { mutableStateOf("") }
    var manualQty by remember { mutableStateOf("50") }
    var manualUnit by remember { mutableStateOf("gram") }
    var unitDropdownExpanded by remember { mutableStateOf(false) }

    val unitOptions = listOf("gram", "kg", "porsi", "buah", "butir", "ikat", "siung", "sdm", "lembar")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WasteCoLogoBadge(size = 36.dp)
                        Spacer(modifier = Modifier.width(Spacing.compact))
                        Text(
                            text = "Zero-Waste Recipe",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifikasi",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .padding(end = Spacing.small)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandSolid)
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCameraClick,
                containerColor = BrandSolid,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = if (uiState.items.isNotEmpty()) 60.dp else 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Foto Bahan Sisa",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        bottomBar = {
            Column {
                // Sticky "Cari Resep" Button (if items available - S05C)
                if (uiState.items.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = Spacing.large, vertical = Spacing.small)
                    ) {
                        Button(
                            onClick = {
                                onGenerateRecipesClick(uiState.items.map { it.id })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(Radius.large),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandSolid,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Cari Resep",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom Navigation Bar with 3 tabs
                WasteCoBottomBar(
                    currentTab = BottomTab.RECIPE,
                    onTabSelected = { tab ->
                        when (tab) {
                            BottomTab.HOME -> onHomeTabClick()
                            BottomTab.RECIPE -> { /* Already here */ }
                            BottomTab.DONATIONS -> onDonationsTabClick()
                        }
                    }
                )
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
            // Notice Banner "DARI FOTO" (S05C)
            if (showPhotoBanner && uiState.items.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.medium),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium, vertical = Spacing.compact),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BrandSolid),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.compact))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DARI FOTO",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarker
                            )
                            Text(
                                text = "${uiState.items.size} bahan berhasil ditambahkan ke daftar!",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = { showPhotoBanner = false }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Header Title
            Text(
                text = "Bahan sisa di dapurmu",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 22.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.xxs))

            Text(
                text = "Foto bahan sisamu, atau isi manual beserta harga satuannya.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // Section "Bahan Sisa Tersedia"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bahan Sisa Tersedia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "${uiState.items.size} bahan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // State S05: Empty
            if (uiState.items.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(Radius.large)
                        ),
                    shape = RoundedCornerShape(Radius.large),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(52.dp)
                        )

                        Spacer(modifier = Modifier.height(Spacing.small))

                        Text(
                            text = "Bahan sisa belum terdaftar,\nAmbil foto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(Spacing.medium))

                        Button(
                            onClick = onCameraClick,
                            shape = RoundedCornerShape(Radius.medium),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandSolid,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.compact))
                            Text(
                                text = "Foto Bahan Sisa",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.small))

                        Text(
                            text = "atau daftarkan sendiri melalui form dibawah ini",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // State S05C: List of available ingredients
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.compact)) {
                    uiState.items.forEachIndexed { index, item ->
                        PantryIngredientRowItem(
                            item = item,
                            isNew = index < 3,
                            onDelete = { onDeleteIngredient(item.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Card: "Tambah Bahan Sisa" (Isi Manual)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.large),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.large)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(Radius.small))
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = PrimaryMain,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.compact))
                        Text(
                            text = "Tambah Bahan Sisa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.compact))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "  Isi Manual  ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(Spacing.medium))

                    // Nama Bahan
                    Text(
                        text = "Nama Bahan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    OutlinedTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        placeholder = { Text("contoh : Bawang Merah", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Radius.medium),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryMain,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(Spacing.compact))

                    // Row: Jumlah & Satuan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Jumlah",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(Spacing.xxs))
                            OutlinedTextField(
                                value = manualQty,
                                onValueChange = { manualQty = it },
                                placeholder = { Text("50") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(Radius.medium),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryMain,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Satuan",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(Spacing.xxs))
                            ExposedDropdownMenuBox(
                                expanded = unitDropdownExpanded,
                                onExpandedChange = { unitDropdownExpanded = !unitDropdownExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = manualUnit,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(Radius.medium),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryMain,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = unitDropdownExpanded,
                                    onDismissRequest = { unitDropdownExpanded = false }
                                ) {
                                    unitOptions.forEach { unitOpt ->
                                        DropdownMenuItem(
                                            text = { Text(unitOpt) },
                                            onClick = {
                                                manualUnit = unitOpt
                                                unitDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.large))

                    // Button: Simpan Bahan Sisa (Light green button)
                    Button(
                        onClick = {
                            if (manualName.isNotBlank()) {
                                val qty = manualQty.toDoubleOrNull() ?: 1.0
                                onAddConfirm(manualName.trim(), qty, manualUnit, "Lainnya", "Kulkas", "2026-11-25", 5000)
                                manualName = ""
                                manualQty = "50"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(Radius.medium),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = PrimaryDarker
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryDarker,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.compact))
                        Text(
                            text = "Simpan Bahan Sisa",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarker
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun PantryIngredientRowItem(
    item: PantryItem,
    isNew: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
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
            // Food Thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(Radius.medium))
                    .background(PrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = PrimaryMain,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            // Details: Name, Quantity, Price
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                val priceDesc = if (item.estimatedPrice > 0) " • Rp${item.estimatedPrice}" else ""
                Text(
                    text = "${item.displayQuantity}$priceDesc",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right Action: "Baru" Pill Badge and 3-dot overflow menu
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isNew) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.small))
                            .background(PrimaryContainer)
                            .padding(horizontal = Spacing.compact, vertical = Spacing.xxs)
                    ) {
                        Text(
                            text = "Baru",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarker
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.xs))
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opsi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    androidx.compose.material3.DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Hapus Bahan", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PantryScreenPreview() {
    WasteCoTheme {
        PantryScreen(
            uiState = PantryUiState(),
            onHomeTabClick = {},
            onDonationsTabClick = {},
            onProfileClick = {},
            onCameraClick = {},
            onAddConfirm = { _, _, _, _, _, _, _ -> },
            onDeleteIngredient = {},
            onGenerateRecipesClick = {}
        )
    }
}
