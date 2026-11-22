package com.example.wasteco.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Onboarding : Destination

    @Serializable
    data object Login : Destination

    @Serializable
    data object Register : Destination

    @Serializable
    data object Home : Destination

    @Serializable
    data object Pantry : Destination

    @Serializable
    data object CameraCapture : Destination

    @Serializable
    data object ReviewPhoto : Destination

    @Serializable
    data class RecipeList(val ingredientIds: List<String> = emptyList()) : Destination

    @Serializable
    data class RecipeDetail(val recipeId: String) : Destination

    @Serializable
    data class CookConfirm(val recipeId: String) : Destination

    @Serializable
    data object Donations : Destination

    @Serializable
    data object DonationMap : Destination

    @Serializable
    data class DonationDetail(val donationId: String) : Destination

    @Serializable
    data object CreateDonation : Destination

    @Serializable
    data class ConfirmReceipt(val donationId: String) : Destination

    @Serializable
    data object Profile : Destination
}
