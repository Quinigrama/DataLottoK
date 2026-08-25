package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.logic.GridLayout
import com.example.ui.theme.LocalExtraColors

@Composable
fun NumberGridPanel(
    layout: GridLayout,
    minNumber: Int,
    maxNumber: Int,
    selectedNumbers: Set<Int>,
    onNumberClick: (Int) -> Unit,
    getMarking: (Int) -> BallMarking = { BallMarking.NONE },
    isStar: Boolean = false,
    ballSize: Dp = if (isStar) 44.dp else 40.dp,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    darkColor: Color = Color(0xFF047857),
    glowColor: Color = Color(0xFF34D399),
    modifier: Modifier = Modifier
) {
    val numberRange = maxNumber - minNumber + 1
    val (cols, rows) = layout.getDimensions(numberRange)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFC44A4A).copy(alpha = 0.04f))
            .border(1.dp, Color(0xFFC44A4A).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (r in 0 until rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (c in 0 until cols) {
                        val num = layout.getNumberAtPosition(r, c, numberRange, minNumber)
                        if (num != null) {
                            LotteryBall(
                                number = num,
                                isSelected = selectedNumbers.contains(num),
                                marking = getMarking(num),
                                onClick = { onNumberClick(num) },
                                size = ballSize,
                                isStar = isStar,
                                primaryColor = primaryColor,
                                darkColor = darkColor,
                                glowColor = glowColor
                            )
                        } else {
                            // Espacio vacío para mantener la alineación de la cuadrícula
                            Spacer(modifier = Modifier.size(ballSize))
                        }
                    }
                }
            }
        }
    }
}
