package com.example.ui.screens.generator

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import com.example.logic.MultipleTicketCalculator
import com.example.logic.StatisticsEngine
import com.example.ui.components.LotteryBall
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandGradientEnd
import com.example.ui.theme.BrandGradientStart
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandWarning
import com.example.ui.viewmodel.LotteryViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    viewModel: LotteryViewModel,
    onNavigateToSavedTickets: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit = {}
) {
    val currentGame by viewModel.currentGame.collectAsStateWithLifecycle()
    val strategy by viewModel.strategy.collectAsStateWithLifecycle()
    val multipleNumberCount by viewModel.multipleNumberCount.collectAsStateWithLifecycle()
    val multipleSecondaryCount by viewModel.multipleSecondaryCount.collectAsStateWithLifecycle()
    val selectedNumbers by viewModel.selectedNumbers.collectAsStateWithLifecycle()
    val selectedSecondaryNumbers by viewModel.selectedSecondaryNumbers.collectAsStateWithLifecycle()
    val savedTickets by viewModel.savedTickets.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.userFeedback.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    val multipleOptions = remember(currentGame) {
        MultipleTicketCalculator.getOptions(currentGame)
    }

    val (liveBets, liveCost, liveError) = remember(currentGame, strategy, multipleNumberCount, multipleSecondaryCount) {
        if (strategy == "multiple") {
            try {
                val bets = MultipleTicketCalculator.calculateTotalBets(currentGame, multipleNumberCount, multipleSecondaryCount)
                val cost = MultipleTicketCalculator.calculateTotalCost(currentGame, multipleNumberCount, multipleSecondaryCount)
                Triple(bets, cost, null)
            } catch (e: Exception) {
                Triple(0, 0.0, e.message)
            }
        } else {
            Triple(1, currentGame.costPerBet, null)
        }
    }

    val isSimplePrimaryComplete = selectedNumbers.size == currentGame.pickCount
    val isSimpleSecondaryComplete = !currentGame.hasSecondaryMatrix ||
            (selectedSecondaryNumbers.size == currentGame.secondaryPickCount)

    val isMultiplePrimaryComplete = selectedNumbers.size == multipleNumberCount
    val isMultipleSecondaryComplete = !currentGame.hasSecondaryMatrix ||
            (selectedSecondaryNumbers.size == multipleSecondaryCount)

    val isComplete = if (strategy == "multiple") {
        isMultiplePrimaryComplete && isMultipleSecondaryComplete
    } else {
        isSimplePrimaryComplete && isSimpleSecondaryComplete
    }

    val sortedSelected = selectedNumbers.toList().sorted()
    val sortedSecondary = selectedSecondaryNumbers.toList().sorted()

    val (qualityScore, qualityValuation) = remember(currentGame, selectedNumbers, selectedSecondaryNumbers) {
        if (selectedNumbers.isNotEmpty()) {
            StatisticsEngine.calculateQualityScore(
                game = currentGame,
                selectedNumbers = selectedNumbers,
                selectedSecondary = selectedSecondaryNumbers
            )
        } else {
            Pair(50, "")
        }
    }

    val gamePrimaryColor by animateColorAsState(
        targetValue = currentGame.primaryColor,
        label = "game_primary_color"
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
                            onClick = onOpenDrawer,
                            modifier = Modifier.testTag("menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
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
                                text = "DataLotto",
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
                                    tint = BrandIndigo
                                )
                            }
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
                        .padding(top = 10.dp, bottom = 6.dp),
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
                                onClick = { viewModel.selectGame(game) },
                                modifier = Modifier.testTag("game_tab_${game.id}"),
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

                // Strategy Selector ("🎲 Simple" / "🎯 Múltiple")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 540.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isSimple = strategy == "simple"
                        val isMultiple = strategy == "multiple"

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSimple) gamePrimaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { viewModel.setStrategy("simple") }
                                .padding(vertical = 8.dp)
                                .testTag("strategy_simple_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🎲 Simple",
                                fontWeight = if (isSimple) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSimple) gamePrimaryColor else Color(0xFF64748B),
                                fontSize = 13.5.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isMultiple) gamePrimaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { viewModel.setStrategy("multiple") }
                                .padding(vertical = 8.dp)
                                .testTag("strategy_multiple_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🎯 Múltiple",
                                fontWeight = if (isMultiple) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMultiple) gamePrimaryColor else Color(0xFF64748B),
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }

                // Scrollable Content: Summary Card + Primary Matrix + Secondary Matrix
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 540.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Multiple Configuration Card (only in multiple strategy)
                    if (strategy == "multiple") {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .testTag("multiple_config_card"),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Configuración de Apuesta Múltiple",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandDark
                                    )

                                    // Primary count selector
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = if (currentGame.hasSecondaryMatrix) "Cantidad de números principales:" else "Cantidad de números a combinar:",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF475569)
                                        )
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            multipleOptions.numberChoices.forEach { count ->
                                                val isSelected = multipleNumberCount == count
                                                FilterChip(
                                                    selected = isSelected,
                                                    onClick = { viewModel.setMultipleNumberCount(count) },
                                                    label = {
                                                        Text(
                                                            text = "$count números",
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                            fontSize = 12.sp
                                                        )
                                                    },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = currentGame.containerColor,
                                                        selectedLabelColor = currentGame.onContainerColor
                                                    ),
                                                    modifier = Modifier.testTag("multiple_chip_$count")
                                                )
                                            }
                                        }
                                    }

                                    // Secondary count selector (Euromillones)
                                    if (currentGame.hasSecondaryMatrix && multipleOptions.secondaryChoices.isNotEmpty()) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = "Cantidad de ${currentGame.secondaryName?.lowercase() ?: "estrellas"}:",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF475569)
                                            )
                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                multipleOptions.secondaryChoices.forEach { secCount ->
                                                    val isSelected = multipleSecondaryCount == secCount
                                                    FilterChip(
                                                        selected = isSelected,
                                                        onClick = { viewModel.setMultipleSecondaryCount(secCount) },
                                                        label = {
                                                            Text(
                                                                text = "$secCount ${currentGame.secondaryEmoji ?: "⭐"}",
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                                fontSize = 12.sp
                                                            )
                                                        },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = currentGame.secondaryContainerColor,
                                                            selectedLabelColor = currentGame.secondaryOnContainerColor
                                                        ),
                                                        modifier = Modifier.testTag("multiple_star_chip_$secCount")
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    HorizontalDivider(color = Color(0xFFF1F5F9))

                                    // Informative Live Bets and Cost Badge
                                    if (liveError != null) {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = BrandDanger.copy(alpha = 0.12f)),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = BrandDanger,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    text = liveError,
                                                    color = BrandDanger,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Combinaciones cubiertas:",
                                                fontSize = 12.5.sp,
                                                color = Color(0xFF64748B),
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "$liveBets apuestas · ${String.format(Locale.getDefault(), "%.2f", liveCost)} €",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Header summary card (Selection status & Live preview of chosen balls)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
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
                                    if (strategy == "multiple") {
                                        if (currentGame.hasSecondaryMatrix) {
                                            Text(
                                                text = "${selectedNumbers.size}/$multipleNumberCount núm. • ${selectedSecondaryNumbers.size}/$multipleSecondaryCount ${currentGame.secondaryName?.lowercase() ?: "estrellas"}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isComplete) gamePrimaryColor else BrandDark
                                            )
                                        } else {
                                            Text(
                                                text = "${selectedNumbers.size}/$multipleNumberCount números seleccionados",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isComplete) gamePrimaryColor else BrandDark
                                            )
                                        }
                                    } else {
                                        if (currentGame.hasSecondaryMatrix) {
                                            Text(
                                                text = "${selectedNumbers.size}/${currentGame.pickCount} núm. • ${selectedSecondaryNumbers.size}/${currentGame.secondaryPickCount} ${currentGame.secondaryName?.lowercase() ?: "estrellas"}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isComplete) gamePrimaryColor else BrandDark
                                            )
                                        } else {
                                            Text(
                                                text = "${selectedNumbers.size}/${currentGame.pickCount} números seleccionados",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isComplete) gamePrimaryColor else BrandDark
                                            )
                                        }
                                    }

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

                                val targetPrimaryCount = if (strategy == "multiple") multipleNumberCount else currentGame.pickCount
                                val targetSecondaryCount = if (strategy == "multiple") multipleSecondaryCount else (if (currentGame.hasSecondaryMatrix) currentGame.secondaryPickCount else 0)
                                val totalPick = targetPrimaryCount + targetSecondaryCount
                                val totalSelected = selectedNumbers.size + if (currentGame.hasSecondaryMatrix) selectedSecondaryNumbers.size else 0

                                LinearProgressIndicator(
                                    progress = { totalSelected.toFloat() / totalPick.coerceAtLeast(1) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = gamePrimaryColor,
                                    trackColor = Color(0xFFE2E8F0)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Balls preview
                                if (strategy == "multiple") {
                                    if (selectedNumbers.isEmpty()) {
                                        Text(
                                            text = "Pulsa \"🎯 Generar Múltiple\" para generar los $multipleNumberCount números al azar",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF64748B),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    } else {
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            sortedSelected.forEach { num ->
                                                LotteryBall(
                                                    number = num,
                                                    isSelected = true,
                                                    size = 36.dp,
                                                    primaryColor = currentGame.primaryColor,
                                                    darkColor = currentGame.darkColor,
                                                    glowColor = currentGame.glowColor
                                                )
                                            }

                                            if (currentGame.hasSecondaryMatrix && sortedSecondary.isNotEmpty()) {
                                                Text(
                                                    text = "+",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp,
                                                    color = currentGame.secondaryPrimaryColor,
                                                    modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 2.dp)
                                                )
                                                sortedSecondary.forEach { secNum ->
                                                    LotteryBall(
                                                        number = secNum,
                                                        isSelected = true,
                                                        size = 36.dp,
                                                        isStar = true,
                                                        primaryColor = currentGame.secondaryPrimaryColor,
                                                        darkColor = currentGame.secondaryDarkColor,
                                                        glowColor = currentGame.secondaryGlowColor
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Balls row placeholder / active numbers for Simple Mode
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Primary balls preview
                                        for (i in 0 until currentGame.pickCount) {
                                            if (i < sortedSelected.size) {
                                                LotteryBall(
                                                    number = sortedSelected[i],
                                                    isSelected = true,
                                                    size = 36.dp,
                                                    primaryColor = currentGame.primaryColor,
                                                    darkColor = currentGame.darkColor,
                                                    glowColor = currentGame.glowColor,
                                                    onClick = { viewModel.toggleNumber(sortedSelected[i]) }
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .background(
                                                            Color(0xFFF1F5F9),
                                                            CircleShape
                                                        )
                                                        .clip(CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "-",
                                                        color = Color(0xFF94A3B8),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }

                                        // Secondary balls preview
                                        if (currentGame.hasSecondaryMatrix) {
                                            Text(
                                                text = "+",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = currentGame.secondaryPrimaryColor,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )

                                            for (i in 0 until currentGame.secondaryPickCount) {
                                                if (i < sortedSecondary.size) {
                                                    LotteryBall(
                                                        number = sortedSecondary[i],
                                                        isSelected = true,
                                                        size = 36.dp,
                                                        isStar = true,
                                                        primaryColor = currentGame.secondaryPrimaryColor,
                                                        darkColor = currentGame.secondaryDarkColor,
                                                        glowColor = currentGame.secondaryGlowColor,
                                                        onClick = { viewModel.toggleSecondaryNumber(sortedSecondary[i]) }
                                                    )
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(
                                                                currentGame.secondaryContainerColor.copy(alpha = 0.5f),
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = null,
                                                            tint = currentGame.secondaryOnContainerColor.copy(alpha = 0.4f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                                if (i < currentGame.secondaryPickCount - 1) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                // Real-time Combination Quality Card (only shown in SIMPLE strategy when at least 1 number is selected)
                if (strategy == "simple" && selectedNumbers.isNotEmpty()) {
                    item {
                            val scoreColor = when {
                                qualityScore >= 80 -> BrandSuccess
                                qualityScore >= 60 -> BrandWarning
                                qualityScore >= 40 -> Color(0xFFF97316)
                                else -> BrandDanger
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .testTag("combination_quality_card"),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(text = "⚡", fontSize = 16.sp)
                                            Text(
                                                text = "Calidad de la Combinación",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandDark
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    scoreColor.copy(alpha = 0.12f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "$qualityScore%",
                                                color = scoreColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    LinearProgressIndicator(
                                        progress = { qualityScore.toFloat() / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = scoreColor,
                                        trackColor = Color(0xFFF1F5F9)
                                    )

                                    Text(
                                        text = qualityValuation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BrandDark,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 16.sp
                                    )

                                    HorizontalDivider(
                                        color = Color(0xFFF1F5F9),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )

                                    Text(
                                        text = "El azar de cada sorteo es independiente; esta puntuación no aumenta tus probabilidades de ganar.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF64748B),
                                        fontSize = 10.5.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Primary Numbers Grid Card (only in Simple Strategy)
                    if (strategy == "simple") {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (currentGame.hasSecondaryMatrix) "Números principales (1-${currentGame.maxNumber})" else "Números (1-${currentGame.maxNumber})",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandDark
                                        )
                                        Text(
                                            text = "${selectedNumbers.size}/${currentGame.pickCount}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSimplePrimaryComplete) gamePrimaryColor else Color(0xFF64748B)
                                        )
                                    }

                                    val numbers = (currentGame.minNumber..currentGame.maxNumber).toList()
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        numbers.forEach { num ->
                                            LotteryBall(
                                                number = num,
                                                isSelected = selectedNumbers.contains(num),
                                                size = 41.dp,
                                                primaryColor = currentGame.primaryColor,
                                                darkColor = currentGame.darkColor,
                                                glowColor = currentGame.glowColor,
                                                onClick = { viewModel.toggleNumber(num) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Secondary Matrix (Stars) when applicable
                        val secMin = currentGame.secondaryMinNumber
                        val secMax = currentGame.secondaryMaxNumber
                        if (currentGame.hasSecondaryMatrix && secMin != null && secMax != null) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp, bottom = 2.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = currentGame.secondaryContainerColor.copy(alpha = 0.45f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "${currentGame.secondaryEmoji ?: "⭐"} ${currentGame.secondaryName ?: "Estrellas"} ($secMin-$secMax)",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = currentGame.secondaryOnContainerColor
                                                )
                                            }

                                            Text(
                                                text = "${selectedSecondaryNumbers.size}/${currentGame.secondaryPickCount}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSimpleSecondaryComplete) currentGame.secondaryDarkColor else Color(0xFF64748B)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        val secondaryNums = (secMin..secMax).toList()
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            secondaryNums.forEach { secNum ->
                                                LotteryBall(
                                                    number = secNum,
                                                    isSelected = selectedSecondaryNumbers.contains(secNum),
                                                    size = 40.dp,
                                                    isStar = true,
                                                    primaryColor = currentGame.secondaryPrimaryColor,
                                                    darkColor = currentGame.secondaryDarkColor,
                                                    glowColor = currentGame.secondaryGlowColor,
                                                    onClick = { viewModel.toggleSecondaryNumber(secNum) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // Action Buttons Panel in solid white card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 540.dp)
                        .padding(vertical = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Generar Combinación Button
                            Button(
                                onClick = {
                                    if (strategy == "multiple") {
                                        viewModel.generateMultipleCombination()
                                    } else {
                                        viewModel.generateCombination()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("generate_button"),
                                enabled = liveError == null,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = gamePrimaryColor
                                )
                            ) {
                                Text(
                                    text = if (strategy == "multiple") "🎯 Generar Múltiple" else "🍀 Generar Combinación",
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
                                enabled = selectedNumbers.isNotEmpty() || selectedSecondaryNumbers.isNotEmpty()
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
                            enabled = isComplete && liveError == null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = gamePrimaryColor,
                                disabledContainerColor = Color(0xFFF1F5F9)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (strategy == "multiple") "💾 Guardar Boleto Múltiple (${currentGame.name})" else "💾 Guardar Boleto (${currentGame.name})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isComplete && liveError == null) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}



