package com.pisti.game.game.engine

import com.pisti.game.data.models.*

/**
 * Core game engine for Pişti - handles all game rules and logic
 */
class PistiGameEngine {
    
    companion object {
        const val CARDS_PER_PLAYER = 4
        const val INITIAL_TABLE_CARDS = 4
        const val MOST_CARDS_BONUS = 3
        const val PISTI_BONUS = 10
        const val TARGET_SCORE = 151 // Traditional Pişti winning score
    }
    
    /**
     * Create a new deck of 52 cards
     */
    fun createDeck(): MutableList<Card> {
        val deck = mutableListOf<Card>()
        
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deck.add(Card(suit, rank))
            }
        }
        
        return deck
    }
    
    /**
     * Shuffle the deck
     */
    fun shuffleDeck(deck: MutableList<Card>) {
        deck.shuffle()
    }
    
    /**
     * Initialize a new game
     */
    fun initializeGame(gameId: String, players: List<Player>, gameMode: GameMode): GameState {
        val deck = createDeck()
        shuffleDeck(deck)
        
        // Deal initial table cards
        val tableCards = mutableListOf<Card>()
        repeat(INITIAL_TABLE_CARDS) {
            if (deck.isNotEmpty()) {
                tableCards.add(deck.removeAt(0))
            }
        }
        
        // Deal cards to players
        players.forEach { player ->
            player.hand.clear()
            player.capturedCards.clear()
            player.pistiCount = 0
            player.score = 0
            
            repeat(CARDS_PER_PLAYER) {
                if (deck.isNotEmpty()) {
                    player.hand.add(deck.removeAt(0))
                }
            }
        }
        
        return GameState(
            gameId = gameId,
            players = players,
            currentPlayerIndex = 0,
            tableCards = tableCards,
            deck = deck,
            round = 1,
            gamePhase = GamePhase.PLAYING,
            gameMode = gameMode
        )
    }
    
    /**
     * Play a card and determine the result
     */
    fun playCard(gameState: GameState, card: Card): GameResult {
        val currentPlayer = gameState.getCurrentPlayer()
        val topTableCard = gameState.getTopTableCard()
        
        // Remove card from player's hand
        if (!currentPlayer.hand.remove(card)) {
            return GameResult.InvalidMove("Card not in player's hand")
        }
        
        val result = if (topTableCard != null && card.canCapture(topTableCard)) {
            // Player captures all table cards
            val capturedCards = gameState.tableCards.toList()
            val isPisti = gameState.isTableSingleCard() && card.isJack()
            
            currentPlayer.captureCards(capturedCards, isPisti)
            gameState.tableCards.clear()
            
            val capture = Capture(
                playerId = currentPlayer.id,
                capturedCards = capturedCards,
                isPisti = isPisti
            )
            
            GameResult.Capture(capture)
        } else {
            // Card goes to table
            gameState.tableCards.add(card)
            GameResult.CardPlayed
        }
        
        // Record the move
        val move = GameMove(
            playerId = currentPlayer.id,
            cardPlayed = card,
            capturedCards = if (result is GameResult.Capture) result.capture.capturedCards else emptyList(),
            isPisti = if (result is GameResult.Capture) result.capture.isPisti else false
        )
        gameState.moveHistory.add(move)
        
        return result
    }
    
    /**
     * Check if round is complete and deal new cards if needed
     */
    fun checkRoundEnd(gameState: GameState): GameState {
        if (gameState.isRoundComplete() && gameState.deck.isNotEmpty()) {
            // Deal new cards to all players
            gameState.players.forEach { player ->
                repeat(CARDS_PER_PLAYER) {
                    if (gameState.deck.isNotEmpty()) {
                        player.hand.add(gameState.deck.removeAt(0))
                    }
                }
            }
            
            return gameState.copy(
                round = gameState.round + 1,
                gamePhase = GamePhase.PLAYING
            )
        }
        
        return gameState
    }
    
    /**
     * Calculate final scores and determine winner
     */
    fun calculateFinalScores(gameState: GameState): GameState {
        // Award most cards bonus
        val playerWithMostCards = gameState.getPlayerWithMostCards()
        playerWithMostCards?.let { player ->
            player.capturedCards.add(Card(Suit.CLUBS, Rank.ACE)) // Virtual bonus card
            player.capturedCards.add(Card(Suit.CLUBS, Rank.ACE)) // Virtual bonus card
            player.capturedCards.add(Card(Suit.CLUBS, Rank.ACE)) // Virtual bonus card
        }
        
        // Calculate final scores
        gameState.players.forEach { player ->
            player.score = player.calculateTotalPoints()
        }
        
        val winner = gameState.players.maxByOrNull { it.score }
        
        return gameState.copy(
            isGameOver = true,
            winner = winner,
            gamePhase = GamePhase.GAME_OVER
        )
    }
    
    /**
     * Move to next player
     */
    fun nextPlayer(gameState: GameState): GameState {
        val nextIndex = (gameState.currentPlayerIndex + 1) % gameState.players.size
        return gameState.copy(currentPlayerIndex = nextIndex)
    }
    
    /**
     * Check if game should end (deck empty and all hands empty)
     */
    fun isGameComplete(gameState: GameState): Boolean {
        return gameState.deck.isEmpty() && gameState.players.all { it.isHandEmpty() }
    }
}

sealed class GameResult {
    object CardPlayed : GameResult()
    data class Capture(val capture: com.pisti.game.data.models.Capture) : GameResult()
    data class InvalidMove(val reason: String) : GameResult()
}