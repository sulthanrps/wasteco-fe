package com.example.wasteco.data.repository

import com.example.wasteco.domain.model.Donation

interface DonationRepository {
    suspend fun getNearbyDonations(
        latitude: Double? = -7.9535,
        longitude: Double? = 112.608,
        radius: Int? = 5,
        category: String? = null,
        search: String? = null
    ): Result<List<Donation>>

    suspend fun getMyDonations(): Result<List<Donation>>

    suspend fun getGivenDonations(): Result<List<Donation>>

    suspend fun getDonationDetail(id: String): Result<Donation>

    suspend fun createDonation(
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
    ): Result<Unit>

    suspend fun confirmHandover(id: String): Result<Unit>

    suspend fun cancelDonation(id: String): Result<Unit>

    fun getCachedDonation(id: String): Donation?
}
