package com.example.wasteco.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wasteco.R
import com.example.wasteco.ui.theme.EntryColors
import com.example.wasteco.ui.theme.EntryDimens
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing

@Composable
fun WasteCoBrandMark(modifier: Modifier = Modifier, decorative: Boolean = false) {
    Image(
        painter = painterResource(R.drawable.wasteco_logo),
        contentDescription = if (decorative) null else stringResource(R.string.app_name),
        modifier = modifier
    )
}

@Composable
fun WasteCoLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = size * 0.72f
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(Radius.medium))
            .background(EntryColors.greenTint),
        contentAlignment = Alignment.Center
    ) {
        WasteCoBrandMark(Modifier.size(iconSize))
    }
}

@Composable
fun WasteCoLogo(modifier: Modifier = Modifier, withTagline: Boolean = false) {
    if (withTagline) {
        Image(
            painter = painterResource(R.drawable.wasteco_logo_tagline),
            contentDescription = stringResource(R.string.brand_with_tagline),
            modifier = modifier.width(EntryDimens.onboardingLogoWidth)
        )
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WasteCoBrandMark(Modifier.size(EntryDimens.compactLogo), decorative = true)
            Spacer(Modifier.width(Spacing.xs))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = EntryColors.action
            )
        }
    }
}
