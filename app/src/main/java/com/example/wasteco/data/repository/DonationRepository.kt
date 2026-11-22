package com.example.wasteco.data.repository

import com.example.wasteco.domain.model.DONATION_RADIUS_KM
import com.example.wasteco.domain.model.Donation
import com.example.wasteco.domain.model.DonationRequest

interface DonationRepository {
    suspend fun getNearbyDonations(
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Int = DONATION_RADIUS_KM,
        search: String? = null
    ): Result<List<Donation>>

    suspend fun getMyDonations(): Result<List<Donation>>
    suspend fun getGivenDonations(): Result<List<Donation>>
    suspend fun getDonationDetail(id: String, latitude: Double? = null, longitude: Double? = null): Result<Donation>
    suspend fun createDonation(request: DonationRequest): Result<Donation>
    suspend fun fulfillDonation(id: String, itemIds: List<String>): Result<Donation>
    suspend fun confirmReceipt(id: String): Result<Donation>
    suspend fun cancelDonation(id: String): Result<Unit>
}
