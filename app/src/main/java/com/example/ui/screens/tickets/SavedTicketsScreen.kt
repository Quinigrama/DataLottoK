package com.example.ui.screens.tickets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GameConfig
import com.example.data.model.SavedTicket
import com.example.logic.ReducedSystemCalculator
import com.example.ui.components.LotteryBall
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandWarning
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.tr
import com.example.ui.viewmodel.LotteryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedTicketsScreen(
    viewModel: LotteryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedTickets by viewModel.savedTickets.collectAsStateWithLifecycle()
    val filterGameId by viewModel.ticketFilterGameId.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.userFeedback.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val ticketBeingValidated by viewModel.ticketBeingValidated.collectAsStateWithLifecycle()
    val winningNumbers by viewModel.winningNumbers.collectAsStateWithLifecycle()
    val winningSecondaryNumbers by viewModel.winningSecondaryNumbers.collectAsStateWithLifecycle()

    val extraColors = LocalExtraColors.current

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    val filteredTickets = remember(savedTickets, filterGameId) {
        if (filterGameId == null) {
            savedTickets
        } else {
            savedTickets.filter { it.gameId.equals(filterGameId, ignoreCase = true) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(extraColors.gradientStart, extraColors.gradientEnd),
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
                    title = {
                        Text(
                            text = tr("Mis Boletos Guardados", "My Saved Tickets"),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = tr("Volver", "Back"),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.shadow(4.dp)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                if (savedTickets.isEmpty()) {
                    EmptyTicketsView(
                        message = tr("No tienes boletos guardados", "You have no saved tickets"),
                        description = tr("Genera o elige tus números en la pantalla principal y pulsa \"Guardar Boleto\" para guardarlos aquí.", "Generate or pick your numbers on the main screen and tap \"Save Ticket\" to keep them here."),
                        buttonText = tr("🍀 Ir al Generador", "🍀 Go to Generator"),
                        onButtonClick = onNavigateBack,
                        modifier = Modifier.padding(24.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 600.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Filter Chips in a surface card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, extraColors.cardBorder)
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    val isSelected = filterGameId == null
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setTicketFilter(null) },
                                        label = {
                                            Text(
                                                text = "${tr("Todos", "All")} (${savedTickets.size})",
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        modifier = Modifier.testTag("filter_chip_all")
                                    )
                                }

                                items(GameConfig.AvailableGames) { game ->
                                    val isSelected = filterGameId == game.id
                                    val gameCount = savedTickets.count { it.gameId.equals(game.id, ignoreCase = true) }
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setTicketFilter(if (isSelected) null else game.id) },
                                        label = {
                                            Text(
                                                text = "${game.flagEmoji} ${game.name} ($gameCount)",
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = game.containerColor,
                                            selectedLabelColor = game.onContainerColor,
                                            selectedLeadingIconColor = game.onContainerColor
                                        ),
                                        modifier = Modifier.testTag("filter_chip_${game.id}")
                                    )
                                }
                            }
                        }

                        if (filteredTickets.isEmpty()) {
                            EmptyTicketsView(
                                message = tr("No hay boletos de este juego", "No tickets for this game"),
                                description = tr("No has guardado combinaciones para este filtro todavía.", "You haven't saved combinations for this filter yet."),
                                buttonText = tr("Mostrar todos los boletos", "Show all tickets"),
                                onButtonClick = { viewModel.setTicketFilter(null) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = filteredTickets,
                                    key = { it.id }
                                ) { ticket ->
                                    TicketCard(
                                        ticket = ticket,
                                        onDelete = { viewModel.deleteTicket(ticket) },
                                        onValidate = { viewModel.openValidationDialog(ticket) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog de Validación Manual
        ticketBeingValidated?.let { ticket ->
            TicketValidationDialog(
                ticket = ticket,
                winningNumbers = winningNumbers,
                winningSecondaryNumbers = winningSecondaryNumbers,
                onToggleWinningNumber = { num, maxCount ->
                    viewModel.toggleWinningNumber(num, maxCount)
                },
                onToggleWinningSecondaryNumber = { num, maxCount ->
                    viewModel.toggleWinningSecondaryNumber(num, maxCount)
                },
                onConfirm = {
                    viewModel.validateTicket(
                        ticket = ticket,
                        winningNumbers = winningNumbers.toList().sorted(),
                        winningSecondary = winningSecondaryNumbers.toList().sorted()
                    )
                },
                onDismiss = {
                    viewModel.closeValidationDialog()
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TicketCard(
    ticket: SavedTicket,
    onDelete: () -> Unit,
    onValidate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameConfig = remember(ticket.gameId) {
        GameConfig.findById(ticket.gameId)
    }

    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())
    }
    val formattedDate = remember(ticket.timestamp) {
        dateFormat.format(Date(ticket.timestamp))
    }

    val isWinning = ticket.isValidated && ticket.prizeLabel != null && !ticket.prizeLabel.startsWith("Sin premio")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ticket_card_${ticket.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, LocalExtraColors.current.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Game badge, Date, and Delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                gameConfig.containerColor,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${gameConfig.flagEmoji} ${ticket.gameName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = gameConfig.onContainerColor
                        )
                    }

                    if (ticket.strategy == "multiple") {
                        Box(
                            modifier = Modifier
                                .background(
                                    BrandIndigo.copy(alpha = 0.12f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (ticket.secondaryNumbers.isNotEmpty()) tr("🎯 Múltiple de ${ticket.numbers.size}+${ticket.secondaryNumbers.size}⭐", "🎯 Multiple of ${ticket.numbers.size}+${ticket.secondaryNumbers.size}⭐") else tr("🎯 Múltiple de ${ticket.numbers.size}", "🎯 Multiple of ${ticket.numbers.size}"),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandIndigo
                            )
                        }
                    } else if (ticket.strategy == "reducida") {
                        val system = ReducedSystemCalculator.findSystem(ticket.gameId, ticket.systemId)
                        val label = if (system != null) {
                            tr("🧩 Reducida de ${system.baseNumbersCount} (${system.combinationsCount} apuestas)", "🧩 Reduced of ${system.baseNumbersCount} (${system.combinationsCount} bets)")
                        } else {
                            tr("🧩 Sistema Reducido", "🧩 Reduced System")
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    BrandIndigo.copy(alpha = 0.12f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandIndigo
                            )
                        }
                    }

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_ticket_${ticket.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = tr("Eliminar boleto", "Delete ticket"),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Balls representation with game specific colors
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val sortedNumbers = ticket.numbers.sorted()
                sortedNumbers.forEach { num ->
                    LotteryBall(
                        number = num,
                        isSelected = true,
                        size = if (sortedNumbers.size > 7 || ticket.secondaryNumbers.isNotEmpty()) 34.dp else 38.dp,
                        primaryColor = gameConfig.primaryColor,
                        darkColor = gameConfig.darkColor,
                        glowColor = gameConfig.glowColor
                    )
                }

                if (ticket.secondaryNumbers.isNotEmpty()) {
                    Text(
                        text = "+",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = gameConfig.secondaryPrimaryColor,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 4.dp)
                    )

                    val sortedSecondary = ticket.secondaryNumbers.sorted()
                    sortedSecondary.forEach { secNum ->
                        LotteryBall(
                            number = secNum,
                            isSelected = true,
                            size = 34.dp,
                            isStar = true,
                            primaryColor = gameConfig.secondaryPrimaryColor,
                            darkColor = gameConfig.secondaryDarkColor,
                            glowColor = gameConfig.secondaryGlowColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Validation status and Action
            if (ticket.strategy == "multiple" || ticket.strategy == "reducida") {
                val infoText = if (ticket.strategy == "multiple") {
                    tr("ℹ️ La validación de boletos múltiples llegará en una fase posterior", "ℹ️ Multiple ticket validation will arrive in a later phase")
                } else {
                    tr("ℹ️ La validación de boletos con sistema reducido llegará en una fase posterior", "ℹ️ Reduced system ticket validation will arrive in a later phase")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = infoText,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (ticket.isValidated) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isWinning) BrandSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isWinning) Icons.Default.EmojiEvents else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isWinning) BrandSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = tr("✓ Validado", "✓ Validated"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isWinning) BrandSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = if (ticket.prizeLabel == "Sin premio") tr("Sin premio", "No prize") else (ticket.prizeLabel ?: tr("Sin premio", "No prize")),
                                fontSize = 12.sp,
                                fontWeight = if (isWinning) FontWeight.Bold else FontWeight.Medium,
                                color = if (isWinning) BrandSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        // Re-validar button
                        IconButton(
                            onClick = onValidate,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("revalidate_ticket_${ticket.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = tr("Re-validar", "Re-validate"),
                                tint = BrandIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Text(
                            text = tr("Pendiente de validación", "Pending validation"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = onValidate,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandIndigo
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier
                                .testTag("validate_ticket_${ticket.id}")
                                .height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tr("Validar", "Validate"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TicketValidationDialog(
    ticket: SavedTicket,
    winningNumbers: Set<Int>,
    winningSecondaryNumbers: Set<Int>,
    onToggleWinningNumber: (Int, Int) -> Unit,
    onToggleWinningSecondaryNumber: (Int, Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val gameConfig = remember(ticket.gameId) {
        GameConfig.findById(ticket.gameId)
    }

    val isPrimaryComplete = winningNumbers.size == gameConfig.pickCount
    val isSecondaryComplete = !gameConfig.hasSecondaryMatrix || winningSecondaryNumbers.size == gameConfig.secondaryPickCount
    val isValidationReady = isPrimaryComplete && isSecondaryComplete

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .testTag("validation_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.dp, LocalExtraColors.current.cardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dialog Title Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = tr("Validar Boleto", "Validate Ticket"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${gameConfig.flagEmoji} ${gameConfig.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = gameConfig.primaryColor
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_validation_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = tr("Cerrar", "Close"),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Official Draw Days Notice (No bloqueante)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, LocalExtraColors.current.cardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = BrandIndigo,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = tr("Días de sorteo oficiales", "Official draw days"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandIndigo
                            )
                            Text(
                                text = tr("Este juego sortea: ${gameConfig.formatDrawDays()}", "This game draws on: ${gameConfig.formatDrawDays()}"),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Content for Winning Numbers Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Played combination preview
                    Text(
                        text = tr("Tu combinación guardada:", "Your saved combination:"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ticket.numbers.sorted().forEach { num ->
                            LotteryBall(
                                number = num,
                                size = 32.dp,
                                primaryColor = gameConfig.primaryColor,
                                darkColor = gameConfig.darkColor,
                                glowColor = gameConfig.glowColor
                            )
                        }

                        if (ticket.secondaryNumbers.isNotEmpty()) {
                            Text(
                                text = "+",
                                fontWeight = FontWeight.Bold,
                                color = gameConfig.secondaryPrimaryColor
                            )
                            ticket.secondaryNumbers.sorted().forEach { secNum ->
                                LotteryBall(
                                    number = secNum,
                                    size = 32.dp,
                                    isStar = true,
                                    primaryColor = gameConfig.secondaryPrimaryColor,
                                    darkColor = gameConfig.secondaryDarkColor,
                                    glowColor = gameConfig.secondaryGlowColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary winning numbers section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tr("Números Ganadores:", "Winning Numbers:"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${winningNumbers.size} / ${gameConfig.pickCount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isPrimaryComplete) BrandSuccess else BrandIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Primary numbers grid
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in gameConfig.minNumber..gameConfig.maxNumber) {
                            val isSelected = winningNumbers.contains(i)
                            LotteryBall(
                                number = i,
                                isSelected = isSelected,
                                size = 38.dp,
                                primaryColor = gameConfig.primaryColor,
                                darkColor = gameConfig.darkColor,
                                glowColor = gameConfig.glowColor,
                                onClick = {
                                    onToggleWinningNumber(i, gameConfig.pickCount)
                                }
                            )
                        }
                    }

                    // Secondary winning numbers section (Euromillones)
                    if (gameConfig.hasSecondaryMatrix && gameConfig.secondaryMaxNumber != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tr("${gameConfig.secondaryEmoji ?: "⭐"} ${gameConfig.secondaryName ?: "Estrellas"} Ganadoras:", "${gameConfig.secondaryEmoji ?: "⭐"} Winning ${gameConfig.secondaryName ?: "Stars"}:"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${winningSecondaryNumbers.size} / ${gameConfig.secondaryPickCount}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSecondaryComplete) BrandSuccess else gameConfig.secondaryDarkColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (i in (gameConfig.secondaryMinNumber ?: 1)..gameConfig.secondaryMaxNumber) {
                                val isSelected = winningSecondaryNumbers.contains(i)
                                LotteryBall(
                                    number = i,
                                    isSelected = isSelected,
                                    size = 38.dp,
                                    isStar = true,
                                    primaryColor = gameConfig.secondaryPrimaryColor,
                                    darkColor = gameConfig.secondaryDarkColor,
                                    glowColor = gameConfig.secondaryGlowColor,
                                    onClick = {
                                        onToggleWinningSecondaryNumber(i, gameConfig.secondaryPickCount)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("cancel_validation_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(tr("Cancelar", "Cancel"), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = isValidationReady,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandIndigo
                        ),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("confirm_validation_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tr("Confirmar Validación", "Confirm Validation"),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTicketsView(
    message: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, LocalExtraColors.current.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = BrandIndigo,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandIndigo
                ),
                modifier = Modifier.testTag("empty_action_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}




