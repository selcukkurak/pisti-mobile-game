package com.pisti.game.ui.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pisti.game.data.models.Player
import com.pisti.game.data.models.PlayerType

@Composable
fun ScoreBoard(
    players: List<Player>,
    currentPlayerId: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Oyuncular",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            players.forEach { player ->
                PlayerScore(
                    player = player,
                    isCurrentPlayer = player.id == currentPlayerId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerScore(
    player: Player,
    isCurrentPlayer: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlayer) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Player type indicator
                Text(
                    text = if (player.type == PlayerType.HUMAN) "👤" else "🤖",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (isCurrentPlayer) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "●",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Captured cards count
                Text(
                    text = "Kart: ${player.capturedCards.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                // Pişti count
                if (player.pistiCount > 0) {
                    Text(
                        text = "Pişti: ${player.pistiCount}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Total score
                Text(
                    text = "${player.score}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}