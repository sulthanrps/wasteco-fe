package com.example.wasteco.ui.feature.auth.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.wasteco.R
import com.example.wasteco.ui.components.WasteCoLogo
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.EntryColors
import com.example.wasteco.ui.theme.EntryDimens
import com.example.wasteco.ui.theme.EntryTypography
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.WasteCoTheme

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize().background(EntryColors.background).statusBarsPadding(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = EntryDimens.maxWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = EntryDimens.gutter, vertical = Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WasteCoLogo(withTagline = true)
            Spacer(Modifier.height(Spacing.compact))
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(EntryDimens.heroAspectRatio)
                    .clip(RoundedCornerShape(Radius.large))
            ) {
                Image(
                    painter = painterResource(R.drawable.onboarding_hero),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(Spacing.medium))
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.compact)) {
                OnboardingFeature(
                    Icons.Outlined.SoupKitchen, EntryColors.greenTint, EntryColors.action,
                    stringResource(R.string.onboarding_recipes), stringResource(R.string.onboarding_recipes_description)
                )
                OnboardingFeature(
                    Icons.Outlined.Handshake, EntryColors.orangeTint, EntryColors.orangeInk,
                    stringResource(R.string.onboarding_donations), stringResource(R.string.onboarding_donations_description)
                )
            }
            Spacer(Modifier.height(EntryDimens.gutter))
            WasteCoPrimaryButton(
                text = stringResource(R.string.onboarding_get_started),
                onClick = onGetStartedClick,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                containerColor = EntryColors.actionSolid,
                cornerRadius = Radius.medium,
                iconSize = EntryDimens.buttonIcon
            )
            Spacer(Modifier.height(Spacing.medium))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.auth_existing_account), style = EntryTypography.body, color = EntryColors.body)
                TextButton(onClick = onLoginClick) {
                    Text(stringResource(R.string.auth_sign_in), color = EntryColors.action)
                }
            }
        }
    }
}

@Composable
private fun OnboardingFeature(
    icon: ImageVector,
    badgeColor: Color,
    iconColor: Color,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.medium),
        color = EntryColors.surface,
        shadowElevation = Elevation.small
    ) {
        Row(
            modifier = Modifier.padding(Spacing.compact),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
        ) {
            Box(
                Modifier.size(EntryDimens.featureIconBadge).background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(EntryDimens.featureIcon), tint = iconColor)
            }
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = EntryColors.heading)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = EntryColors.muted)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun OnboardingScreenPreview() {
    WasteCoTheme { OnboardingScreen(onGetStartedClick = {}, onLoginClick = {}) }
}
