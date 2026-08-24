package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.GameConfig
import com.example.data.model.SavedTicket
import com.example.data.repository.TicketRepository
import com.example.logic.LotteryGenerator
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

    fun selectGame(game: GameConfig) {
        if (_currentGame.value.id != game.id) {
            _currentGame.value = game
            _selectedNumbers.value = emptySet()
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

    fun generateCombination() {
        val generated = LotteryGenerator.generateSimple(_currentGame.value)
        _selectedNumbers.value = generated.toSet()
        _userFeedback.value = "🍀 Combinación generada para ${_currentGame.value.name}"
    }

    fun clearSelection() {
        _selectedNumbers.value = emptySet()
    }

    fun saveCurrentTicket(onSuccess: () -> Unit = {}) {
        val game = _currentGame.value
        val numbers = _selectedNumbers.value.toList().sorted()

        if (numbers.size != game.pickCount) {
            _userFeedback.value = "Debes seleccionar exactamente ${game.pickCount} números para guardar el boleto"
            return
        }

        viewModelScope.launch {
            val ticket = SavedTicket(
                gameId = game.id,
                gameName = game.name,
                numbers = numbers,
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
