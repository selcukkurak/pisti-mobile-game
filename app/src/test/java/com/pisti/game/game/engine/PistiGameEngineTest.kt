package com.pisti.game.game.engine

import org.junit.Test
import org.junit.Assert.*
import com.pisti.game.data.models.*

class PistiGameEngineTest {

    private val gameEngine = PistiGameEngine()

    @Test
    fun createDeck_shouldHave52Cards() {
        val deck = gameEngine.createDeck()
        assertEquals(52, deck.size)
        
        // Check we have all suits and ranks
        val suits = deck.map { it.suit }.distinct()
        val ranks = deck.map { it.rank }.distinct()
        
        assertEquals(4, suits.size)
        assertEquals(13, ranks.size)
    }

    @Test
    fun initializeGame_shouldSetupCorrectly() {
        val players = listOf(
            Player("1", "Player 1", PlayerType.HUMAN),
            Player("2", "Player 2", PlayerType.AI),
            Player("3", "Player 3", PlayerType.AI),
            Player("4", "Player 4", PlayerType.AI)
        )

        val gameState = gameEngine.initializeGame("test", players, GameMode.OFFLINE_SINGLE_PLAYER)

        assertEquals(4, gameState.players.size)
        assertEquals(4, gameState.tableCards.size)
        
        // Each player should have 4 cards
        gameState.players.forEach { player ->
            assertEquals(4, player.hand.size)
        }
        
        // Deck should have remaining cards (52 - 4 table - 4*4 players = 32)
        assertEquals(32, gameState.deck.size)
    }

    @Test
    fun playCard_captureTest() {
        val players = listOf(Player("1", "Test", PlayerType.HUMAN))
        val gameState = gameEngine.initializeGame("test", players, GameMode.OFFLINE_SINGLE_PLAYER)
        
        // Set up a capture scenario
        val tableCard = Card(Suit.HEARTS, Rank.KING)
        val playerCard = Card(Suit.CLUBS, Rank.KING)
        
        gameState.tableCards.clear()
        gameState.tableCards.add(tableCard)
        gameState.players[0].hand.clear()
        gameState.players[0].hand.add(playerCard)
        
        val result = gameEngine.playCard(gameState, playerCard)
        
        assertTrue(result is GameResult.Capture)
        assertEquals(1, gameState.players[0].capturedCards.size)
        assertEquals(0, gameState.tableCards.size)
    }

    @Test
    fun playCard_pistiTest() {
        val players = listOf(Player("1", "Test", PlayerType.HUMAN))
        val gameState = gameEngine.initializeGame("test", players, GameMode.OFFLINE_SINGLE_PLAYER)
        
        // Set up a Pişti scenario (Jack capturing single card)
        val tableCard = Card(Suit.HEARTS, Rank.QUEEN)
        val jack = Card(Suit.CLUBS, Rank.JACK)
        
        gameState.tableCards.clear()
        gameState.tableCards.add(tableCard)
        gameState.players[0].hand.clear()
        gameState.players[0].hand.add(jack)
        
        val result = gameEngine.playCard(gameState, jack)
        
        assertTrue(result is GameResult.Capture)
        val capture = result as GameResult.Capture
        assertTrue(capture.capture.isPisti)
        assertEquals(1, gameState.players[0].pistiCount)
    }

    @Test
    fun cardPoints_shouldCalculateCorrectly() {
        // Test special cards
        assertEquals(1, Card(Suit.HEARTS, Rank.ACE).getPoints())
        assertEquals(2, Card(Suit.CLUBS, Rank.JACK).getPoints())
        assertEquals(2, Card(Suit.SPADES, Rank.JACK).getPoints())
        assertEquals(3, Card(Suit.DIAMONDS, Rank.TEN).getPoints())
        assertEquals(2, Card(Suit.CLUBS, Rank.TWO).getPoints())
        
        // Test regular cards
        assertEquals(0, Card(Suit.HEARTS, Rank.FIVE).getPoints())
        assertEquals(0, Card(Suit.DIAMONDS, Rank.JACK).getPoints()) // Not clubs or spades
    }

    @Test
    fun calculateFinalScores_shouldAwardMostCardsBonus() {
        val players = listOf(
            Player("1", "Player 1", PlayerType.HUMAN),
            Player("2", "Player 2", PlayerType.AI)
        )
        val gameState = gameEngine.initializeGame("test", players, GameMode.OFFLINE_SINGLE_PLAYER)
        
        // Give player 1 more cards
        gameState.players[0].capturedCards.addAll(listOf(
            Card(Suit.HEARTS, Rank.TWO),
            Card(Suit.HEARTS, Rank.THREE),
            Card(Suit.HEARTS, Rank.FOUR)
        ))
        
        gameState.players[1].capturedCards.add(Card(Suit.CLUBS, Rank.TWO))
        
        val finalState = gameEngine.calculateFinalScores(gameState)
        
        assertTrue(finalState.isGameOver)
        assertNotNull(finalState.winner)
    }
}