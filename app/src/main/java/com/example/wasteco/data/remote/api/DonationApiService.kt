package com.example.wasteco.data.remote.api

import com.example.wasteco.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface DonationApiService {
    @GET("api/v1/donations")
    suspend fun getNearbyDonations(
        @Query("lat") latitude: Double?,
        @Query("long") longitude: Double?,
        @Query("radius") radius: Int,
        @Query("search") search: String?
    ): Response<DonationListResponse>

    @GET("api/v1/donations/my")
    suspend fun getMyDonations(@Query("role") role: String): Response<DonationActivityResponse>

    @GET("api/v1/donations/{id}")
    suspend fun getDonationDetail(
        @Path("id") id: String,
        @Query("lat") latitude: Double?,
        @Query("long") longitude: Double?
    ): Response<DonationDetailResponse>

    @POST("api/v1/donations")
    suspend fun createDonation(@Body request: CreateDonationRequest): Response<DonationDetailResponse>

    @POST("api/v1/donations/{id}/fulfill")
    suspend fun fulfillDonation(
        @Path("id") id: String,
        @Body request: FulfillDonationRequest
    ): Response<DonationDetailResponse>

    @POST("api/v1/donations/{id}/confirm-received")
    suspend fun confirmReceipt(@Path("id") id: String): Response<DonationDetailResponse>

    @DELETE("api/v1/donations/{id}")
    suspend fun cancelDonation(@Path("id") id: String): Response<Unit>
}
