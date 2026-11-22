package com.example.wasteco.data.repository

import com.example.wasteco.data.mapper.toDomain
import com.example.wasteco.data.remote.api.PantryApiService
import com.example.wasteco.data.remote.dto.AddPantryRequest
import com.example.wasteco.data.remote.dto.ErrorResponse
import com.example.wasteco.domain.model.PantryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException

class DefaultPantryRepository(
    private val pantryApiService: PantryApiService,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : PantryRepository {

    override suspend fun getPantryIngredients(): Result<List<PantryItem>> = withContext(Dispatchers.IO) {
        try {
            val response = pantryApiService.getPantryIngredients()
            if (response.isSuccessful && response.body() != null) {
                val items = response.body()!!.payload.ingredients.map { it.toDomain() }
                Result.success(items)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Koneksi gagal saat mengambil data pantry."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan pada server"))
        }
    }

    override suspend fun addPantryIngredient(
        name: String,
        quantity: Double,
        unit: String,
        category: String,
        storageLocation: String,
        expiryDate: String,
        estimatedPrice: Int
    ): Result<PantryItem> = withContext(Dispatchers.IO) {
        try {
            val request = AddPantryRequest(
                name = name,
                quantity = quantity,
                unit = unit,
                category = category,
                storageLocation = storageLocation,
                expiryDate = expiryDate,
                estimatedPrice = estimatedPrice
            )
            val response = pantryApiService.addPantryIngredient(request)
            if (response.isSuccessful && response.body() != null) {
                val item = response.body()!!.payload.ingredient.toDomain()
                Result.success(item)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Koneksi gagal saat menambahkan bahan."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan pada server"))
        }
    }

    override suspend fun deletePantryIngredient(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = pantryApiService.deletePantryIngredient(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Koneksi gagal saat menghapus bahan."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan pada server"))
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Terjadi kesalahan pada server"
        return try {
            val errorResponse = json.decodeFromString<ErrorResponse>(errorBody)
            errorResponse.message ?: errorResponse.error ?: "Terjadi kesalahan"
        } catch (_: Exception) {
            "Terjadi kesalahan respon dari server"
        }
    }
}
