package com.example.wasteco.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wasteco.ui.theme.PrimaryLight
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.SecondaryMain
import com.example.wasteco.ui.theme.Spacing

@Composable
fun WasteCoLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 28.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(Radius.medium))
            .background(PrimaryLight),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = "WasteCo Logo",
            tint = PrimaryMain,
            modifier = Modifier.size(iconSize)
        )
        // Accent dot representing the orange accent from Figma
        Box(
            modifier = Modifier
                .size(iconSize / 4)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(SecondaryMain)
        )
    }
}

@Composable
fun WasteCoLogo(
    modifier: Modifier = Modifier,
    badgeSize: Dp = 40.dp,
    showText: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WasteCoLogoBadge(size = badgeSize)
        if (showText) {
            Spacer(modifier = Modifier.width(Spacing.compact))
            Text(
                text = "WasteCo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
