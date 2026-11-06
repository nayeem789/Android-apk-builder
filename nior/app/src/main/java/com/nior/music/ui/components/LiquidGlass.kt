package com.nior.music.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nior.music.ui.theme.*

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = GlassSurfaceDark,
    borderColor: Color = GlassBorderDark,
    elevation: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(elevation, shape, ambientColor = Color.Black, spotColor = NiorPurple)
            .clip(shape)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            borderColor,
                            Color(0x11FFFFFF),
                            borderColor.copy(alpha = 0.15f)
                        )
                    )
                ),
                shape
            ),
        color = backgroundColor,
        shape = shape
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun LiquidGlassPill(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    accentColor: Color = NiorPurple,
    shape: Shape = RoundedCornerShape(50),
    content: @Composable BoxScope.() -> Unit
) {
    val bgBrush = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.40f),
                accentColor.copy(alpha = 0.20f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x1AFFFFFF),
                Color(0x0AFFFFFF)
            )
        )
    }

    val borderBrush = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.6f),
                Color(0x44FFFFFF)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x22FFFFFF),
                Color(0x11FFFFFF)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgBrush)
            .border(1.dp, borderBrush, shape)
    ) {
        content()
    }
}
