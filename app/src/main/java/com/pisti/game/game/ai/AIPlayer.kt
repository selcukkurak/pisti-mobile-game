package com.pisti.game.game.ai

import com.pisti.game.data.models.*

/**
 * Base class for AI players in Pişti
 */
abstract class AIPlayer(
    val player: Player,
    val difficulty: AIDifficulty
) {
    /**
     * Choose which card to play based on current game state
     */
    abstract fun chooseCard(gameState: GameState): Card?
    
    /**
     * Common helper function to check if a card can make a capture
     */
    protected fun canMakeCapture(card: Card, tableCards: List<Card>): Boolean {
        val topCard = tableCards.lastOrNull()
        return topCard?.let { card.canCapture(it) } ?: false
    }
    
    /**
     * Check if playing this card would result in a Pişti
     */
    protected fun wouldBePisti(card: Card, gameState: GameState): Boolean {
        return card.isJack() && 
               gameState.isTableSingleCard() && 
               canMakeCapture(card, gameState.tableCards)
    }
    
    /**
     * Get all cards that can make captures
     */
    protected fun getCapturingCards(hand: List<Card>, tableCards: List<Card>): List<Card> {
        return hand.filter { canMakeCapture(it, tableCards) }
    }
    
    /**
     * Get high-value cards (Aces, special cards)
     */
    protected fun getHighValueCards(cards: List<Card>): List<Card> {
        return cards.filter { it.getPoints() > 0 }
    }
}

/**
 * Beginner AI - Basic card matching strategy
 */
class BeginnerAI(player: Player) : AIPlayer(player, AIDifficulty.BEGINNER) {
    
    override fun chooseCard(gameState: GameState): Card? {
        val hand = player.hand
        if (hand.isEmpty()) return null
        
        // Always try to capture if possible
        val capturingCards = getCapturingCards(hand, gameState.tableCards)
        if (capturingCards.isNotEmpty()) {
            return capturingCards.random()
        }
        
        // Play lowest value card
        return hand.minByOrNull { it.rank.value }
    }
}

/**
 * Intermediate AI - Strategic card holding
 */
class IntermediateAI(player: Player) : AIPlayer(player, AIDifficulty.INTERMEDIATE) {
    
    override fun chooseCard(gameState: GameState): Card? {
        val hand = player.hand
        if (hand.isEmpty()) return null
        
        // Priority 1: Try for Pişti if possible
        val pistiCards = hand.filter { wouldBePisti(it, gameState) }
        if (pistiCards.isNotEmpty()) {
            return pistiCards.first()
        }
        
        // Priority 2: Capture high-value cards on table
        val capturingCards = getCapturingCards(hand, gameState.tableCards)
        if (capturingCards.isNotEmpty()) {
            val tableHighValueCards = getHighValueCards(gameState.tableCards)
            if (tableHighValueCards.isNotEmpty()) {
                return capturingCards.first()
            }
            
            // Only capture if there are multiple cards on table
            if (gameState.tableCards.size > 1) {
                return capturingCards.first()
            }
        }
        
        // Priority 3: Play safe - don't give easy captures
        val safeCards = hand.filter { card ->
            gameState.players.none { otherPlayer ->
                otherPlayer != player && 
                otherPlayer.hand.any { it.canCapture(card) }
            }
        }
        
        if (safeCards.isNotEmpty()) {
            return safeCards.minByOrNull { it.rank.value }
        }
        
        // Fallback: play lowest card
        return hand.minByOrNull { it.rank.value }
    }
}

/**
 * Expert AI - Advanced prediction and blocking
 */
class ExpertAI(player: Player) : AIPlayer(player, AIDifficulty.EXPERT) {
    
    private val cardMemory = mutableSetOf<Card>() // Remember played cards
    
    override fun chooseCard(gameState: GameState): Card? {
        val hand = player.hand
        if (hand.isEmpty()) return null
        
        // Update card memory
        updateCardMemory(gameState)
        
        // Priority 1: Pişti opportunity
        val pistiCards = hand.filter { wouldBePisti(it, gameState) }
        if (pistiCards.isNotEmpty()) {
            return pistiCards.first()
        }
        
        // Priority 2: Block opponent's likely Pişti
        val blockingCard = findBlockingCard(gameState)
        if (blockingCard != null) {
            return blockingCard
        }
        
        // Priority 3: Strategic capturing
        val capturingCards = getCapturingCards(hand, gameState.tableCards)
        if (capturingCards.isNotEmpty()) {
            // Calculate value of capture
            val captureValue = calculateCaptureValue(gameState.tableCards)
            
            if (captureValue > 3 || gameState.tableCards.size > 2) {
                return chooseBestCapturingCard(capturingCards, gameState)
            }
        }
        
        // Priority 4: Strategic card placement
        return chooseStrategicCard(gameState)
    }
    
    private fun updateCardMemory(gameState: GameState) {
        gameState.moveHistory.forEach { move ->
            cardMemory.add(move.cardPlayed)
            cardMemory.addAll(move.capturedCards)
        }
    }
    
    private fun findBlockingCard(gameState: GameState): Card? {
        val topCard = gameState.getTopTableCard() ?: return null
        
        // If table has one card and opponents might have Jacks
        if (gameState.isTableSingleCard()) {
            // Play a card that's different from top card to prevent Pişti
            return player.hand.find { !it.canCapture(topCard) }
        }
        
        return null
    }
    
    private fun calculateCaptureValue(tableCards: List<Card>): Int {
        return tableCards.sumOf { it.getPoints() } + tableCards.size
    }
    
    private fun chooseBestCapturingCard(capturingCards: List<Card>, gameState: GameState): Card {
        // Prefer using lower-value cards for capturing
        return capturingCards.minByOrNull { it.getPoints() } ?: capturingCards.first()
    }
    
    private fun chooseStrategicCard(gameState: GameState): Card {
        val hand = player.hand
        
        // Try to create future capturing opportunities
        val strategicCard = hand.find { card ->
            val remainingCards = 52 - cardMemory.size
            val probabilityOfMatch = remainingCards / 13.0 // Approximate
            probabilityOfMatch > 0.3 && card.getPoints() == 0
        }
        
        return strategicCard ?: hand.minByOrNull { it.rank.value } ?: hand.first()
    }
}

/**
 * Factory to create AI players based on difficulty
 */
object AIPlayerFactory {
    fun createAIPlayer(player: Player): AIPlayer {
        return when (player.aiDifficulty) {
            AIDifficulty.BEGINNER -> BeginnerAI(player)
            AIDifficulty.INTERMEDIATE -> IntermediateAI(player)
            AIDifficulty.EXPERT -> ExpertAI(player)
        }
    }
}