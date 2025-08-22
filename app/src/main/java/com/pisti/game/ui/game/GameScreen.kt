package com.pisti.game.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pisti.game.ui.game.components.GameBoard
import com.pisti.game.ui.game.components.PlayerHand
import com.pisti.game.ui.game.components.ScoreBoard
import com.pisti.game.ui.game.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onGameEnd: () -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current
    val gameState by gameViewModel.gameState.observeAsState()
    val currentState = gameState
    
    // Initialize game on first composition
    LaunchedEffect(Unit) {
        gameViewModel.initializeGame()
    }
    
    // Handle game end
    LaunchedEffect(currentState?.isGameOver) {
        if (currentState?.isGameOver == true) {
            // TODO: Show game end dialog before navigating
        }
    }
    
    if (currentState == null) {
        // Loading state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            
            // Top: Opponent players and score
            ScoreBoard(
                players = currentState.players,
                currentPlayerId = currentState.getCurrentPlayer().id,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Middle: Game board (table cards)
            GameBoard(
                tableCards = currentState.tableCards,
                lastCapture = currentState.lastCapture,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bottom: Human player's hand
            val humanPlayer = currentState.getHumanPlayer()
            if (humanPlayer != null) {
                PlayerHand(
                    player = humanPlayer,
                    isCurrentPlayer = currentState.getCurrentPlayer().id == humanPlayer.id,
                    onCardSelected = { card ->
                        gameViewModel.playCard(card)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Game controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onGameEnd
                ) {
                    Text("Çık")
                }
                
                if (currentState.isGameOver) {
                    Button(
                        onClick = {
                            gameViewModel.initializeGame()
                        }
                    ) {
                        Text("Yeni Oyun")
                    }
                }
            }
        }
    }
}