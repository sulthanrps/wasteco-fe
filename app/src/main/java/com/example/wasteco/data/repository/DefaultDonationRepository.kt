package com.example.wasteco.data.repository

import com.example.wasteco.data.mapper.toDomain
import com.example.wasteco.data.remote.api.DonationApiService
import com.example.wasteco.data.remote.dto.ErrorResponse
import com.example.wasteco.domain.model.Donation
import com.example.wasteco.domain.model.DonationItem
import com.example.wasteco.domain.model.DonationUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class DefaultDonationRepository(
    private val donationApiService: DonationApiService,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : DonationRepository {

    private val donationCache = ConcurrentHashMap<String, Donation>()

    init {
        seedSampleDonations()
    }

    private fun seedSampleDonations() {
        val sample1 = Donation(
            id = "don_1",
            title = "Wortel & Telur",
            category = "Sayuran & Protein",
            itemsSummary = "Wortel • Telur • Nasi",
            items = listOf(
                DonationItem("Wortel", "500 gram"),
                DonationItem("Telur Ayam", "4 butir"),
                DonationItem("Nasi Putih", "2 porsi")
            ),
            conditionNote = "Untuk masak makan malam keluarga. Bantu sebagian item juga boleh.",
            storageMethod = "Segar",
            pickupDeadline = "Hari ini, 16.00 – 19.00",
            areaName = "Dinoyo, Malang",
            meetingPoint = "Depan Minimarket Dinoyo\nJl. MT Haryono • 800 m dari lokasimu",
            latitude = -7.9535,
            longitude = 112.608,
            distanceDesc = "800 m",
            whatsappNumber = "+6281358902144",
            estimatedPrice = 12000,
            estimatedPortions = 3,
            photoUrl = "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&w=600&q=80",
            status = "active",
            step = 1,
            createdTimeAgo = "Diminta 1 jam lalu",
            user = DonationUser("u1", "Budi Santoso", "Warga Dinoyo", null, true),
            donor = null
        )

        val sample2 = Donation(
            id = "don_2",
            title = "Kentang",
            category = "Umbi-umbian",
            itemsSummary = "Kentang 1kg",
            items = listOf(
                DonationItem("Kentang", "1 kg")
            ),
            conditionNote = "Butuh kentang untuk sop keluarga.",
            storageMethod = "Suhu ruang",
            pickupDeadline = "Hari ini, 17.00 – 20.00",
            areaName = "Dinoyo, Malang",
            meetingPoint = "Pos Ronda RW 03 Dinoyo",
            latitude = -7.9570,
            longitude = 112.610,
            distanceDesc = "1.2 km",
            whatsappNumber = "+6281234567891",
            estimatedPrice = 15000,
            estimatedPortions = 1,
            photoUrl = "https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&w=600&q=80",
            status = "active",
            step = 1,
            createdTimeAgo = "Diminta 3 jam lalu",
            user = DonationUser("u2", "Siti A.", "Warga Dinoyo", null, true),
            donor = null
        )

        val sample3 = Donation(
            id = "don_3",
            title = "Tomat & Bumbu",
            category = "Bumbu Dapur",
            itemsSummary = "Tomat • Bawang • Cabai • +2",
            items = listOf(
                DonationItem("Tomat", "500 gram"),
                DonationItem("Bawang Merah", "250 gram"),
                DonationItem("Cabai Rawit", "100 gram")
            ),
            conditionNote = "Bumbu pelengkap sambal untuk warung makan berkah.",
            storageMethod = "Suhu ruang",
            pickupDeadline = "Hari ini, 15.00 – 18.00",
            areaName = "Dinoyo, Malang",
            meetingPoint = "Gang 5 Dinoyo No 12",
            latitude = -7.9510,
            longitude = 112.605,
            distanceDesc = "1.8 km",
            whatsappNumber = "+6281234567892",
            estimatedPrice = 20000,
            estimatedPortions = 6,
            photoUrl = "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=600&q=80",
            status = "active",
            step = 1,
            createdTimeAgo = "Diminta 5 jam lalu",
            user = DonationUser("u3", "Dimas R.", "Warga Dinoyo", null, false),
            donor = null
        )

        val sampleMy = Donation(
            id = "my_don_1",
            title = "Wortel & Bawang",
            category = "Sayuran & Bumbu",
            itemsSummary = "2 item • Wortel 500 g, Bawang merah 250 g",
            items = listOf(
                DonationItem("Wortel", "500 g"),
                DonationItem("Bawang Merah", "250 g")
            ),
            conditionNote = "Untuk masak sup sore nanti.",
            storageMethod = "Segar",
            pickupDeadline = "Hari ini, 16.00 – 19.00",
            areaName = "Dinoyo, Malang",
            meetingPoint = "Depan Minimarket Dinoyo",
            latitude = -7.9535,
            longitude = 112.608,
            distanceDesc = "Titik temu: Depan Minimarket",
            whatsappNumber = "+6281358902144",
            estimatedPrice = 15000,
            estimatedPortions = 2,
            photoUrl = "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&w=600&q=80",
            status = "in_progress",
            step = 2,
            createdTimeAgo = "3 jam lalu",
            user = DonationUser("my_id", "Affan Maulana", "Warga Dinoyo", null, true),
            donor = DonationUser("donor_1", "Siti Aminah", "Warga Dinoyo", null, true)
        )

        val sampleGiven = Donation(
            id = "given_don_1",
            title = "Tomat & Cabai Segar",
            category = "Sayuran & Bumbu",
            itemsSummary = "2 item • Tomat 500 g, Cabai 100 g",
            items = listOf(
                DonationItem("Tomat", "500 g"),
                DonationItem("Cabai Rawit", "100 g")
            ),
            conditionNote = "Bahan surplus segar dari kebun rumah, siap dibagikan.",
            storageMethod = "Segar",
            pickupDeadline = "Hari ini, 17.00 – 19.30",
            areaName = "Dinoyo, Malang",
            meetingPoint = "Pos Ronda RW 03 Dinoyo (Dekat Masjid)",
            latitude = -7.9520,
            longitude = 112.607,
            distanceDesc = "Titik temu: Pos Ronda RW 03",
            whatsappNumber = "+6281234567890",
            estimatedPrice = 18000,
            estimatedPortions = 4,
            photoUrl = "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=600&q=80",
            status = "in_progress",
            step = 2,
            createdTimeAgo = "1 jam lalu",
            user = DonationUser("u_penerima_1", "Ibu Rahmawati", "Warga Dinoyo", null, true),
            donor = DonationUser("my_id", "Affan Maulana", "Warga Dinoyo", null, true)
        )

        donationCache[sample1.id] = sample1
        donationCache[sample2.id] = sample2
        donationCache[sample3.id] = sample3
        donationCache[sampleMy.id] = sampleMy
        donationCache[sampleGiven.id] = sampleGiven
    }

    override suspend fun getNearbyDonations(
        latitude: Double?,
        longitude: Double?,
        radius: Int?,
        category: String?,
        search: String?
    ): Result<List<Donation>> = withContext(Dispatchers.IO) {
        try {
            val response = donationApiService.getNearbyDonations(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                category = category,
                search = search
            )
            if (response.isSuccessful && response.body() != null) {
                val apiDonations = response.body()!!.payload.donations.map { it.toDomain() }
                if (apiDonations.isNotEmpty()) {
                    apiDonations.forEach { donationCache[it.id] = it }
                    Result.success(apiDonations)
                } else {
                    val cachedNearby = donationCache.values.filter { !it.id.startsWith("my_") }
                    Result.success(cachedNearby)
                }
            } else {
                val cachedNearby = donationCache.values.filter { !it.id.startsWith("my_") }
                Result.success(cachedNearby)
            }
        } catch (_: Exception) {
            val cachedNearby = donationCache.values.filter { !it.id.startsWith("my_") }
            Result.success(cachedNearby)
        }
    }

    override suspend fun getMyDonations(): Result<List<Donation>> = withContext(Dispatchers.IO) {
        try {
            val response = donationApiService.getMyDonations()
            if (response.isSuccessful && response.body() != null) {
                val myDonations = response.body()!!.payload.donations.map { it.toDomain() }
                if (myDonations.isNotEmpty()) {
                    myDonations.forEach { donationCache[it.id] = it }
                    Result.success(myDonations)
                } else {
                    val cachedMy = donationCache.values.filter { it.id.startsWith("my_") }
                    Result.success(cachedMy)
                }
            } else {
                val cachedMy = donationCache.values.filter { it.id.startsWith("my_") }
                Result.success(cachedMy)
            }
        } catch (_: Exception) {
            val cachedMy = donationCache.values.filter { it.id.startsWith("my_") }
            Result.success(cachedMy)
        }
    }

    override suspend fun getGivenDonations(): Result<List<Donation>> = withContext(Dispatchers.IO) {
        val given = donationCache.values.filter {
            it.donor?.id == "my_id" || it.id.startsWith("given_")
        }.sortedByDescending { it.id }
        Result.success(given)
    }

    override suspend fun getDonationDetail(id: String): Result<Donation> = withContext(Dispatchers.IO) {
        val cached = donationCache[id]
        if (cached != null) {
            return@withContext Result.success(cached)
        }
        try {
            val response = donationApiService.getDonationDetail(id)
            if (response.isSuccessful && response.body() != null) {
                val donation = response.body()!!.payload.donation.toDomain()
                donationCache[donation.id] = donation
                Result.success(donation)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal memuat detail permintaan"))
        }
    }

    override suspend fun createDonation(
        title: String,
        category: String,
        quantity: Double,
        unit: String,
        conditionNote: String,
        storageMethod: String,
        pickupDeadline: String,
        areaName: String,
        meetingPoint: String,
        latitude: Double,
        longitude: Double,
        whatsappNumber: String,
        estimatedPrice: Int,
        estimatedPortions: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val textPlain = "text/plain".toMediaTypeOrNull()
            val partMap = mapOf(
                "title" to title.toRequestBody(textPlain),
                "category" to category.toRequestBody(textPlain),
                "quantity" to quantity.toString().toRequestBody(textPlain),
                "unit" to unit.toRequestBody(textPlain),
                "condition_note" to conditionNote.toRequestBody(textPlain),
                "storage_method" to storageMethod.toRequestBody(textPlain),
                "pickup_deadline" to pickupDeadline.toRequestBody(textPlain),
                "area_name" to areaName.toRequestBody(textPlain),
                "meeting_point" to meetingPoint.toRequestBody(textPlain),
                "latitude" to latitude.toString().toRequestBody(textPlain),
                "longitude" to longitude.toString().toRequestBody(textPlain),
                "whatsapp_number" to whatsappNumber.toRequestBody(textPlain),
                "estimated_price" to estimatedPrice.toString().toRequestBody(textPlain),
                "estimated_portions" to estimatedPortions.toString().toRequestBody(textPlain)
            )

            val newId = "my_don_${UUID.randomUUID()}"
            val newDonation = Donation(
                id = newId,
                title = title,
                category = category,
                itemsSummary = "$quantity $unit $title",
                items = listOf(DonationItem(title, "$quantity $unit")),
                conditionNote = conditionNote,
                storageMethod = storageMethod,
                pickupDeadline = pickupDeadline,
                areaName = areaName,
                meetingPoint = meetingPoint,
                latitude = latitude,
                longitude = longitude,
                distanceDesc = "Titik temu: $meetingPoint",
                whatsappNumber = whatsappNumber,
                estimatedPrice = estimatedPrice,
                estimatedPortions = estimatedPortions,
                photoUrl = "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&w=600&q=80",
                status = "active",
                step = 1,
                createdTimeAgo = "Baru saja",
                user = DonationUser("my_id", "Affan Maulana", areaName, null, true),
                donor = null
            )
            donationCache[newId] = newDonation

            val response = donationApiService.createDonation(partMap, null)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun confirmHandover(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cached = donationCache[id]
        if (cached != null) {
            val updated = if (cached.donor == null) {
                cached.copy(
                    status = "in_progress",
                    step = 2,
                    donor = DonationUser("my_id", "Affan Maulana", "Warga Dinoyo", null, true)
                )
            } else {
                cached.copy(status = "completed", step = 3)
            }
            donationCache[id] = updated
        }
        try {
            val response = donationApiService.confirmHandover(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun cancelDonation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        donationCache.remove(id)
        try {
            val response = donationApiService.cancelDonation(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }

    override fun getCachedDonation(id: String): Donation? {
        return donationCache[id]
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
