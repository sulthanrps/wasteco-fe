package com.example.wasteco.ui.feature.donation

import com.example.wasteco.domain.model.Donation
import com.example.wasteco.domain.model.DONATION_RADIUS_KM
import com.example.wasteco.domain.model.DonationRole

enum class DonationTab {
    NEARBY, MY_ACTIVITIES
}

enum class ActivityFilter {
    ALL, GIVING, REQUESTING
}

data class DonationActivityItem(
    val donation: Donation,
    val role: DonationRole
)

data class DonationHubUiState(
    val selectedTab: DonationTab = DonationTab.NEARBY,
    val selectedActivityFilter: ActivityFilter = ActivityFilter.ALL,
    val nearbyDonations: List<Donation> = emptyList(),
    val myDonations: List<Donation> = emptyList(),
    val givenDonations: List<Donation> = emptyList(),
    val searchQuery: String = "",
    val currentLocation: String = "",
    val radiusKm: Int = DONATION_RADIUS_KM,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null
) {
    val filteredNearbyDonations: List<Donation>
        get() = if (searchQuery.isBlank()) {
            nearbyDonations
        } else {
            nearbyDonations.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.itemsSummary.contains(searchQuery, ignoreCase = true)
            }
        }

    private val activeRequests: List<Donation>
        get() = myDonations.filter { it.status == "open" || it.status == "handed_over" }

    val activeRequestCount: Int
        get() = activeRequests.size

    val filteredActivities: List<DonationActivityItem>
        get() {
            val list = mutableListOf<DonationActivityItem>()
            if (selectedActivityFilter == ActivityFilter.ALL || selectedActivityFilter == ActivityFilter.GIVING) {
                givenDonations.forEach { list.add(DonationActivityItem(it, DonationRole.DONOR)) }
            }
            if (selectedActivityFilter == ActivityFilter.ALL || selectedActivityFilter == ActivityFilter.REQUESTING) {
                val requests = if (selectedActivityFilter == ActivityFilter.ALL) myDonations else activeRequests
                requests.forEach { list.add(DonationActivityItem(it, DonationRole.REQUESTER)) }
            }
            return list
        }
}
