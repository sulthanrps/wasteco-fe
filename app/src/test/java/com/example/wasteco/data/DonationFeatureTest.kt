package com.example.wasteco.data

import com.example.wasteco.data.remote.api.DonationApiService
import com.example.wasteco.data.remote.dto.DonationActionResponse
import com.example.wasteco.data.remote.dto.DonationDetailResponse
import com.example.wasteco.data.remote.dto.DonationListResponse
import com.example.wasteco.data.repository.DefaultDonationRepository
import com.example.wasteco.domain.model.DonationItem
import com.example.wasteco.domain.model.DonationRole
import com.example.wasteco.domain.model.DonationUser
import com.example.wasteco.domain.model.Donation
import com.example.wasteco.ui.feature.donation.ActivityFilter
import com.example.wasteco.ui.feature.donation.DonationActivityItem
import com.example.wasteco.ui.feature.donation.DonationHubUiState
import com.example.wasteco.ui.feature.donation.map.DonationMapUiState
import com.example.wasteco.ui.feature.donation.create.CreateDonationUiState
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FakeDonationApiService : DonationApiService {
    override suspend fun getNearbyDonations(
        latitude: Double?,
        longitude: Double?,
        radius: Int?,
        category: String?,
        search: String?
    ): Response<DonationListResponse> = Response.error(
        500,
        "{\"message\":\"Server error\"}".toResponseBody("application/json".toMediaTypeOrNull())
    )

    override suspend fun getMyDonations(): Response<DonationListResponse> = Response.error(
        500,
        "{\"message\":\"Server error\"}".toResponseBody("application/json".toMediaTypeOrNull())
    )

    override suspend fun getDonationDetail(
        id: String,
        latitude: Double?,
        longitude: Double?
    ): Response<DonationDetailResponse> = Response.error(
        404,
        "{\"message\":\"Not found\"}".toResponseBody("application/json".toMediaTypeOrNull())
    )

    override suspend fun createDonation(
        partMap: Map<String, RequestBody>,
        photo: MultipartBody.Part?
    ): Response<DonationActionResponse> = Response.success(
        DonationActionResponse("Donation created")
    )

    override suspend fun confirmHandover(id: String): Response<DonationActionResponse> = Response.success(
        DonationActionResponse("Confirmed")
    )

    override suspend fun cancelDonation(id: String): Response<DonationActionResponse> = Response.success(
        DonationActionResponse("Cancelled")
    )
}

class DonationFeatureTest {

    private lateinit var repository: DefaultDonationRepository

    @Before
    fun setUp() {
        repository = DefaultDonationRepository(
            donationApiService = FakeDonationApiService()
        )
    }

    @Test
    fun testNearbyDonationsFallbackReturnsSeedItems() = runBlocking {
        val result = repository.getNearbyDonations()
        assertTrue(result.isSuccess)
        val donations = result.getOrNull()
        assertNotNull(donations)
        assertTrue(donations!!.isNotEmpty())
        val first = donations.first()
        assertTrue(first.latitude != 0.0)
        assertTrue(first.longitude != 0.0)
    }

    @Test
    fun testGivenDonationsIncludesInitialSeedAndHandover() = runBlocking {
        val initialGiven = repository.getGivenDonations().getOrNull()
        assertNotNull(initialGiven)
        assertEquals(1, initialGiven!!.size)
        assertEquals("given_don_1", initialGiven.first().id)

        // Confirm handover on don_1 (Wortel & Telur)
        val handoverResult = repository.confirmHandover("don_1")
        assertTrue(handoverResult.isSuccess)

        val updatedGiven = repository.getGivenDonations().getOrNull()
        assertNotNull(updatedGiven)
        assertEquals(2, updatedGiven!!.size)
        assertTrue(updatedGiven.any { it.id == "don_1" })
    }

    @Test
    fun testDonationHubOptionBActivityFilters() {
        val dummyGiven = Donation(
            id = "g1",
            title = "Bayam",
            category = "Sayur",
            itemsSummary = "Bayam 1 ikat",
            items = listOf(DonationItem("Bayam", "1 ikat")),
            conditionNote = "",
            storageMethod = "",
            pickupDeadline = "",
            areaName = "",
            meetingPoint = "",
            latitude = 0.0,
            longitude = 0.0,
            distanceDesc = "",
            whatsappNumber = "",
            estimatedPrice = 0,
            estimatedPortions = 1,
            photoUrl = "",
            status = "in_progress",
            step = 2,
            createdTimeAgo = "",
            user = DonationUser("u1", "Penerima", "", null, true),
            donor = DonationUser("my_id", "Affan", "", null, true)
        )

        val dummyMy = Donation(
            id = "m1",
            title = "Telur",
            category = "Protein",
            itemsSummary = "Telur 2 butir",
            items = listOf(DonationItem("Telur", "2 butir")),
            conditionNote = "",
            storageMethod = "",
            pickupDeadline = "",
            areaName = "",
            meetingPoint = "",
            latitude = 0.0,
            longitude = 0.0,
            distanceDesc = "",
            whatsappNumber = "",
            estimatedPrice = 0,
            estimatedPortions = 1,
            photoUrl = "",
            status = "in_progress",
            step = 2,
            createdTimeAgo = "",
            user = DonationUser("my_id", "Affan", "", null, true),
            donor = DonationUser("u2", "Donor", "", null, true)
        )

        val stateAll = DonationHubUiState(
            selectedActivityFilter = ActivityFilter.ALL,
            givenDonations = listOf(dummyGiven),
            myDonations = listOf(dummyMy)
        )
        assertEquals(2, stateAll.filteredActivities.size)

        val stateGiving = stateAll.copy(selectedActivityFilter = ActivityFilter.GIVING)
        assertEquals(1, stateGiving.filteredActivities.size)
        assertEquals(DonationRole.DONOR, stateGiving.filteredActivities.first().role)

        val stateRequesting = stateAll.copy(selectedActivityFilter = ActivityFilter.REQUESTING)
        assertEquals(1, stateRequesting.filteredActivities.size)
        assertEquals(DonationRole.REQUESTER, stateRequesting.filteredActivities.first().role)
    }

    @Test
    fun testDonationMapUiStateSelection() {
        val dummy1 = Donation(
            id = "d1",
            title = "Wortel",
            category = "Sayur",
            itemsSummary = "Wortel 1kg",
            items = listOf(DonationItem("Wortel", "1kg")),
            conditionNote = "",
            storageMethod = "",
            pickupDeadline = "",
            areaName = "Dinoyo",
            meetingPoint = "Minimarket",
            latitude = -7.9535,
            longitude = 112.608,
            distanceDesc = "800 m",
            whatsappNumber = "",
            estimatedPrice = 10000,
            estimatedPortions = 2,
            photoUrl = "",
            status = "active",
            step = 1,
            createdTimeAgo = "1 jam lalu",
            user = DonationUser("u1", "Budi", "", null, true),
            donor = null
        )

        val mapState = DonationMapUiState(
            donations = listOf(dummy1),
            selectedDonationId = "d1"
        )
        assertNotNull(mapState.selectedDonation)
        assertEquals("d1", mapState.selectedDonation?.id)
    }

    @Test
    fun testDonationMapLocationUpdate() {
        val initialState = DonationMapUiState()
        assertEquals(-7.9535, initialState.userLatitude, 0.0001)
        assertEquals(112.608, initialState.userLongitude, 0.0001)
        assertEquals("Dinoyo, Malang", initialState.userAddress)
        assertEquals(false, initialState.isLocationSheetVisible)
        assertEquals(false, initialState.isGpsLoading)

        // User changes location to Lowokwaru
        val updatedState = initialState.copy(
            userLatitude = -7.9450,
            userLongitude = 112.6150,
            userAddress = "Lowokwaru, Malang",
            isLocationSheetVisible = true
        )
        assertEquals(-7.9450, updatedState.userLatitude, 0.0001)
        assertEquals(112.6150, updatedState.userLongitude, 0.0001)
        assertEquals("Lowokwaru, Malang", updatedState.userAddress)
        assertTrue(updatedState.isLocationSheetVisible)
    }

    @Test
    fun testCreateDonationLocationState() {
        val defaultState = CreateDonationUiState()
        assertEquals(-7.9535, defaultState.latitude, 0.0001)
        assertEquals(112.6080, defaultState.longitude, 0.0001)
        assertEquals("Dinoyo, Malang", defaultState.areaName)

        val updated = defaultState.copy(
            latitude = -7.9826,
            longitude = 112.6308,
            areaName = "Klojen, Kota Malang",
            meetingPoint = "Alun-Alun Kota Malang"
        )
        assertEquals(-7.9826, updated.latitude, 0.0001)
        assertEquals(112.6308, updated.longitude, 0.0001)
        assertEquals("Klojen, Kota Malang", updated.areaName)
        assertEquals("Alun-Alun Kota Malang", updated.meetingPoint)
    }
}
