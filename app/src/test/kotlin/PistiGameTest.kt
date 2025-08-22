package com.pisti.game.test

import com.pisti.game.data.models.*
import com.pisti.game.game.engine.PistiGameEngine
import com.pisti.game.game.engine.GameResult

fun main() {
    println("🎮 Pişti Game Engine Test")
    println("=" * 40)
    
    testCardCreation()
    testDeckCreation()
    testGameInitialization()
    testCardCapture()
    testPisti()
    testScoring()
    
    println("\n✅ All tests completed successfully!")
}

fun testCardCreation() {
    println("\n🧪 Testing Card Creation...")
    
    val aceOfHearts = Card(Suit.HEARTS, Rank.ACE)
    assert(aceOfHearts.getPoints() == 1) { "Ace should be worth 1 point" }
    
    val jackOfClubs = Card(Suit.CLUBS, Rank.JACK)
    assert(jackOfClubs.getPoints() == 2) { "Jack of Clubs should be worth 2 points" }
    
    val jackOfSpades = Card(Suit.SPADES, Rank.JACK)
    assert(jackOfSpades.getPoints() == 2) { "Jack of Spades should be worth 2 points" }
    
    val tenOfDiamonds = Card(Suit.DIAMONDS, Rank.TEN)
    assert(tenOfDiamonds.getPoints() == 3) { "Ten of Diamonds should be worth 3 points" }
    
    val twoOfClubs = Card(Suit.CLUBS, Rank.TWO)
    assert(twoOfClubs.getPoints() == 2) { "Two of Clubs should be worth 2 points" }
    
    val regularCard = Card(Suit.HEARTS, Rank.FIVE)
    assert(regularCard.getPoints() == 0) { "Regular cards should be worth 0 points" }
    
    println("✅ Card creation and scoring tests passed")
}

fun testDeckCreation() {
    println("\n🧪 Testing Deck Creation...")
    
    val gameEngine = PistiGameEngine()
    val deck = gameEngine.createDeck()
    
    assert(deck.size == 52) { "Deck should have 52 cards" }
    
    val suits = deck.map { it.suit }.distinct()
    val ranks = deck.map { it.rank }.distinct()
    
    assert(suits.size == 4) { "Deck should have 4 suits" }
    assert(ranks.size == 13) { "Deck should have 13 ranks" }
    
    println("✅ Deck creation tests passed")
}

fun testGameInitialization() {
    println("\n🧪 Testing Game Initialization...")
    
    val gameEngine = PistiGameEngine()
    val players = listOf(
        Player("1", "Human", PlayerType.HUMAN),
        Player("2", "AI1", PlayerType.AI, AIDifficulty.BEGINNER),
        Player("3", "AI2", PlayerType.AI, AIDifficulty.INTERMEDIATE),
        Player("4", "AI3", PlayerType.AI, AIDifficulty.EXPERT)
    )
    
    val gameState = gameEngine.initializeGame("test", players, GameMode.OFFLINE_SINGLE_PLAYER)
    
    assert(gameState.players.size == 4) { "Should have 4 players" }
    assert(gameState.tableCards.size == 4) { "Should have 4 cards on table initially" }
    
    gameState.players.forEach { player ->
        assert(player.hand.size == 4) { "Each player should have 4 cards" }
    }
    
    // 52 total - 4 table - 16 in hands = 32 in deck
    assert(gameState.deck.size == 32) { "Deck should have 32 cards remaining" }
    
    println("✅ Game initialization tests passed")
}

fun testCardCapture() {
    println("\n🧪 Testing Card Capture...")
    
    val gameEngine = PistiGameEngine()
    val players = listOf(Player("1", "Test", PlayerType.HUMAN))
    val gameState = gameEngine.initializeGame("test", players, GameMode.OFFLINE_SINGLE_PLAYER)
    
    // Set up capture scenario
    val tableCard = Card(Suit.HEARTS, Rank.KING)
    val playerCard = Card(Suit.CLUBS, Rank.KING)
    
    gameState.tableCards.clear()
    gameState.tableCards.add(tableCard)
    gameState.players[0].hand.clear()
    gameState.players[0].hand.add(playerCard)
    
    val result = gameEngine.playCard(gameState, playerCard)
    
    assert(result is GameResult.Capture) { "Should result in a capture" }
    assert(gameState.players[0].capturedCards.size == 1) { "Player should have captured 1 card" }
    assert(gameState.tableCards.isEmpty()) { "Table should be empty after capture" }
    
    println("✅ Card capture tests passed")
}

fun testPisti() {
    println("\n🧪 Testing Pişti...")
    
    val gameEngine = PistiGameEngine()
    val players = listOf(Player("1", "Test", PlayerType.HUMAN))
    val gameState = gameEngine.initializeGame("test", players, GameMode.OFFLINE_SINGLE_PLAYER)
    
    // Set up Pişti scenario
    val tableCard = Card(Suit.HEARTS, Rank.QUEEN)
    val jack = Card(Suit.CLUBS, Rank.JACK)
    
    gameState.tableCards.clear()
    gameState.tableCards.add(tableCard)
    gameState.players[0].hand.clear()
    gameState.players[0].hand.add(jack)
    
    val result = gameEngine.playCard(gameState, jack)
    
    assert(result is GameResult.Capture) { "Should result in capture" }
    
    val capture = (result as GameResult.Capture).capture
    assert(capture.isPisti) { "Should be a Pişti" }
    assert(gameState.players[0].pistiCount == 1) { "Player should have 1 Pişti" }
    
    println("✅ Pişti tests passed")
}

fun testScoring() {
    println("\n🧪 Testing Scoring System...")
    
    val player = Player("test", "Test", PlayerType.HUMAN)
    
    // Add some captured cards
    player.capturedCards.addAll(listOf(
        Card(Suit.HEARTS, Rank.ACE),      // 1 point + 1 for capture = 2
        Card(Suit.CLUBS, Rank.JACK),      // 2 points + 1 for capture = 3  
        Card(Suit.DIAMONDS, Rank.TEN),    // 3 points + 1 for capture = 4
        Card(Suit.HEARTS, Rank.FIVE),     // 0 points + 1 for capture = 1
        Card(Suit.CLUBS, Rank.TWO)        // 2 points + 1 for capture = 3
    ))
    
    player.pistiCount = 2  // 20 points from Pişti
    
    val totalPoints = player.calculateTotalPoints()
    // Expected: (1+2+3+0+2) card values + 5 cards captured + 20 Pişti = 33
    val expected = 8 + 5 + 20
    
    assert(totalPoints == expected) { "Total points should be $expected, got $totalPoints" }
    
    println("✅ Scoring tests passed")
}

operator fun String.times(n: Int): String = this.repeat(n)

fun assert(condition: Boolean, message: () -> String) {
    if (!condition) {
        throw AssertionError(message())
    }
}