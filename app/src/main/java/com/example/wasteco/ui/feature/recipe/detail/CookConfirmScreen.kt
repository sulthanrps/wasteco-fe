package com.example.wasteco.ui.feature.recipe.detail

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wasteco.domain.model.Recipe
import com.example.wasteco.domain.model.UsedIngredient
import com.example.wasteco.ui.components.WasteCoLogoBadge
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryContainer
import com.example.wasteco.ui.theme.PrimaryDarker
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.SecondaryMain
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.WasteCoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookConfirmScreen(
    uiState: CookConfirmUiState,
    onBackClick: () -> Unit,
    onPortionDelta: (Int) -> Unit,
    onIngredientDelta: (String, Double) -> Unit,
    onConfirmCooking: () -> Unit,
    onFinishAndGoHome: () -> Unit,
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
                            text = "Konfirmasi Memasak",
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
            if (!uiState.isCookRecorded && uiState.recipe != null) {
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
                            text = if (uiState.isConfirming) "Menyimpan..." else "Konfirmasi Selesai Memasak",
                            onClick = onConfirmCooking,
                            enabled = !uiState.isConfirming,
                            leadingIcon = if (uiState.isConfirming) null else Icons.Default.CheckCircle
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isCookRecorded) {
            // S08A — Masakan Tercatat Success State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(Spacing.xlarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryMain,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.large))

                Text(
                    text = "Masakan Berhasil Dicatat!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                Text(
                    text = "Hebat! Kamu berhasil menghemat Rp ${uiState.totalEstimatedSavings} dan mencegah sisa makanan terbuang ke TPA.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.xlarge))

                WasteCoPrimaryButton(
                    text = "Kembali ke Beranda",
                    onClick = onFinishAndGoHome
                )
            }
        } else if (uiState.recipe == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryMain)
            }
        } else {
            val recipe = uiState.recipe
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.large, vertical = Spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Checkmark Badge with Orange Leaf Dot
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(PrimaryMain),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Orange Leaf Dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(SecondaryMain),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Question Title
                Text(
                    text = "Bahan apa saja yang sudah\nkamu gunakan?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.large))

                // Recipe Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium)),
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
                        AsyncImage(
                            model = recipe.imageUrl ?: "https://images.unsplash.com/photo-1603133872878-684f208fb84b?auto=format&fit=crop&w=800&q=80",
                            contentDescription = recipe.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(Radius.small))
                        )

                        Spacer(modifier = Modifier.width(Spacing.medium))

                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = PrimaryMain,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Resep Terlaksana",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryDarker
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.xxs))

                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Target racikan: ${uiState.cookedPortions} porsi santapan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.large))

                // Section: Bahan Terpakai
                Text(
                    text = "Bahan Terpakai",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.compact))

                // Ingredient Stepper Cards
                recipe.usedIngredients.forEach { ing ->
                    val currentQty = uiState.ingredientQuantities[ing.ingredientId] ?: ing.quantity
                    val qtyDisplay = if (currentQty % 1.0 == 0.0) {
                        currentQty.toInt().toString()
                    } else {
                        currentQty.toString()
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xxs)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium)),
                        shape = RoundedCornerShape(Radius.medium),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.medium, vertical = Spacing.compact),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(getIngredientBgColor(ing.name)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getIngredientIcon(ing.name),
                                        contentDescription = null,
                                        tint = getIngredientTint(ing.name),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(Spacing.compact))
                                Text(
                                    text = ing.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Stepper Pill
                            StepperPill(
                                displayValue = "$qtyDisplay ${ing.unit}",
                                onMinus = { onIngredientDelta(ing.ingredientId, -1.0) },
                                onPlus = { onIngredientDelta(ing.ingredientId, 1.0) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Portions Produced Stepper Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(Radius.medium)),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium, vertical = Spacing.compact),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = PrimaryMain,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(Spacing.compact))
                            Column {
                                Text(
                                    text = "Porsi Matang",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Dihasilkan",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Servings Stepper Pill
                        StepperPill(
                            displayValue = "${uiState.cookedPortions} porsi",
                            onMinus = { onPortionDelta(-1) },
                            onPlus = { onPortionDelta(1) }
                        )
                    }
                }

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xlarge))
            }
        }
    }
}

@Composable
private fun StepperPill(
    displayValue: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF1F4F1))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minus Button
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onMinus() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Kurang",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = displayValue,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = Spacing.compact)
        )

        // Plus Button
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onPlus() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun getIngredientIcon(name: String): ImageVector {
    val lower = name.lowercase()
    return when {
        "nasi" in lower || "beras" in lower -> Icons.Default.RiceBowl
        "telur" in lower || "minyak" in lower -> Icons.Default.Egg
        "wortel" in lower || "sayur" in lower || "bayam" in lower -> Icons.Default.Grass
        else -> Icons.Default.Restaurant
    }
}

private fun getIngredientBgColor(name: String): Color {
    val lower = name.lowercase()
    return when {
        "nasi" in lower -> Color(0xFFE8F5E9)
        "telur" in lower -> Color(0xFFFFF3E0)
        "wortel" in lower -> Color(0xFFFFEBE5)
        else -> Color(0xFFF1F4F1)
    }
}

private fun getIngredientTint(name: String): Color {
    val lower = name.lowercase()
    return when {
        "nasi" in lower -> PrimaryMain
        "telur" in lower -> SecondaryMain
        "wortel" in lower -> Color(0xFFE87A24)
        else -> PrimaryMain
    }
}

@Preview(showBackground = true)
@Composable
private fun CookConfirmScreenPreview() {
    WasteCoTheme {
        CookConfirmScreen(
            uiState = CookConfirmUiState(
                recipe = Recipe(
                    id = "1",
                    name = "Nasi Goreng Wortel Telur",
                    description = "Resep zero waste.",
                    cookingTimeMinutes = 15,
                    difficulty = "Mudah",
                    servings = 2,
                    estimatedSavings = 25000,
                    usedIngredients = listOf(
                        UsedIngredient("1", "Nasi Putih", 2.0, "porsi", 6000),
                        UsedIngredient("2", "Telur Ayam", 2.0, "butir", 4000),
                        UsedIngredient("3", "Wortel", 100.0, "gram", 2000)
                    ),
                    additionalIngredients = emptyList(),
                    instructions = emptyList()
                ),
                cookedPortions = 2,
                ingredientQuantities = mapOf("1" to 2.0, "2" to 2.0, "3" to 100.0)
            ),
            onBackClick = {},
            onPortionDelta = {},
            onIngredientDelta = { _, _ -> },
            onConfirmCooking = {},
            onFinishAndGoHome = {}
        )
    }
}
