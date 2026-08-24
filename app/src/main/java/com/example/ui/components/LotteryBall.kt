package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
    isStar: Boolean = false,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    darkColor: Color = Color(0xFF047857),
    glowColor: Color = Color(0xFF34D399),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ball_pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.05f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball_pulse_scale"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) pulseScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ball_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFFD700) else MaterialTheme.colorScheme.surfaceVariant,
        label = "ball_bg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "ball_text"
    )

    val shape = if (isStar) RoundedCornerShape(10.dp) else CircleShape

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Checkbox,
            onClickLabel = if (isSelected) {
                if (isStar) "Deseleccionar estrella $number" else "Deseleccionar número $number"
            } else {
                if (isStar) "Seleccionar estrella $number" else "Seleccionar número $number"
            },
            onClick = onClick
        )
    } else Modifier

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .testTag(if (isStar) "star_ball_$number" else "number_ball_$number")
            .size(size)
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 4.dp else 1.dp,
                shape = shape,
                clip = false
            )
            .clip(shape)
            .then(
                if (isSelected) {
                    Modifier.background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                        )
                    )
                } else {
                    Modifier.background(backgroundColor)
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    Color(0xFFFF8F00)
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                },
                shape = shape
            )
            .then(clickModifier)
    ) {
        if (isStar && isSelected) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(if (size > 36.dp) 12.dp else 9.dp)
                )
                Spacer(modifier = Modifier.width(1.dp))
                Text(
                    text = number.toString(),
                    color = textColor,
                    fontSize = if (size > 40.dp) 15.sp else 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = number.toString(),
                color = textColor,
                fontSize = if (size > 40.dp) 16.sp else 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

