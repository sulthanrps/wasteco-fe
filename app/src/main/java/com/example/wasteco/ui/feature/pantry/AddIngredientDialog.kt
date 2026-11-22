package com.example.wasteco.ui.feature.pantry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.components.WasteCoSecondaryButton
import com.example.wasteco.ui.components.WasteCoTextField
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing

@Composable
fun AddIngredientDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, quantity: Double, unit: String, category: String, storageLocation: String, expiryDate: String, estimatedPrice: Int) -> Unit,
    isAdding: Boolean = false,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("gram") }
    var category by remember { mutableStateOf("Sayuran") }
    var storageLocation by remember { mutableStateOf("Kulkas") }
    var expiryDate by remember { mutableStateOf("2026-11-25") }
    var estimatedPriceText by remember { mutableStateOf("5000") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.large),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.large)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Tambah Bahan ke Pantry",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.medium))

                WasteCoTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) "Nama bahan wajib diisi" else null
                    },
                    label = "Nama Bahan",
                    placeholder = "Misal: Wortel, Nasi sisa, Telur",
                    isError = nameError != null,
                    errorMessage = nameError
                )

                Spacer(modifier = Modifier.height(Spacing.compact))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    WasteCoTextField(
                        value = quantityText,
                        onValueChange = {
                            quantityText = it
                            quantityError = if (it.isBlank()) "Wajib diisi" else null
                        },
                        label = "Jumlah",
                        placeholder = "250",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = quantityError != null,
                        errorMessage = quantityError,
                        modifier = Modifier.weight(1f)
                    )

                    WasteCoTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = "Satuan",
                        placeholder = "gram / porsi",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.compact))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    WasteCoTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = "Kategori",
                        placeholder = "Sayuran",
                        modifier = Modifier.weight(1f)
                    )

                    WasteCoTextField(
                        value = storageLocation,
                        onValueChange = { storageLocation = it },
                        label = "Lokasi",
                        placeholder = "Kulkas",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.compact))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    WasteCoTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = "Tgl Kadaluarsa",
                        placeholder = "YYYY-MM-DD",
                        modifier = Modifier.weight(1f)
                    )

                    WasteCoTextField(
                        value = estimatedPriceText,
                        onValueChange = { estimatedPriceText = it },
                        label = "Estimasi Harga (Rp)",
                        placeholder = "5000",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.large))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WasteCoSecondaryButton(
                        text = "Batal",
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(Spacing.compact))
                    WasteCoPrimaryButton(
                        text = "Simpan",
                        onClick = {
                            if (name.isBlank()) {
                                nameError = "Nama bahan wajib diisi"
                                return@WasteCoPrimaryButton
                            }
                            val qty = quantityText.toDoubleOrNull() ?: 1.0
                            val price = estimatedPriceText.toIntOrNull() ?: 0
                            onConfirm(name.trim(), qty, unit.trim(), category.trim(), storageLocation.trim(), expiryDate.trim(), price)
                        },
                        isLoading = isAdding,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
