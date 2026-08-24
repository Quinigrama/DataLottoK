package com.example.ui.screens.stats

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GameConfig
import com.example.logic.FrequencyEntry
import com.example.logic.NumberClassification
import com.example.logic.StatisticsEngine
import com.example.ui.components.LotteryBall
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandGradientEnd
import com.example.ui.theme.BrandGradientStart
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandWarning
import com.example.ui.viewmodel.LotteryViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: LotteryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentGame by viewModel.currentGame.collectAsStateWithLifecycle()
    val simulatedDraws by viewModel.simulatedDraws.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.userFeedback.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Números principales, 1 = Estrellas (si Euromillones)

    val mainFrequencies = remember(simulatedDraws, currentGame) {
        if (simulatedDraws.isNotEmpty()) {
            StatisticsEngine.computeFrequencies(simulatedDraws, currentGame, isSecondary = false)
        } else {
            emptyList()
        }
    }

    val starFrequencies = remember(simulatedDraws, currentGame) {
        if (simulatedDraws.isNotEmpty() && currentGame.hasSecondaryMatrix) {
            StatisticsEngine.computeFrequencies(simulatedDraws, currentGame, isSecondary = true)
        } else {
            emptyList()
        }
    }

    val activeList = if (selectedTab == 1 && currentGame.hasSecondaryMatrix) starFrequencies else mainFrequencies
    val maxCountInActive = remember(activeList) {
        activeList.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    }

    val gamePrimaryColor by animateColorAsState(
        targetValue = currentGame.primaryColor,
        label = "stats_game_color"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandGradientStart, BrandGradientEnd),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = BrandDark
                            )
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📊 Estadísticas",
                                fontWeight = FontWeight.Bold,
                                color = BrandDark
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.shadow(4.dp)
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
                // Game Selector Tabs (Bonoloto, La Primitiva, Euromillones)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 540.dp)
                        .padding(top = 10.dp, bottom = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
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
                                onClick = {
                                    viewModel.selectGame(game)
                                    selectedTab = 0
                                },
                                modifier = Modifier.testTag("stats_game_tab_${game.id}"),
                                text = {
                                    Text(
                                        text = "${game.flagEmoji} ${game.name}",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        color = if (isSelected) game.primaryColor else Color(0xFF64748B)
                                    )
                                }
                            )
                        }
                    }
                }

                // Disclaimer / Aviso fijo sobre simulación y azar independiente
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 540.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF3C7)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Estas frecuencias son sobre datos simulados aleatoriamente, no sorteos reales. La frecuencia pasada no predice el próximo sorteo.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF78350F),
                            lineHeight = 14.sp
                        )
                    }
                }

                // Control Card: Simular 200 Sorteos
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 540.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Generador de Frecuencias",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandDark
                                )
                                Text(
                                    text = if (simulatedDraws.isNotEmpty()) "${simulatedDraws.size} sorteos analizados" else "Sin sorteos generados",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Button(
                                onClick = { viewModel.simulateDraws(200) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = gamePrimaryColor
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("simulate_draws_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🎲 Simular 200 Sorteos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Legend
                        if (simulatedDraws.isNotEmpty()) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "🔥", fontSize = 12.sp)
                                    Text(
                                        text = "Caliente (≥ p70)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandDark
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "🧊", fontSize = 12.sp)
                                    Text(
                                        text = "Frío (≤ p30)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandDark
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF94A3B8), CircleShape)
                                    )
                                    Text(
                                        text = "Neutro",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandDark
                                    )
                                }
                            }
                        }
                    }
                }

                // Sub-tabs for Euromillones (Números principales / Estrellas)
                if (simulatedDraws.isNotEmpty() && currentGame.hasSecondaryMatrix) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 540.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            indicator = { tabPositions ->
                                if (selectedTab < tabPositions.size) {
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                        color = if (selectedTab == 1) currentGame.secondaryDarkColor else gamePrimaryColor,
                                        height = 2.5.dp
                                    )
                                }
                            },
                            divider = {}
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = {
                                    Text(
                                        text = "Números Principales (1-${currentGame.maxNumber})",
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (selectedTab == 0) gamePrimaryColor else Color(0xFF64748B)
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = {
                                    Text(
                                        text = "${currentGame.secondaryEmoji ?: "⭐"} ${currentGame.secondaryName ?: "Estrellas"} (1-${currentGame.secondaryMaxNumber ?: 12})",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (selectedTab == 1) currentGame.secondaryDarkColor else Color(0xFF64748B)
                                    )
                                }
                            )
                        }
                    }
                }

                // Content: Empty State vs Frequency List
                if (simulatedDraws.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 540.dp)
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(
                                        gamePrimaryColor.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = gamePrimaryColor,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Sin Datos Estadísticos",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrandDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Simula sorteos para ver las frecuencias de aparición y los números calientes / fríos para ${currentGame.name}.",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.simulateDraws(200) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = gamePrimaryColor
                                ),
                                modifier = Modifier.testTag("empty_simulate_draws_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🎲 Simular 200 Sorteos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    // List of Frequencies
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 540.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(activeList, key = { "${it.isStar}_${it.number}" }) { entry ->
                            FrequencyCard(
                                entry = entry,
                                gameConfig = currentGame,
                                maxCount = maxCountInActive
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FrequencyCard(
    entry: FrequencyEntry,
    gameConfig: GameConfig,
    maxCount: Int,
    modifier: Modifier = Modifier
) {
    val isStar = entry.isStar

    val (badgeText, badgeBg, badgeTextColor) = when (entry.classification) {
        NumberClassification.HOT -> Triple(
            "🔥 Caliente",
            if (isStar) gameConfig.secondaryContainerColor else gameConfig.containerColor,
            if (isStar) gameConfig.secondaryOnContainerColor else gameConfig.onContainerColor
        )
        NumberClassification.COLD -> Triple(
            "🧊 Frío",
            Color(0xFFE2E8F0),
            Color(0xFF334155)
        )
        NumberClassification.NEUTRAL -> Triple(
            "Neutro",
            Color(0xFFF8FAFC),
            Color(0xFF94A3B8)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("freq_card_${if (isStar) "star_" else ""}${entry.number}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Ball display
            LotteryBall(
                number = entry.number,
                isSelected = true,
                size = 36.dp,
                isStar = isStar,
                primaryColor = if (isStar) gameConfig.secondaryPrimaryColor else gameConfig.primaryColor,
                darkColor = if (isStar) gameConfig.secondaryDarkColor else gameConfig.darkColor,
                glowColor = if (isStar) gameConfig.secondaryGlowColor else gameConfig.glowColor
            )

            // Counts and bar
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Número ${entry.number}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BrandDark
                    )

                    Text(
                        text = "${entry.count} apariciones (${String.format(Locale.getDefault(), "%.1f", entry.percentage)}%)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }

                LinearProgressIndicator(
                    progress = { entry.count.toFloat() / maxCount.coerceAtLeast(1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp)),
                    color = when (entry.classification) {
                        NumberClassification.HOT -> if (isStar) gameConfig.secondaryDarkColor else gameConfig.primaryColor
                        NumberClassification.COLD -> Color(0xFF94A3B8)
                        NumberClassification.NEUTRAL -> Color(0xFFCBD5E1)
                    },
                    trackColor = Color(0xFFF1F5F9)
                )
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .background(badgeBg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }
        }
    }
}
