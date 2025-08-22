package com.pisti.game.test

import com.pisti.game.data.models.*
import com.pisti.game.game.engine.PistiGameEngine
import com.pisti.game.game.engine.GameResult
import com.pisti.game.game.ai.AIPlayerFactory

/**
 * Simulate a complete Pişti game with AI players
 */
fun main() {
    println("🎮 Pişti AI Game Simulation")
    println("=" * 50)
    
    val gameEngine = PistiGameEngine()
    
    // Create players
    val players = listOf(
        Player("human", "İnsan Oyuncu", PlayerType.HUMAN),
        Player("ai1", "Ahmet (Kolay)", PlayerType.AI, AIDifficulty.BEGINNER),
        Player("ai2", "Fatma (Orta)", PlayerType.AI, AIDifficulty.INTERMEDIATE), 
        Player("ai3", "Mehmet (Zor)", PlayerType.AI, AIDifficulty.EXPERT)
    )
    
    println("\n👥 Oyuncular:")
    players.forEach { player ->
        val difficulty = when (player.aiDifficulty) {
            AIDifficulty.BEGINNER -> "Kolay"
            AIDifficulty.INTERMEDIATE -> "Orta"
            AIDifficulty.EXPERT -> "Zor"
        }
        val type = if (player.type == PlayerType.HUMAN) "İnsan" else "AI ($difficulty)"
        println("  - ${player.name} [$type]")
    }
    
    // Initialize game
    var gameState = gameEngine.initializeGame("simulation", players, GameMode.OFFLINE_SINGLE_PLAYER)
    
    println("\n🎯 Oyun Başlatılıyor...")
    println("Masa kartları: ${gameState.tableCards.size}")
    println("Her oyuncunun kartları: ${gameState.players[0].hand.size}")
    println("Destede kalan: ${gameState.deck.size}")
    
    var turnCount = 0
    var roundCount = 1
    
    // Simulate game
    while (!gameEngine.isGameComplete(gameState) && turnCount < 100) { // Safety limit
        val currentPlayer = gameState.getCurrentPlayer()
        turnCount++
        
        println("\n--- Sıra ${turnCount}: ${currentPlayer.name} ---")
        println("Masadaki kartlar: ${gameState.tableCards.size} ${if (gameState.tableCards.isNotEmpty()) "(" + gameState.tableCards.last().rank.symbol + gameState.tableCards.last().suit.symbol + " üstte)" else ""}")
        println("Eldeki kartlar: ${currentPlayer.hand.size}")
        
        val cardToPlay = if (currentPlayer.type == PlayerType.HUMAN) {
            // For simulation, let human player play first available card
            currentPlayer.hand.firstOrNull()
        } else {
            // AI chooses card
            val aiPlayer = AIPlayerFactory.createAIPlayer(currentPlayer)
            aiPlayer.chooseCard(gameState)
        }
        
        if (cardToPlay == null) {
            println("❌ Oynanacak kart yok!")
            break
        }
        
        println("🎴 Oynanan kart: ${cardToPlay.rank.symbol}${cardToPlay.suit.symbol}")
        
        val result = gameEngine.playCard(gameState, cardToPlay)
        
        when (result) {
            is GameResult.Capture -> {
                val capture = result.capture
                if (capture.isPisti) {
                    println("🎉 PİŞTİ! ${currentPlayer.name} ${capture.capturedCards.size} kart yakaladı (+10 bonus)")
                } else {
                    println("✅ ${currentPlayer.name} ${capture.capturedCards.size} kart yakaladı")
                }
            }
            is GameResult.CardPlayed -> {
                println("📌 Kart masaya konuldu")
            }
            is GameResult.InvalidMove -> {
                println("❌ Geçersiz hamle: ${result.reason}")
                break
            }
        }
        
        // Move to next player
        gameState = gameEngine.nextPlayer(gameState)
        
        // Check round end
        val newGameState = gameEngine.checkRoundEnd(gameState)
        if (newGameState.round > gameState.round) {
            roundCount = newGameState.round
            println("\n🔄 Round $roundCount başlıyor - Yeni kartlar dağıtıldı")
        }
        gameState = newGameState
        
        // Show current scores every 10 turns
        if (turnCount % 10 == 0) {
            showScores(gameState.players)
        }
    }
    
    // Calculate final scores
    val finalState = gameEngine.calculateFinalScores(gameState)
    
    println("\n🏁 OYUN BİTTİ!")
    println("Total ${turnCount} hamle oynadı, ${roundCount} round tamamlandı")
    
    showFinalResults(finalState.players)
    
    finalState.winner?.let { winner ->
        println("\n🏆 KAZANAN: ${winner.name}!")
        println("Final Skoru: ${winner.score} puan")
    }
}

fun showScores(players: List<Player>) {
    println("\n📊 Anlık Durum:")
    players.forEach { player ->
        println("  ${player.name}: ${player.capturedCards.size} kart, ${player.pistiCount} Pişti, ${player.score} puan")
    }
}

fun showFinalResults(players: List<Player>) {
    println("\n📊 FİNAL SONUÇLARI:")
    val sortedPlayers = players.sortedByDescending { it.score }
    
    sortedPlayers.forEachIndexed { index, player ->
        val position = when(index) {
            0 -> "🥇"
            1 -> "🥈" 
            2 -> "🥉"
            else -> "  "
        }
        
        val totalCardValue = player.capturedCards.sumOf { it.getPoints() }
        val capturedCount = player.capturedCards.size
        val pistiBonus = player.pistiCount * 10
        
        println("$position ${player.name}:")
        println("    Yakalanan kartlar: $capturedCount (her biri 1 puan)")
        println("    Kart değerleri: $totalCardValue puan")
        println("    Pişti sayısı: ${player.pistiCount} x 10 = $pistiBonus puan")
        println("    TOPLAM: ${player.score} puan")
        println()
    }
}

operator fun String.times(n: Int): String = this.repeat(n)