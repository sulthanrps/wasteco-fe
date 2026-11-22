package com.example.wasteco.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.wasteco.R
import com.example.wasteco.domain.model.SessionState
import com.example.wasteco.ui.feature.auth.login.LoginRoute
import com.example.wasteco.ui.feature.auth.onboarding.OnboardingScreen
import com.example.wasteco.ui.feature.auth.register.RegisterRoute
import com.example.wasteco.ui.feature.donation.DonationHubRoute
import com.example.wasteco.ui.feature.donation.confirm.ConfirmReceiptRoute
import com.example.wasteco.ui.feature.donation.create.CreateDonationRoute
import com.example.wasteco.ui.feature.donation.detail.DonationDetailRoute
import com.example.wasteco.ui.feature.donation.map.DonationMapRoute
import com.example.wasteco.ui.feature.home.HomeRoute
import com.example.wasteco.ui.feature.pantry.PantryRoute
import com.example.wasteco.ui.feature.profile.ProfileRoute
import com.example.wasteco.ui.feature.recipe.RecipeListRoute
import com.example.wasteco.ui.feature.recipe.camera.IngredientScanRoute
import com.example.wasteco.ui.feature.recipe.detail.CookConfirmRoute
import com.example.wasteco.ui.feature.recipe.detail.RecipeDetailRoute

private const val SHOW_REQUESTING_AFTER_CREATE = "show_requesting_after_create"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteCoNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    sessionViewModel: SessionViewModel = viewModel(factory = SessionViewModel.Factory)
) {
    val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()

    if (sessionState == SessionState.Checking) {
        SessionCheckingIndicator(modifier = modifier)
        return
    }

    // Decided once: changing startDestination later would rebuild the whole graph.
    val startDest: Destination = remember {
        if (sessionState == SessionState.Authenticated) Destination.Home else Destination.Onboarding
    }

    // Session expired (401) or logged out while inside the app -> back to Login, clear back stack.
    LaunchedEffect(sessionState) {
        if (sessionState != SessionState.Unauthenticated) return@LaunchedEffect
        val current = navController.currentDestination ?: return@LaunchedEffect
        val isOnAuthScreen = current.hasRoute<Destination.Onboarding>() ||
            current.hasRoute<Destination.Login>() ||
            current.hasRoute<Destination.Register>()
        if (!isOnAuthScreen) {
            navController.navigate(Destination.Login) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDest,
        modifier = modifier.navigationBarsPadding()
    ) {
        composable<Destination.Onboarding> {
            OnboardingScreen(
                onGetStartedClick = {
                    navController.navigate(Destination.Register)
                },
                onLoginClick = {
                    navController.navigate(Destination.Login)
                }
            )
        }

        composable<Destination.Login> {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigate(Destination.Home) {
                        popUpTo(Destination.Onboarding) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Destination.Register) {
                        popUpTo(Destination.Login) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Destination.Register> {
            RegisterRoute(
                onRegisterSuccess = {
                    navController.navigate(Destination.Home) {
                        popUpTo(Destination.Onboarding) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Destination.Login) {
                        popUpTo(Destination.Register) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Destination.Home> {
            HomeRoute(
                onDonationClick = { navController.navigate(Destination.DonationDetail(it)) },
                onDonationMapClick = { navController.navigate(Destination.DonationMap) },
                onPantryClick = {
                    navController.navigate(Destination.Pantry)
                },
                onDonationsClick = {
                    navController.navigate(Destination.Donations)
                },
                onProfileClick = {
                    navController.navigate(Destination.Profile)
                }
            )
        }

        composable<Destination.Pantry> {
            PantryRoute(
                onHomeTabClick = {
                    navController.navigate(Destination.Home) {
                        popUpTo(Destination.Home) { inclusive = true }
                    }
                },
                onDonationsTabClick = {
                    navController.navigate(Destination.Donations)
                },
                onProfileClick = {
                    navController.navigate(Destination.Profile)
                },
                onCameraClick = {
                    navController.navigate(Destination.CameraCapture)
                },
                onGenerateRecipesClick = { selectedIds ->
                    navController.navigate(Destination.RecipeList(selectedIds))
                }
            )
        }

        composable<Destination.CameraCapture> {
            IngredientScanRoute(
                onClose = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(Destination.Pantry) {
                        popUpTo(Destination.Pantry) { inclusive = true }
                    }
                }
            )
        }

        composable<Destination.RecipeList> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.RecipeList>()
            RecipeListRoute(
                ingredientIds = route.ingredientIds,
                onBackClick = {
                    navController.popBackStack()
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(Destination.RecipeDetail(recipeId))
                }
            )
        }

        composable<Destination.RecipeDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.RecipeDetail>()
            RecipeDetailRoute(
                recipeId = route.recipeId,
                onBackClick = {
                    navController.popBackStack()
                },
                onCookClick = { recipeId ->
                    navController.navigate(Destination.CookConfirm(recipeId))
                }
            )
        }

        composable<Destination.CookConfirm> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.CookConfirm>()
            CookConfirmRoute(
                recipeId = route.recipeId,
                onBackClick = {
                    navController.popBackStack()
                },
                onFinishAndGoHome = {
                    navController.navigate(Destination.Home) {
                        popUpTo(Destination.Home) { inclusive = true }
                    }
                }
            )
        }

        composable<Destination.Donations> { backStackEntry ->
            val showRequesting by remember(backStackEntry) {
                backStackEntry.savedStateHandle.getStateFlow(SHOW_REQUESTING_AFTER_CREATE, false)
            }.collectAsStateWithLifecycle()
            DonationHubRoute(
                showRequesting = showRequesting,
                onRequestingShown = {
                    backStackEntry.savedStateHandle[SHOW_REQUESTING_AFTER_CREATE] = false
                },
                onMapViewClick = {
                    navController.navigate(Destination.DonationMap)
                },
                onCreateRequestClick = {
                    navController.navigate(Destination.CreateDonation)
                },
                onDonationClick = { donationId ->
                    navController.navigate(Destination.DonationDetail(donationId))
                },
                onConfirmReceiptClick = { donationId ->
                    navController.navigate(Destination.ConfirmReceipt(donationId))
                },
                onHomeTabClick = {
                    navController.navigate(Destination.Home) {
                        popUpTo(Destination.Home) { inclusive = true }
                    }
                },
                onRecipeTabClick = {
                    navController.navigate(Destination.Pantry)
                },
                onProfileClick = {
                    navController.navigate(Destination.Profile)
                }
            )
        }

        composable<Destination.DonationMap> {
            DonationMapRoute(
                onListViewClick = {
                    if (navController.previousBackStackEntry?.destination?.hasRoute<Destination.Donations>() == true) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Destination.Donations) {
                            popUpTo(Destination.DonationMap) { inclusive = true }
                        }
                    }
                },
                onDonationDetailClick = { donationId, latitude, longitude ->
                    navController.navigate(Destination.DonationDetail(donationId, latitude?.toString(), longitude?.toString()))
                },
                onHomeTabClick = {
                    navController.navigate(Destination.Home) {
                        popUpTo(Destination.Home) { inclusive = true }
                    }
                },
                onRecipeTabClick = {
                    navController.navigate(Destination.Pantry)
                },
                onProfileClick = {
                    navController.navigate(Destination.Profile)
                }
            )
        }

        composable<Destination.DonationDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.DonationDetail>()
            DonationDetailRoute(
                donationId = route.donationId,
                latitude = route.latitude?.toDoubleOrNull(),
                longitude = route.longitude?.toDoubleOrNull(),
                onBackClick = {
                    navController.popBackStack()
                },
                onReturnToDonationHub = {
                    if (!navController.popBackStack(Destination.Donations, inclusive = false)) {
                        navController.navigate(Destination.Donations)
                    }
                }
            )
        }

        composable<Destination.CreateDonation> {
            CreateDonationRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccessFinish = {
                    navController.navigate(Destination.Donations) {
                        popUpTo(Destination.Donations) { inclusive = true }
                    }
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set(SHOW_REQUESTING_AFTER_CREATE, true)
                }
            )
        }

        composable<Destination.ConfirmReceipt> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.ConfirmReceipt>()
            ConfirmReceiptRoute(
                donationId = route.donationId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccessFinish = {
                    navController.navigate(Destination.Donations) {
                        popUpTo(Destination.Donations) { inclusive = true }
                    }
                }
            )
        }

        composable<Destination.Profile> {
            ProfileRoute(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun SessionCheckingIndicator(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.session_checking)
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
