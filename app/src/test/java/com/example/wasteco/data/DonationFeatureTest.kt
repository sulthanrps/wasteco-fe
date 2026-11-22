package com.example.wasteco.data

import com.example.wasteco.data.remote.api.DonationApiService
import com.example.wasteco.data.remote.dto.CreateDonationRequest
import com.example.wasteco.data.remote.dto.DonationActivityResponse
import com.example.wasteco.data.remote.dto.DonationDetailResponse
import com.example.wasteco.data.remote.dto.DonationListResponse
import com.example.wasteco.data.remote.dto.FulfillDonationRequest
import com.example.wasteco.data.repository.DefaultDonationRepository
import com.example.wasteco.domain.model.DonationRequest
import com.example.wasteco.domain.model.DonationRequestItem
import com.example.wasteco.domain.model.DonationRole
import com.example.wasteco.ui.feature.donation.ActivityFilter
import com.example.wasteco.ui.feature.donation.DonationHubUiState
import com.example.wasteco.ui.feature.donation.create.CreateDonationUiState
import com.example.wasteco.ui.feature.donation.map.DonationMapUiState
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class DonationFeatureTest {
    private val api = FakeDonationApiService()
    private val repository = DefaultDonationRepository(api)

    @Test
    fun nearbyUsesThreeKilometersAndPreservesNumericItemQuantity() = runBlocking {
        val result = repository.getNearbyDonations(latitude = -7.95, longitude = 112.61).getOrThrow()
        assertEquals(3, api.radius)
        assertEquals(-7.95 to 112.61, api.coordinates)
        assertEquals("500 gram", result.single().items.first().quantity)
        assertNull(result.single().user.whatsappUrl)
    }

    @Test
    fun nearbyDistinguishesEmptyServerResultsFromErrors() = runBlocking {
        api.nearby = Response.success(json.decodeFromString<DonationListResponse>("""{"payload":{"donations":[]}}"""))
        assertTrue(repository.getNearbyDonations().getOrThrow().isEmpty())
        api.nearby = errorResponse(500)
        assertTrue(repository.getNearbyDonations().isFailure)
    }

    @Test
    fun detailFetchesTheRequesterContactEvenAfterNearbyWasLoaded() = runBlocking {
        val id = repository.getNearbyDonations().getOrThrow().single().id
        val detail = repository.getDonationDetail(id).getOrThrow()

        assertEquals(id, api.detailId)
        assertEquals("https://wa.me/6281234567890", detail.user.whatsappUrl)
    }

    @Test
    fun createPreservesItemsAndTimeWindowAndSurfacesServerRejection() = runBlocking {
        val request = DonationRequest(
            items = listOf(DonationRequestItem("Wortel", 500.0, "gram"), DonationRequestItem("Telur", 4.0, "butir")),
            areaName = "Dinoyo",
            meetingPoint = "Minimarket",
            latitude = -7.95,
            longitude = 112.61,
            receiveStart = "2026-11-25T16:00:00+07:00",
            receiveEnd = "2026-11-25T19:00:00+07:00"
        )
        api.create = errorResponse(400, """{"error_code":"phone_number_required"}""")
        assertTrue(repository.createDonation(request).exceptionOrNull()?.message.orEmpty().contains("WhatsApp"))
        assertEquals(listOf("Wortel", "Telur"), api.createdBody?.items?.map { it.name })
        assertEquals(listOf(500.0, 4.0), api.createdBody?.items?.map { it.quantity })
        assertEquals(request.receiveStart, api.createdBody?.receiveStart)
        assertEquals(request.receiveEnd, api.createdBody?.receiveEnd)
    }

    @Test
    fun fulfillmentAndReceiptUseDistinctActionsAndServerStatuses() = runBlocking {
        val id = "request-1"
        val handedOver = repository.fulfillDonation(id, listOf("item-1")).getOrThrow()
        assertEquals(listOf("item-1"), api.fulfilledItemIds)
        assertEquals("handed_over", handedOver.status)
        assertEquals(2, handedOver.step)
        assertTrue(handedOver.items.single().selected)

        val received = repository.confirmReceipt(id).getOrThrow()
        assertEquals(id, api.confirmedId)
        assertEquals("received", received.status)
        assertEquals(3, received.step)

        api.fulfill = errorResponse(409)
        assertTrue(repository.fulfillDonation(id, listOf("item-1")).isFailure)
        api.cancellation = errorResponse(403)
        assertTrue(repository.cancelDonation(id).isFailure)
    }

    @Test
    fun activitiesUseServerRolesAndIncludeHistory() = runBlocking {
        val given = repository.getGivenDonations().getOrThrow()
        val requested = repository.getMyDonations().getOrThrow()
        assertEquals(listOf("donor", "requester"), api.roles)
        assertEquals(listOf("handed_over", "received"), given.map { it.status })

        val state = DonationHubUiState(givenDonations = given, myDonations = requested)
        assertEquals(4, state.filteredActivities.size)
        assertTrue(state.copy(selectedActivityFilter = ActivityFilter.GIVING)
            .filteredActivities.all { it.role == DonationRole.DONOR })
        assertTrue(state.copy(selectedActivityFilter = ActivityFilter.REQUESTING)
            .filteredActivities.all { it.role == DonationRole.REQUESTER })
    }

    @Test
    fun requestingOnlyShowsActiveRequestsAndAllKeepsCompletedHistory() = runBlocking {
        val requested = repository.getMyDonations().getOrThrow()
        val open = requested.first().copy(id = "open-request", status = "open")
        val cancelled = requested.first().copy(id = "cancelled-request", status = "cancelled")
        val state = DonationHubUiState(myDonations = listOf(open, cancelled) + requested)

        assertEquals(2, state.activeRequestCount)
        assertEquals(0, state.copy(myDonations = listOf(cancelled, requested.last())).activeRequestCount)
        assertEquals(
            listOf("open", "cancelled", "handed_over", "received"),
            state.filteredActivities.map { it.donation.status }
        )
        assertEquals(
            listOf("open", "handed_over"),
            state.copy(selectedActivityFilter = ActivityFilter.REQUESTING)
                .filteredActivities.map { it.donation.status }
        )
    }

    @Test
    fun mapSelectionUsesTheRequestedPin() = runBlocking {
        val donation = repository.getNearbyDonations().getOrThrow().single()
        val state = DonationMapUiState(donations = listOf(donation), selectedDonationId = donation.id)
        assertEquals(donation, state.selectedDonation)
        assertNull(state.copy(donations = emptyList()).selectedDonation)
    }

    @Test
    fun newRequestStartsWithoutExampleItemsOrAnInventedLocation() {
        val state = CreateDonationUiState()
        assertTrue(state.addedItems.isEmpty())
        assertFalse(state.hasLocation)
        assertTrue(state.meetingPoint.isBlank())
        assertNull(state.receiveStart)
        assertNull(state.receiveEnd)
    }
}

private val json = Json { ignoreUnknownKeys = true }

private class FakeDonationApiService : DonationApiService {
    var radius: Int? = null
    var coordinates: Pair<Double?, Double?>? = null
    var detailId: String? = null
    var createdBody: CreateDonationRequest? = null
    var fulfilledItemIds: List<String>? = null
    var confirmedId: String? = null
    val roles = mutableListOf<String>()
    var nearby = Response.success(
        json.decodeFromString<DonationListResponse>(
            """{"payload":{"donations":[${donation(status = "open", contact = false)}]}}"""
        )
    )
    var create = donationResponse("open")
    var fulfill = donationResponse("handed_over")
    var cancellation = Response.success<Unit>(null)

    override suspend fun getNearbyDonations(
        latitude: Double?, longitude: Double?, radius: Int, search: String?
    ): Response<DonationListResponse> {
        this.radius = radius
        coordinates = latitude to longitude
        return nearby
    }

    override suspend fun getMyDonations(role: String): Response<DonationActivityResponse> {
        roles.add(role)
        return Response.success(
            json.decodeFromString<DonationActivityResponse>(
                """{"payload":{"active":[${donation("handed_over")}],"history":[${donation("received")}]}}"""
            )
        )
    }

    override suspend fun getDonationDetail(
        id: String, latitude: Double?, longitude: Double?
    ): Response<DonationDetailResponse> {
        detailId = id
        return donationResponse("open")
    }

    override suspend fun createDonation(request: CreateDonationRequest): Response<DonationDetailResponse> {
        createdBody = request
        return create
    }

    override suspend fun fulfillDonation(id: String, request: FulfillDonationRequest): Response<DonationDetailResponse> {
        fulfilledItemIds = request.itemIds
        return fulfill
    }

    override suspend fun confirmReceipt(id: String): Response<DonationDetailResponse> {
        confirmedId = id
        return donationResponse("received")
    }

    override suspend fun cancelDonation(id: String): Response<Unit> = cancellation
}

private fun <T> errorResponse(code: Int, body: String = """{"message":"Server error"}"""): Response<T> =
    Response.error(code, body.toResponseBody())

private fun donationResponse(status: String): Response<DonationDetailResponse> =
    Response.success(json.decodeFromString("""{"payload":{"donation":${donation(status)}}}"""))

private fun donation(status: String, contact: Boolean = true): String {
    val requester = if (contact) {
        """{"id":"user-1","name":"Peminta","whatsapp_url":"https://wa.me/6281234567890"}"""
    } else {
        """{"id":"user-1","name":"Peminta"}"""
    }
    return """
        {
          "id":"request-1",
          "title":"Wortel",
          "meeting_point":"Minimarket",
          "latitude":-7.95,
          "longitude":112.61,
          "receive_start":"2026-11-25T16:00:00+07:00",
          "receive_end":"2026-11-25T19:00:00+07:00",
          "status":"$status",
          "items":[{"id":"item-1","name":"Wortel","quantity":500,"unit":"gram","selected":${status != "open"}}],
          "requester":$requester
        }
    """.trimIndent()
}
