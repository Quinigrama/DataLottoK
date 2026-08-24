package com.example.ui.screens.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GameConfig
import com.example.data.model.SavedTicket
import com.example.ui.components.LotteryBall
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandGradientEnd
import com.example.ui.theme.BrandGradientStart
import com.example.ui.theme.BrandIndigo
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
                    title = {
                        Text(
                            text = "Mis Boletos Guardados",
                            fontWeight = FontWeight.Bold,
                            color = BrandDark
                        )
                    },
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
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
                        message = "No tienes boletos guardados",
                        description = "Genera o elige tus números en la pantalla principal y pulsa \"Guardar Boleto\" para guardarlos aquí.",
                        buttonText = "🍀 Ir al Generador",
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
                        // Filter Chips in a solid white card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
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
                                                text = "Todos (${savedTickets.size})",
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
                                message = "No hay boletos de este juego",
                                description = "No has guardado combinaciones para este filtro todavía.",
                                buttonText = "Mostrar todos los boletos",
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
                                        onDelete = { viewModel.deleteTicket(ticket) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(
    ticket: SavedTicket,
    onDelete: () -> Unit,
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ticket_card_${ticket.id}"),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
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
                        contentDescription = "Eliminar boleto",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Balls representation with game specific colors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sortedNumbers = ticket.numbers.sorted()
                sortedNumbers.forEachIndexed { index, num ->
                    LotteryBall(
                        number = num,
                        isSelected = true,
                        size = if (ticket.secondaryNumbers.isNotEmpty()) 36.dp else 40.dp,
                        primaryColor = gameConfig.primaryColor,
                        darkColor = gameConfig.darkColor,
                        glowColor = gameConfig.glowColor
                    )
                    if (index < sortedNumbers.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                if (ticket.secondaryNumbers.isNotEmpty()) {
                    Text(
                        text = "+",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = gameConfig.secondaryPrimaryColor,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    val sortedSecondary = ticket.secondaryNumbers.sorted()
                    sortedSecondary.forEachIndexed { index, secNum ->
                        LotteryBall(
                            number = secNum,
                            isSelected = true,
                            size = 36.dp,
                            isStar = true,
                            primaryColor = gameConfig.secondaryPrimaryColor,
                            darkColor = gameConfig.secondaryDarkColor,
                            glowColor = gameConfig.secondaryGlowColor
                        )
                        if (index < sortedSecondary.size - 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
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
            containerColor = Color.White
        )
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
                    .background(Color(0xFFEEF2FF)),
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
                color = BrandDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
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


