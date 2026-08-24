package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LotteryBall(
    number: Int,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    size: Dp = 44.dp,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    darkColor: Color = Color(0xFF047857),
    glowColor: Color = Color(0xFF34D399),
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ball_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant,
        label = "ball_bg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "ball_text"
    )

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Checkbox,
            onClickLabel = if (isSelected) "Deseleccionar número $number" else "Seleccionar número $number",
            onClick = onClick
        )
    } else Modifier

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .testTag("number_ball_$number")
            .size(size)
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 4.dp else 1.dp,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.background(
                        Brush.radialGradient(
                            colors = listOf(glowColor, darkColor),
                            radius = 60f
                        )
                    )
                } else {
                    Modifier.background(backgroundColor)
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .then(clickModifier)
    ) {
        Text(
            text = number.toString(),
            color = textColor,
            fontSize = if (size > 40.dp) 16.sp else 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
