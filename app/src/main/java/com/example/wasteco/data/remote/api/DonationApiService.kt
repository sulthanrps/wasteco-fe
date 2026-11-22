package com.example.wasteco.data.remote.api

import com.example.wasteco.data.remote.dto.DonationActionResponse
import com.example.wasteco.data.remote.dto.DonationDetailResponse
import com.example.wasteco.data.remote.dto.DonationListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface DonationApiService {

    @GET("api/v1/donations")
    suspend fun getNearbyDonations(
        @Query("lat") latitude: Double? = null,
        @Query("long") longitude: Double? = null,
        @Query("radius") radius: Int? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<DonationListResponse>

    @GET("api/v1/donations/my")
    suspend fun getMyDonations(): Response<DonationListResponse>

    @GET("api/v1/donations/{id}")
    suspend fun getDonationDetail(
        @Path("id") id: String,
        @Query("lat") latitude: Double? = null,
        @Query("long") longitude: Double? = null
    ): Response<DonationDetailResponse>

    @Multipart
    @POST("api/v1/donations")
    suspend fun createDonation(
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part photo: MultipartBody.Part? = null
    ): Response<DonationActionResponse>

    @POST("api/v1/donations/{id}/confirm-handed")
    suspend fun confirmHandover(
        @Path("id") id: String
    ): Response<DonationActionResponse>

    @DELETE("api/v1/donations/{id}")
    suspend fun cancelDonation(
        @Path("id") id: String
    ): Response<DonationActionResponse>
}
