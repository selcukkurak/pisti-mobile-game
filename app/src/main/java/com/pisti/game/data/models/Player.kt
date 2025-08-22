package com.pisti.game.data.models

/**
 * Represents a player in the Pişti game
 */
data class Player(
    val id: String,
    val name: String,
    val type: PlayerType,
    val aiDifficulty: AIDifficulty = AIDifficulty.BEGINNER,
    var score: Int = 0,
    var capturedCards: MutableList<Card> = mutableListOf(),
    var hand: MutableList<Card> = mutableListOf(),
    var pistiCount: Int = 0
) {
    /**
     * Calculate total points from captured cards
     */
    fun calculateTotalPoints(): Int {
        var totalPoints = capturedCards.sumOf { it.getPoints() }
        
        // Add 1 point for each captured card
        totalPoints += capturedCards.size
        
        // Add Pişti bonus (10 points each)
        totalPoints += pistiCount * 10
        
        return totalPoints
    }
    
    /**
     * Add cards to captured pile
     */
    fun captureCards(cards: List<Card>, isPisti: Boolean = false) {
        capturedCards.addAll(cards)
        if (isPisti) {
            pistiCount++
        }
    }
    
    /**
     * Play a card from hand
     */
    fun playCard(card: Card): Card? {
        return if (hand.remove(card)) {
            card
        } else {
            null
        }
    }
    
    /**
     * Add cards to hand
     */
    fun addCardsToHand(cards: List<Card>) {
        hand.addAll(cards)
    }
    
    /**
     * Check if player has no cards in hand
     */
    fun isHandEmpty(): Boolean = hand.isEmpty()
}

enum class PlayerType {
    HUMAN,
    AI
}

enum class AIDifficulty {
    BEGINNER,
    INTERMEDIATE,
    EXPERT
}