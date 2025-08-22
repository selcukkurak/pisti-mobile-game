package com.pisti.game.data.models

/**
 * Represents the current state of a Pişti game
 */
data class GameState(
    val gameId: String,
    val players: List<Player>,
    val currentPlayerIndex: Int,
    val tableCards: MutableList<Card>,
    val deck: MutableList<Card>,
    val round: Int,
    val gamePhase: GamePhase,
    val gameMode: GameMode,
    val isGameOver: Boolean = false,
    val winner: Player? = null,
    val lastCapture: Capture? = null,
    val moveHistory: MutableList<GameMove> = mutableListOf()
) {
    /**
     * Get the current player
     */
    fun getCurrentPlayer(): Player = players[currentPlayerIndex]
    
    /**
     * Get the top card on the table (last played card)
     */
    fun getTopTableCard(): Card? = tableCards.lastOrNull()
    
    /**
     * Check if the table has exactly one card (for Pişti detection)
     */
    fun isTableSingleCard(): Boolean = tableCards.size == 1
    
    /**
     * Get the human player
     */
    fun getHumanPlayer(): Player? = players.find { it.type == PlayerType.HUMAN }
    
    /**
     * Check if all players have empty hands
     */
    fun isRoundComplete(): Boolean = players.all { it.isHandEmpty() }
    
    /**
     * Get player who captured the most cards
     */
    fun getPlayerWithMostCards(): Player? {
        return players.maxByOrNull { it.capturedCards.size }
    }
}

data class Capture(
    val playerId: String,
    val capturedCards: List<Card>,
    val isPisti: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameMove(
    val playerId: String,
    val cardPlayed: Card,
    val capturedCards: List<Card>,
    val isPisti: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

enum class GamePhase {
    SETUP,
    DEALING,
    PLAYING,
    ROUND_END,
    GAME_OVER
}

enum class GameMode {
    OFFLINE_SINGLE_PLAYER,
    ONLINE_MULTIPLAYER
}