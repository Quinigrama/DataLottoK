package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.GameConfig
import com.example.data.model.SavedTicket
import com.example.data.repository.TicketRepository
import com.example.logic.LotteryGenerator
import com.example.logic.PrizeCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LotteryViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    private val _currentGame = MutableStateFlow(GameConfig.Bonoloto)
    val currentGame: StateFlow<GameConfig> = _currentGame.asStateFlow()

    private val _selectedNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val selectedNumbers: StateFlow<Set<Int>> = _selectedNumbers.asStateFlow()

    private val _selectedSecondaryNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val selectedSecondaryNumbers: StateFlow<Set<Int>> = _selectedSecondaryNumbers.asStateFlow()

    private val _ticketFilterGameId = MutableStateFlow<String?>(null)
    val ticketFilterGameId: StateFlow<String?> = _ticketFilterGameId.asStateFlow()

    val savedTickets: StateFlow<List<SavedTicket>> = repository.allTickets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    // Estado para validación de boletos
    private val _ticketBeingValidated = MutableStateFlow<SavedTicket?>(null)
    val ticketBeingValidated: StateFlow<SavedTicket?> = _ticketBeingValidated.asStateFlow()

    private val _winningNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val winningNumbers: StateFlow<Set<Int>> = _winningNumbers.asStateFlow()

    private val _winningSecondaryNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val winningSecondaryNumbers: StateFlow<Set<Int>> = _winningSecondaryNumbers.asStateFlow()

    fun selectGame(game: GameConfig) {
        if (_currentGame.value.id != game.id) {
            _currentGame.value = game
            _selectedNumbers.value = emptySet()
            _selectedSecondaryNumbers.value = emptySet()
            _userFeedback.value = "Cambiado a ${game.name}"
        }
    }

    fun setTicketFilter(gameId: String?) {
        _ticketFilterGameId.value = gameId
    }

    fun toggleNumber(number: Int) {
        val currentSet = _selectedNumbers.value
        val game = _currentGame.value

        if (currentSet.contains(number)) {
            _selectedNumbers.value = currentSet - number
        } else {
            if (currentSet.size < game.pickCount) {
                _selectedNumbers.value = currentSet + number
            } else {
                _userFeedback.value = "Ya has seleccionado ${game.pickCount} números"
            }
        }
    }

    fun toggleSecondaryNumber(number: Int) {
        val game = _currentGame.value
        if (!game.hasSecondaryMatrix) return

        val currentSecondary = _selectedSecondaryNumbers.value
        val maxSecondary = game.secondaryPickCount
        val name = game.secondaryName ?: "números secundarios"

        if (currentSecondary.contains(number)) {
            _selectedSecondaryNumbers.value = currentSecondary - number
        } else {
            if (currentSecondary.size < maxSecondary) {
                _selectedSecondaryNumbers.value = currentSecondary + number
            } else {
                _userFeedback.value = "Ya has seleccionado $maxSecondary $name"
            }
        }
    }

    fun generateCombination() {
        val game = _currentGame.value
        val result = LotteryGenerator.generateSimple(game)
        _selectedNumbers.value = result.primaryNumbers.toSet()
        _selectedSecondaryNumbers.value = result.secondaryNumbers.toSet()
        _userFeedback.value = "🍀 Combinación generada para ${game.name}"
    }

    fun clearSelection() {
        _selectedNumbers.value = emptySet()
        _selectedSecondaryNumbers.value = emptySet()
    }

    fun saveCurrentTicket(onSuccess: () -> Unit = {}) {
        val game = _currentGame.value
        val numbers = _selectedNumbers.value.toList().sorted()
        val secondaryNumbers = _selectedSecondaryNumbers.value.toList().sorted()

        if (numbers.size != game.pickCount) {
            _userFeedback.value = "Debes seleccionar exactamente ${game.pickCount} números para guardar el boleto"
            return
        }

        if (game.hasSecondaryMatrix && secondaryNumbers.size != game.secondaryPickCount) {
            val name = game.secondaryName ?: "estrellas"
            _userFeedback.value = "Debes seleccionar exactamente ${game.secondaryPickCount} $name para guardar el boleto"
            return
        }

        viewModelScope.launch {
            val ticket = SavedTicket(
                gameId = game.id,
                gameName = game.name,
                numbers = numbers,
                secondaryNumbers = if (game.hasSecondaryMatrix) secondaryNumbers else emptyList(),
                timestamp = System.currentTimeMillis()
            )
            repository.saveTicket(ticket)
            _userFeedback.value = "💾 ¡Boleto de ${game.name} guardado con éxito!"
            onSuccess()
        }
    }

    fun deleteTicket(ticket: SavedTicket) {
        viewModelScope.launch {
            repository.deleteTicket(ticket)
            _userFeedback.value = "Boleto eliminado"
        }
    }

    // Métodos para validación manual de boletos
    fun openValidationDialog(ticket: SavedTicket) {
        _ticketBeingValidated.value = ticket
        _winningNumbers.value = emptySet()
        _winningSecondaryNumbers.value = emptySet()
    }

    fun closeValidationDialog() {
        _ticketBeingValidated.value = null
        _winningNumbers.value = emptySet()
        _winningSecondaryNumbers.value = emptySet()
    }

    fun toggleWinningNumber(number: Int, maxCount: Int) {
        val current = _winningNumbers.value
        if (current.contains(number)) {
            _winningNumbers.value = current - number
        } else {
            if (current.size < maxCount) {
                _winningNumbers.value = current + number
            }
        }
    }

    fun toggleWinningSecondaryNumber(number: Int, maxCount: Int) {
        val current = _winningSecondaryNumbers.value
        if (current.contains(number)) {
            _winningSecondaryNumbers.value = current - number
        } else {
            if (current.size < maxCount) {
                _winningSecondaryNumbers.value = current + number
            }
        }
    }

    fun validateTicket(
        ticket: SavedTicket,
        winningNumbers: List<Int>,
        winningSecondary: List<Int>
    ) {
        val game = GameConfig.findById(ticket.gameId)
        val winningNumbersSet = winningNumbers.toSet()
        val ticketNumbersSet = ticket.numbers.toSet()
        val hitCount = ticketNumbersSet.intersect(winningNumbersSet).size

        val starHitCount = if (game.hasSecondaryMatrix) {
            val winningStarSet = winningSecondary.toSet()
            val ticketStarSet = ticket.secondaryNumbers.toSet()
            ticketStarSet.intersect(winningStarSet).size
        } else {
            0
        }

        val prizeResult = PrizeCalculator.calculatePrize(game, hitCount, starHitCount)
        val updatedTicket = ticket.copy(
            isValidated = true,
            validatedHits = hitCount,
            validatedStarHits = if (game.hasSecondaryMatrix) starHitCount else null,
            prizeLabel = prizeResult.prizeLabel
        )

        viewModelScope.launch {
            repository.updateTicket(updatedTicket)
            closeValidationDialog()
            if (prizeResult.isWinner) {
                _userFeedback.value = "🎉 ¡Boleto validado con premio! (${prizeResult.prizeLabel})"
            } else {
                _userFeedback.value = "Boleto validado — sin aciertos en categorías premiadas"
            }
        }
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }
}

class LotteryViewModelFactory(
    private val repository: TicketRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LotteryViewModel::class.java)) {
            return LotteryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
