package com.pisti.game.data.models

/**
 * Represents a playing card with suit and rank
 */
data class Card(
    val suit: Suit,
    val rank: Rank
) {
    /**
     * Get the points value of this card according to Pişti rules
     */
    fun getPoints(): Int = when {
        rank == Rank.ACE -> 1
        rank == Rank.JACK && suit == Suit.CLUBS -> 2
        rank == Rank.JACK && suit == Suit.SPADES -> 2
        rank == Rank.TEN && suit == Suit.DIAMONDS -> 3
        rank == Rank.TWO && suit == Suit.CLUBS -> 2
        else -> 0
    }
    
    /**
     * Check if this card can capture the given card
     * In Pişti: matching ranks can capture, and Jacks can capture any card
     */
    fun canCapture(other: Card): Boolean = this.rank == other.rank || this.isJack()
    
    /**
     * Check if this is a Jack (for Pişti bonus)
     */
    fun isJack(): Boolean = rank == Rank.JACK
}

enum class Suit(val symbol: String, val color: CardColor) {
    CLUBS("♣", CardColor.BLACK),
    DIAMONDS("♦", CardColor.RED),
    HEARTS("♥", CardColor.RED),
    SPADES("♠", CardColor.BLACK)
}

enum class Rank(val value: Int, val symbol: String) {
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K"),
    ACE(14, "A")
}

enum class CardColor {
    RED, BLACK
}