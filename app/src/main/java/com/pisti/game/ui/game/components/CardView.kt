package com.pisti.game.ui.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pisti.game.data.models.Card
import com.pisti.game.data.models.CardColor
import com.pisti.game.ui.theme.CardRed
import com.pisti.game.ui.theme.CardBlack
import com.pisti.game.ui.theme.CardBack
import com.pisti.game.ui.theme.CardSelected

@Composable
fun CardView(
    card: Card?,
    isSelected: Boolean = false,
    isClickable: Boolean = false,
    showBack: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val cardModifier = modifier
        .size(width = 60.dp, height = 84.dp)
        .clip(RoundedCornerShape(8.dp))
        .then(
            if (isClickable && onClick != null) {
                Modifier.clickable { onClick() }
            } else {
                Modifier
            }
        )
        .border(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) CardSelected else Color.Gray,
            shape = RoundedCornerShape(8.dp)
        )
    
    Card(
        modifier = cardModifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        if (showBack) {
            // Card back design
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CardBack),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🂠",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        } else if (card != null) {
            // Card front
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top rank
                    Text(
                        text = card.rank.symbol,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (card.suit.color == CardColor.RED) CardRed else CardBlack,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    // Center suit
                    Text(
                        text = card.suit.symbol,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (card.suit.color == CardColor.RED) CardRed else CardBlack,
                        textAlign = TextAlign.Center
                    )
                    
                    // Bottom rank (rotated)
                    Text(
                        text = card.rank.symbol,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (card.suit.color == CardColor.RED) CardRed else CardBlack,
                        modifier = Modifier
                            .align(Alignment.End)
                            .graphicsLayer {
                                rotationZ = 180f
                            }
                    )
                }
            }
        } else {
            // Empty card slot
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    fontSize = 24.sp,
                    color = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun Modifier.graphicsLayer(
    block: androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit
): Modifier = this.then(
    androidx.compose.ui.graphics.graphicsLayer(block = block)
)