package com.example.wasteco.data.repository

import com.example.wasteco.data.mapper.toDomain
import com.example.wasteco.data.remote.api.DonationApiService
import com.example.wasteco.data.remote.dto.CreateDonationRequest
import com.example.wasteco.data.remote.dto.DonationRequestItemDto
import com.example.wasteco.data.remote.dto.ErrorResponse
import com.example.wasteco.data.remote.dto.FulfillDonationRequest
import com.example.wasteco.domain.model.DonationRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.Response

class DefaultDonationRepository(
    private val donationApiService: DonationApiService,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : DonationRepository {
    override suspend fun getNearbyDonations(
        latitude: Double?, longitude: Double?, radius: Int, search: String?
    ) = request {
        donationApiService.getNearbyDonations(latitude, longitude, radius, search)
            .bodyOrThrow().payload.donations.map { it.toDomain() }
    }

    override suspend fun getMyDonations() = getActivity("requester")

    override suspend fun getGivenDonations() = getActivity("donor")

    private suspend fun getActivity(role: String) = request {
        val payload = donationApiService.getMyDonations(role).bodyOrThrow().payload
        (payload.active + payload.history).map { it.toDomain() }
    }

    override suspend fun getDonationDetail(id: String, latitude: Double?, longitude: Double?) = request {
        donationApiService.getDonationDetail(id, latitude, longitude)
            .bodyOrThrow().payload.donation.toDomain()
    }

    override suspend fun createDonation(request: DonationRequest) = request {
        val body = CreateDonationRequest(
            items = request.items.map { DonationRequestItemDto(it.name, it.quantity, it.unit) },
            areaName = request.areaName,
            meetingPoint = request.meetingPoint,
            latitude = request.latitude,
            longitude = request.longitude,
            receiveStart = request.receiveStart,
            receiveEnd = request.receiveEnd
        )
        donationApiService.createDonation(body).bodyOrThrow().payload.donation.toDomain()
    }

    override suspend fun fulfillDonation(id: String, itemIds: List<String>) = request {
        donationApiService.fulfillDonation(id, FulfillDonationRequest(itemIds))
            .bodyOrThrow().payload.donation.toDomain()
    }

    override suspend fun confirmReceipt(id: String) = request {
        donationApiService.confirmReceipt(id).bodyOrThrow().payload.donation.toDomain()
    }

    override suspend fun cancelDonation(id: String) = request {
        donationApiService.cancelDonation(id).requireSuccess()
    }

    private suspend fun <T> request(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(block())
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private fun <T> Response<T>.bodyOrThrow(): T {
        requireSuccess()
        return checkNotNull(body()) { "Respons donasi kosong." }
    }

    private fun Response<*>.requireSuccess() {
        if (isSuccessful) return
        val error = errorBody()?.use { body ->
            runCatching { json.decodeFromString<ErrorResponse>(body.string()) }.getOrNull()
        }
        val message = when (error?.errorCode ?: error?.error) {
            "phone_number_required" -> "Simpan nomor WhatsApp di Profil terlebih dahulu."
            "conflict" -> "Permintaan sudah berubah. Muat ulang untuk melihat status terbaru."
            else -> error?.message ?: error?.error ?: "Permintaan gagal (HTTP ${code()})."
        }
        error(message)
    }
}
