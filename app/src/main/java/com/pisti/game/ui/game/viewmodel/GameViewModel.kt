package com.pisti.game.ui.game.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.pisti.game.data.models.*
import com.pisti.game.game.engine.GameResult
import com.pisti.game.game.engine.PistiGameEngine
import com.pisti.game.game.ai.AIPlayerFactory
import java.util.*

class GameViewModel : ViewModel() {
    
    private val gameEngine = PistiGameEngine()
    
    private val _gameState = MutableLiveData<GameState?>()
    val gameState: LiveData<GameState?> = _gameState
    
    private val _isProcessing = MutableLiveData(false)
    val isProcessing: LiveData<Boolean> = _isProcessing
    
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message
    
    fun initializeGame() {
        viewModelScope.launch {
            _isProcessing.value = true
            
            // Create players
            val players = listOf(
                Player(
                    id = "human",
                    name = "Sen",
                    type = PlayerType.HUMAN
                ),
                Player(
                    id = "ai1",
                    name = "Ahmet",
                    type = PlayerType.AI,
                    aiDifficulty = AIDifficulty.BEGINNER
                ),
                Player(
                    id = "ai2",
                    name = "Fatma",
                    type = PlayerType.AI,
                    aiDifficulty = AIDifficulty.INTERMEDIATE
                ),
                Player(
                    id = "ai3",
                    name = "Mehmet",
                    type = PlayerType.AI,
                    aiDifficulty = AIDifficulty.EXPERT
                )
            )
            
            // Initialize game
            val gameState = gameEngine.initializeGame(
                gameId = UUID.randomUUID().toString(),
                players = players,
                gameMode = GameMode.OFFLINE_SINGLE_PLAYER
            )
            
            _gameState.value = gameState
            _isProcessing.value = false
            
            // Start AI turn if first player is AI
            if (gameState.getCurrentPlayer().type == PlayerType.AI) {
                processAITurn()
            }
        }
    }
    
    fun playCard(card: Card) {
        val currentState = _gameState.value ?: return
        val currentPlayer = currentState.getCurrentPlayer()
        
        if (currentPlayer.type != PlayerType.HUMAN || _isProcessing.value == true) {
            return
        }
        
        viewModelScope.launch {
            _isProcessing.value = true
            
            // Play the card
            val result = gameEngine.playCard(currentState, card)
            
            when (result) {
                is GameResult.InvalidMove -> {
                    _message.value = result.reason
                    _isProcessing.value = false
                    return@launch
                }
                is GameResult.Capture -> {
                    _message.value = if (result.capture.isPisti) {
                        "Pişti! +10 puan bonus!"
                    } else {
                        "${result.capture.capturedCards.size} kart yakaladınız!"
                    }
                }
                GameResult.CardPlayed -> {
                    _message.value = null
                }
            }
            
            // Update game state and move to next player
            var updatedState = gameEngine.nextPlayer(currentState)
            
            // Check if round is complete
            updatedState = gameEngine.checkRoundEnd(updatedState)
            
            // Check if game is complete
            if (gameEngine.isGameComplete(updatedState)) {
                updatedState = gameEngine.calculateFinalScores(updatedState)
                _message.value = "Oyun bitti! Kazanan: ${updatedState.winner?.name}"
            }
            
            _gameState.value = updatedState
            _isProcessing.value = false
            
            // Clear message after delay
            delay(3000)
            _message.value = null
            
            // Continue with AI turns
            if (!updatedState.isGameOver) {
                processAITurns()
            }
        }
    }
    
    private suspend fun processAITurns() {
        var currentState = _gameState.value ?: return
        
        while (!currentState.isGameOver && 
               currentState.getCurrentPlayer().type == PlayerType.AI &&
               _isProcessing.value != true) {
            
            delay(1000) // Add delay for better UX
            processAITurn()
            currentState = _gameState.value ?: return
        }
    }
    
    private suspend fun processAITurn() {
        val currentState = _gameState.value ?: return
        val currentPlayer = currentState.getCurrentPlayer()
        
        if (currentPlayer.type != PlayerType.AI) return
        
        _isProcessing.value = true
        
        // Create AI player and get its move
        val aiPlayer = AIPlayerFactory.createAIPlayer(currentPlayer)
        val cardToPlay = aiPlayer.chooseCard(currentState)
        
        if (cardToPlay == null) {
            _isProcessing.value = false
            return
        }
        
        // Add thinking delay
        delay(1500)
        
        // Play the AI's card
        val result = gameEngine.playCard(currentState, cardToPlay)
        
        when (result) {
            is GameResult.Capture -> {
                _message.value = if (result.capture.isPisti) {
                    "${currentPlayer.name} Pişti yaptı!"
                } else {
                    "${currentPlayer.name} ${result.capture.capturedCards.size} kart yakaladı"
                }
            }
            else -> {
                _message.value = "${currentPlayer.name} ${cardToPlay.rank.symbol}${cardToPlay.suit.symbol} oynadı"
            }
        }
        
        // Update game state
        var updatedState = gameEngine.nextPlayer(currentState)
        updatedState = gameEngine.checkRoundEnd(updatedState)
        
        if (gameEngine.isGameComplete(updatedState)) {
            updatedState = gameEngine.calculateFinalScores(updatedState)
            _message.value = "Oyun bitti! Kazanan: ${updatedState.winner?.name}"
        }
        
        _gameState.value = updatedState
        _isProcessing.value = false
        
        // Clear message after delay
        delay(2000)
        if (!updatedState.isGameOver) {
            _message.value = null
        }
    }
    
    fun clearMessage() {
        _message.value = null
    }
}