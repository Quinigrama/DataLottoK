package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.FavoritesPreferences
import com.example.data.model.GameConfig
import com.example.data.model.SavedTicket
import com.example.data.repository.TicketRepository
import com.example.logic.LotteryGenerator
import com.example.logic.MultipleTicketCalculator
import com.example.logic.PrizeCalculator
import com.example.logic.ReducedSystemCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MarkingMode {
    COLD, HOT, ABSENT, FAVORITE
}

class LotteryViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    private val _currentGame = MutableStateFlow(GameConfig.Bonoloto)
    val currentGame: StateFlow<GameConfig> = _currentGame.asStateFlow()

    // Modo de marcado manual: Frío, Caliente, Ausente, Favorito
    private val _markingMode = MutableStateFlow<MarkingMode?>(null)
    val markingMode: StateFlow<MarkingMode?> = _markingMode.asStateFlow()

    // Ajustes manuales por encima de la clasificación automática
    private val _manualHot = MutableStateFlow<Set<Int>>(emptySet())
    val manualHot: StateFlow<Set<Int>> = _manualHot.asStateFlow()

    private val _manualCold = MutableStateFlow<Set<Int>>(emptySet())
    val manualCold: StateFlow<Set<Int>> = _manualCold.asStateFlow()

    private val _manualAbsent = MutableStateFlow<Set<Int>>(emptySet())
    val manualAbsent: StateFlow<Set<Int>> = _manualAbsent.asStateFlow()

    // Favoritos persistentes por juego
    private val _favoriteNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteNumbers: StateFlow<Set<Int>> = _favoriteNumbers.asStateFlow()

    private val _favoriteStars = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteStars: StateFlow<Set<Int>> = _favoriteStars.asStateFlow()

    // Estrategia de juego: "simple", "multiple" o "reducida"
    private val _strategy = MutableStateFlow("simple")
    val strategy: StateFlow<String> = _strategy.asStateFlow()

    // Sistema reducido activo
    private val _reducedSystemId = MutableStateFlow<String?>(null)
    val reducedSystemId: StateFlow<String?> = _reducedSystemId.asStateFlow()

    // Opciones para apuesta múltiple
    private val initialMultipleOptions = MultipleTicketCalculator.getOptions(GameConfig.Bonoloto)
    private val _multipleNumberCount = MutableStateFlow(initialMultipleOptions.defaultNumber)
    val multipleNumberCount: StateFlow<Int> = _multipleNumberCount.asStateFlow()

    private val _multipleSecondaryCount = MutableStateFlow(initialMultipleOptions.defaultSecondary)
    val multipleSecondaryCount: StateFlow<Int> = _multipleSecondaryCount.asStateFlow()

    private val _multipleBets = MutableStateFlow(0)
    val multipleBets: StateFlow<Int> = _multipleBets.asStateFlow()

    private val _multipleCost = MutableStateFlow(0.0)
    val multipleCost: StateFlow<Double> = _multipleCost.asStateFlow()

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

    // Estado para datos simulados de estadísticas (en memoria)
    private val _simulatedDraws = MutableStateFlow<List<com.example.logic.SimulatedDraw>>(emptyList())
    val simulatedDraws: StateFlow<List<com.example.logic.SimulatedDraw>> = _simulatedDraws.asStateFlow()

    fun setStrategy(value: String) {
        _strategy.value = value
        if (value == "reducida") {
            val systems = ReducedSystemCalculator.getSystems(_currentGame.value.id)
            val currentSys = systems.find { it.id == _reducedSystemId.value }
            if (currentSys == null && systems.isNotEmpty()) {
                _reducedSystemId.value = systems.first().id
            }
        }
    }

    fun setReducedSystemId(id: String) {
        _reducedSystemId.value = id
        val system = ReducedSystemCalculator.findSystem(_currentGame.value.id, id)
        if (system != null && _selectedNumbers.value.size > system.baseNumbersCount) {
            _selectedNumbers.value = _selectedNumbers.value.take(system.baseNumbersCount).toSet()
        }
    }

    fun setMultipleNumberCount(n: Int) {
        _multipleNumberCount.value = n
    }

    fun setMultipleSecondaryCount(n: Int) {
        _multipleSecondaryCount.value = n
    }

    fun simulateDraws(count: Int = 200) {
        val game = _currentGame.value
        val draws = com.example.logic.StatisticsEngine.generateSimulatedDraws(game, count)
        _simulatedDraws.value = draws
        _userFeedback.value = "🎲 Simulados $count sorteos para ${game.name}"
    }

    fun selectGame(game: GameConfig) {
        if (_currentGame.value.id != game.id) {
            _currentGame.value = game
            _selectedNumbers.value = emptySet()
            _selectedSecondaryNumbers.value = emptySet()
            _simulatedDraws.value = emptyList()
            clearManualOverrides()
            _strategy.value = "simple"
            val options = MultipleTicketCalculator.getOptions(game)
            _multipleNumberCount.value = options.defaultNumber
            _multipleSecondaryCount.value = options.defaultSecondary
            _multipleBets.value = 0
            _multipleCost.value = 0.0
            val systems = ReducedSystemCalculator.getSystems(game.id)
            _reducedSystemId.value = systems.firstOrNull()?.id
            _userFeedback.value = "Cambiado a ${game.name}"
        }
    }

    fun setMarkingMode(mode: MarkingMode?) {
        _markingMode.value = if (_markingMode.value == mode) null else mode
    }

    fun handleMarkingClick(
        context: Context,
        gameId: String,
        number: Int,
        isStar: Boolean = false
    ): Boolean {
        val mode = _markingMode.value ?: return true
        return when (mode) {
            MarkingMode.FAVORITE -> {
                val success = FavoritesPreferences.toggleFavorite(context, gameId, number, isStar)
                if (success) {
                    loadFavorites(context, gameId)
                }
                success
            }
            MarkingMode.HOT -> {
                val current = _manualHot.value
                if (current.contains(number)) {
                    _manualHot.value = current - number
                } else {
                    _manualHot.value = current + number
                    _manualCold.value = _manualCold.value - number
                    _manualAbsent.value = _manualAbsent.value - number
                }
                true
            }
            MarkingMode.COLD -> {
                val current = _manualCold.value
                if (current.contains(number)) {
                    _manualCold.value = current - number
                } else {
                    _manualCold.value = current + number
                    _manualHot.value = _manualHot.value - number
                    _manualAbsent.value = _manualAbsent.value - number
                }
                true
            }
            MarkingMode.ABSENT -> {
                val current = _manualAbsent.value
                if (current.contains(number)) {
                    _manualAbsent.value = current - number
                } else {
                    _manualAbsent.value = current + number
                    _manualHot.value = _manualHot.value - number
                    _manualCold.value = _manualCold.value - number
                }
                true
            }
        }
    }

    fun loadFavorites(context: Context, gameId: String) {
        _favoriteNumbers.value = FavoritesPreferences.getFavorites(context, gameId, isStar = false)
        _favoriteStars.value = FavoritesPreferences.getFavorites(context, gameId, isStar = true)
    }

    fun clearManualOverrides() {
        _manualHot.value = emptySet()
        _manualCold.value = emptySet()
        _manualAbsent.value = emptySet()
    }

    fun setTicketFilter(gameId: String?) {
        _ticketFilterGameId.value = gameId
    }

    fun toggleNumber(number: Int) {
        val currentSet = _selectedNumbers.value
        val game = _currentGame.value
        val maxLimit = if (_strategy.value == "reducida") {
            ReducedSystemCalculator.findSystem(game.id, _reducedSystemId.value)?.baseNumbersCount ?: game.pickCount
        } else {
            game.pickCount
        }

        if (currentSet.contains(number)) {
            _selectedNumbers.value = currentSet - number
        } else {
            if (currentSet.size < maxLimit) {
                _selectedNumbers.value = currentSet + number
            } else {
                _userFeedback.value = "Ya has seleccionado $maxLimit números"
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

    fun generateMultipleCombination() {
        val game = _currentGame.value
        try {
            val result = MultipleTicketCalculator.generate(
                game = game,
                numberCount = _multipleNumberCount.value,
                secondaryCount = _multipleSecondaryCount.value
            )
            _selectedNumbers.value = result.numbers.toSet()
            _selectedSecondaryNumbers.value = result.secondaryNumbers.toSet()
            _multipleBets.value = result.totalBets
            _multipleCost.value = result.totalCost
            val costFormatted = String.format(java.util.Locale.getDefault(), "%.2f", result.totalCost)
            _userFeedback.value = "🎯 Apuesta múltiple generada (${result.totalBets} apuestas · $costFormatted €)"
        } catch (e: Exception) {
            _userFeedback.value = e.message ?: "Error al generar la combinación múltiple"
        }
    }

    fun generateReducedCombinations() {
        val game = _currentGame.value
        val system = ReducedSystemCalculator.findSystem(game.id, _reducedSystemId.value)
        if (system == null) {
            _userFeedback.value = "Selecciona un sistema reducido válido"
            return
        }

        if (_selectedNumbers.value.size != system.baseNumbersCount) {
            _userFeedback.value = "Debes seleccionar exactamente ${system.baseNumbersCount} números base para este sistema"
            return
        }

        if (game.hasSecondaryMatrix && _selectedSecondaryNumbers.value.size != game.secondaryPickCount) {
            val name = game.secondaryName ?: "estrellas"
            _userFeedback.value = "Debes seleccionar exactamente ${game.secondaryPickCount} $name fijas"
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            try {
                ReducedSystemCalculator.generate(
                    game = game,
                    system = system,
                    baseNumbers = _selectedNumbers.value.toList()
                )
                _userFeedback.value = "🧩 Sistema reducido generado: ${system.combinationsCount} apuestas listas para guardar"
            } catch (e: Exception) {
                _userFeedback.value = e.message ?: "Error al generar el sistema reducido"
            }
        }
    }

    fun clearSelection() {
        _selectedNumbers.value = emptySet()
        _selectedSecondaryNumbers.value = emptySet()
    }

    fun saveCurrentTicket(onSuccess: () -> Unit = {}) {
        val game = _currentGame.value
        val numbers = _selectedNumbers.value.toList().sorted()
        val secondaryNumbers = _selectedSecondaryNumbers.value.toList().sorted()
        val currentStrategy = _strategy.value

        if (currentStrategy == "multiple") {
            val requiredCount = _multipleNumberCount.value
            if (numbers.size != requiredCount) {
                _userFeedback.value = "Debes generar una combinación múltiple de $requiredCount números para guardar el boleto"
                return
            }
            if (game.hasSecondaryMatrix) {
                val requiredSec = _multipleSecondaryCount.value
                if (secondaryNumbers.size != requiredSec) {
                    val name = game.secondaryName ?: "estrellas"
                    _userFeedback.value = "Debes generar exactamente $requiredSec $name para guardar el boleto"
                    return
                }
            }
        } else if (currentStrategy == "reducida") {
            val system = ReducedSystemCalculator.findSystem(game.id, _reducedSystemId.value)
            if (system == null) {
                _userFeedback.value = "Selecciona un sistema reducido válido"
                return
            }
            if (numbers.size != system.baseNumbersCount) {
                _userFeedback.value = "Debes seleccionar exactamente ${system.baseNumbersCount} números base para guardar el boleto"
                return
            }
            if (game.hasSecondaryMatrix && secondaryNumbers.size != game.secondaryPickCount) {
                val name = game.secondaryName ?: "estrellas"
                _userFeedback.value = "Debes seleccionar exactamente ${game.secondaryPickCount} $name para guardar el boleto"
                return
            }
        } else {
            if (numbers.size != game.pickCount) {
                _userFeedback.value = "Debes seleccionar exactamente ${game.pickCount} números para guardar el boleto"
                return
            }

            if (game.hasSecondaryMatrix && secondaryNumbers.size != game.secondaryPickCount) {
                val name = game.secondaryName ?: "estrellas"
                _userFeedback.value = "Debes seleccionar exactamente ${game.secondaryPickCount} $name para guardar el boleto"
                return
            }
        }

        viewModelScope.launch {
            val ticket = SavedTicket(
                gameId = game.id,
                gameName = game.name,
                numbers = numbers,
                secondaryNumbers = if (game.hasSecondaryMatrix) secondaryNumbers else emptyList(),
                strategy = currentStrategy,
                systemId = if (currentStrategy == "reducida") _reducedSystemId.value else null,
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
