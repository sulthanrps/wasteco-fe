package com.example.wasteco.ui.feature.recipe

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wasteco.domain.model.Recipe
import com.example.wasteco.domain.model.UsedIngredient
import com.example.wasteco.ui.components.WasteCoLogoBadge
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryContainer
import com.example.wasteco.ui.theme.PrimaryDarker
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.SecondaryDark
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.WasteCoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    uiState: RecipeListUiState,
    onBackClick: () -> Unit,
    onRecipeClick: (String) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WasteCoLogoBadge(size = 32.dp)
                        Spacer(modifier = Modifier.width(Spacing.compact))
                        Text(
                            text = "Rekomendasi Resep",
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
                actions = {
                    IconButton(onClick = onRetryClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Muat Ulang"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
            // Header Title
            Text(
                text = "Rekomendasi Gemini AI",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 22.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.xxs))

            Text(
                text = "Diurutkan dari yang paling cocok dengan bahanmu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            if (uiState.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.xlarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PrimaryMain)
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    Text(
                        text = "Gemini AI Sedang Meracik Resep Zero-Waste...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (!uiState.errorMessage.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.xlarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(Spacing.large))
                    WasteCoPrimaryButton(text = "Coba Lagi", onClick = onRetryClick)
                }
            } else {
                // If backend returned empty or for showcase, fallback to rich items matching Figma S06
                val displayRecipes = if (uiState.recipes.isNotEmpty()) uiState.recipes else sampleSmartRecipes

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.large)) {
                    displayRecipes.forEachIndexed { index, recipe ->
                        SmartRecipeFigmaCard(
                            recipe = recipe,
                            isTopMatch = index == 0,
                            availableCount = if (index == 0) 3 else if (index == 1) 2 else 1,
                            totalNeeded = if (index == 0) 5 else 4,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmartRecipeFigmaCard(
    recipe: Recipe,
    isTopMatch: Boolean,
    availableCount: Int,
    totalNeeded: Int,
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
        Column(modifier = Modifier.fillMaxWidth()) {
            // Food Image with Floating Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(PrimaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.SoupKitchen,
                    contentDescription = null,
                    tint = PrimaryMain,
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.Center)
                )

                // Top-left floating availability pill badge
                Box(
                    modifier = Modifier
                        .padding(Spacing.medium)
                        .clip(RoundedCornerShape(Radius.full))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = Spacing.compact, vertical = Spacing.xs)
                        .align(Alignment.TopStart)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (isTopMatch) PrimaryDarker else SecondaryDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = "$availableCount dari $totalNeeded bahan tersedia",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.large)
            ) {
                // Title & "PALING COCOK" badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    if (isTopMatch) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Radius.small))
                                .background(PrimaryContainer)
                                .padding(horizontal = Spacing.compact, vertical = Spacing.xxs)
                        ) {
                            Text(
                                text = "PALING COCOK",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarker
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Meta Row: Clock 15 menit • People 2 porsi • Pot Mudah
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = SecondaryDark, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(Spacing.xxs))
                        Text(text = "${recipe.cookingTimeMinutes} menit", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = null, tint = SecondaryDark, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(Spacing.xxs))
                        Text(text = "${recipe.servings} porsi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = PrimaryMain, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(Spacing.xxs))
                        Text(text = recipe.difficulty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Gray Container: "Ada di dapurmu" & "Perlu Ditambahkan"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.medium)
                    ) {
                        // Ada di dapurmu
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryDarker, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(Spacing.small))
                            val pantryNames = if (recipe.usedIngredients.isNotEmpty()) recipe.usedIngredients.joinToString(", ") { it.name } else "Nasi, Telur, Wortel"
                            Text(
                                text = "Ada di dapurmu: $pantryNames",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        // Perlu Ditambahkan
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(Spacing.small))
                            val addNames = if (recipe.additionalIngredients.isNotEmpty()) recipe.additionalIngredients.joinToString(", ") else "Bawang putih, Minyak goreng"
                            Text(
                                text = "Perlu Ditambahkan: $addNames",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Radius.small))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                            ) {
                                Text(
                                    text = "Terdeteksi AI",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Button "Lihat Resep ->"
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(Radius.medium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTopMatch) PrimaryDarker else PrimaryContainer,
                        contentColor = if (isTopMatch) Color.White else PrimaryDarker
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = "Lihat Resep",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

val sampleSmartRecipes = listOf(
    Recipe(
        id = "recipe_nasi_goreng",
        name = "Nasi Goreng Wortel Telur",
        description = "Nasi goreng kampung spesial memanfaatkan nasi sisa dan wortel.",
        cookingTimeMinutes = 15,
        difficulty = "Mudah",
        servings = 2,
        estimatedSavings = 25000,
        usedIngredients = listOf(
            UsedIngredient("1", "Nasi Putih", 2.0, "porsi", 5000),
            UsedIngredient("2", "Telur Ayam", 2.0, "butir", 4000),
            UsedIngredient("3", "Wortel (dadu kecil)", 100.0, "gram", 2000)
        ),
        additionalIngredients = listOf("Bawang Putih (3 siung)", "Minyak & Garam (secukupnya)"),
        instructions = listOf(
            "Panaskan 2 sdm minyak di wajan dengan api sedang. Tumis bawang putih cincang hingga harum kekuningan.",
            "Masukkan wortel yang sudah dipotong dadu kecil. Tumis sekitar 2 menit hingga mulai layu dan empuk.",
            "Sisihkan sayuran ke tepi wajan, pecahkan 2 butir telur lalu buat orak-arik cepat hingga matang berserabut.",
            "Masukkan 2 porsi nasi dingin. Aduk rata dengan api sedang-tinggi. Tambahkan garam dan penyedap secukupnya, masak hingga bumbu meresap rata.",
            "Angkat dan sajikan hangat untuk 2 porsi."
        ),
        zeroWasteTips = "Gunakan nasi yang sudah didinginkan di kulkas semalaman agar bulir nasi tidak lembek saat digoreng."
    ),
    Recipe(
        id = "recipe_orak_arik",
        name = "Orak-Arik Telur Wortel Gurih",
        description = "Tumisan telur dan wortel yang praktis untuk sarapan lezat.",
        cookingTimeMinutes = 10,
        difficulty = "Praktis",
        servings = 2,
        estimatedSavings = 18000,
        usedIngredients = listOf(
            UsedIngredient("1", "Telur Ayam", 2.0, "butir", 4000),
            UsedIngredient("2", "Wortel", 1.0, "buah", 2000)
        ),
        additionalIngredients = listOf("Bawang bombay", "Merica bubuk"),
        instructions = listOf(
            "Iris tipis wortel dan bawang bombay.",
            "Tumis bawang bombay hingga harum, masukkan wortel.",
            "Kocok telur lalu tuangkan ke dalam wajan, aduk perlahan hingga matang."
        )
    ),
    Recipe(
        id = "recipe_sup_sayur",
        name = "Sup Sayur Telur Puyuh & Wortel",
        description = "Sup kuah bening gurih menyegarkan dengan sayuran bergizi.",
        cookingTimeMinutes = 20,
        difficulty = "Segar",
        servings = 3,
        estimatedSavings = 22000,
        usedIngredients = listOf(
            UsedIngredient("1", "Wortel", 2.0, "buah", 4000)
        ),
        additionalIngredients = listOf("Telur puyuh", "Daun seledri", "Bawang merah"),
        instructions = listOf(
            "Didihkan air kaldu dalam panci.",
            "Masukkan potongan wortel dan telur puyuh.",
            "Bumbui dengan garam dan bawang goreng, taburi daun seledri sebelum diangkat."
        )
    )
)

@Preview(showBackground = true)
@Composable
private fun RecipeListScreenPreview() {
    WasteCoTheme {
        RecipeListScreen(
            uiState = RecipeListUiState(recipes = sampleSmartRecipes),
            onBackClick = {},
            onRecipeClick = {},
            onRetryClick = {}
        )
    }
}
