package com.example.ui.screens.generator

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GameConfig
import com.example.ui.components.LotteryBall
import com.example.ui.viewmodel.LotteryViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    viewModel: LotteryViewModel,
    onNavigateToSavedTickets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentGame by viewModel.currentGame.collectAsStateWithLifecycle()
    val selectedNumbers by viewModel.selectedNumbers.collectAsStateWithLifecycle()
    val savedTickets by viewModel.savedTickets.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.userFeedback.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    val isComplete = selectedNumbers.size == currentGame.pickCount
    val sortedSelected = selectedNumbers.toList().sorted()

    val gamePrimaryColor by animateColorAsState(
        targetValue = currentGame.primaryColor,
        label = "game_primary_color"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DataLotto",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${currentGame.flagEmoji} ${currentGame.name}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = currentGame.onContainerColor,
                            modifier = Modifier
                                .background(
                                    currentGame.containerColor,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSavedTickets,
                        modifier = Modifier.testTag("nav_to_tickets_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (savedTickets.isNotEmpty()) {
                                    Badge(
                                        containerColor = gamePrimaryColor,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = savedTickets.size.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Mis Boletos",
                                tint = gamePrimaryColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game Selector Tabs (Bonoloto vs La Primitiva)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp)
                    .padding(top = 4.dp, bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                TabRow(
                    selectedTabIndex = GameConfig.AvailableGames.indexOfFirst { it.id == currentGame.id }.coerceAtLeast(0),
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        val selectedIndex = GameConfig.AvailableGames.indexOfFirst { it.id == currentGame.id }.coerceAtLeast(0)
                        if (selectedIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                                color = gamePrimaryColor,
                                height = 3.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    GameConfig.AvailableGames.forEach { game ->
                        val isSelected = currentGame.id == game.id
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectGame(game) },
                            modifier = Modifier.testTag("game_tab_${game.id}"),
                            text = {
                                Text(
                                    text = "${game.flagEmoji} ${game.name}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) game.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            // Header summary card (Selection status & Live preview of chosen balls)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp)
                    .padding(bottom = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = currentGame.containerColor.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedNumbers.size}/${currentGame.pickCount} números seleccionados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isComplete) gamePrimaryColor else MaterialTheme.colorScheme.onSurface
                        )
                        if (isComplete) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completo",
                                    tint = gamePrimaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "¡Listo!",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = gamePrimaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { selectedNumbers.size.toFloat() / currentGame.pickCount },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = gamePrimaryColor,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Balls row placeholder / active numbers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until currentGame.pickCount) {
                            if (i < sortedSelected.size) {
                                LotteryBall(
                                    number = sortedSelected[i],
                                    isSelected = true,
                                    size = 38.dp,
                                    primaryColor = currentGame.primaryColor,
                                    darkColor = currentGame.darkColor,
                                    glowColor = currentGame.glowColor,
                                    onClick = { viewModel.toggleNumber(sortedSelected[i]) }
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            CircleShape
                                        )
                                        .clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "-",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Grid of Numbers (minNumber..maxNumber)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 540.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items((currentGame.minNumber..currentGame.maxNumber).toList()) { num ->
                    LotteryBall(
                        number = num,
                        isSelected = selectedNumbers.contains(num),
                        size = 42.dp,
                        primaryColor = currentGame.primaryColor,
                        darkColor = currentGame.darkColor,
                        glowColor = currentGame.glowColor,
                        onClick = { viewModel.toggleNumber(num) }
                    )
                }
            }

            // Action Buttons Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp)
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Generar Combinación Button
                    Button(
                        onClick = { viewModel.generateCombination() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("generate_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = gamePrimaryColor
                        )
                    ) {
                        Text(
                            text = "🍀 Generar Combinación",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Limpiar Button
                    OutlinedButton(
                        onClick = { viewModel.clearSelection() },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("clear_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = selectedNumbers.isNotEmpty()
                    ) {
                        Text(
                            text = "🗑️ Limpiar",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Guardar Boleto Button
                Button(
                    onClick = { viewModel.saveCurrentTicket() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_ticket_button"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gamePrimaryColor,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💾 Guardar Boleto (${currentGame.name})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isComplete) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

