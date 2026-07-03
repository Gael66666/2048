package com.gael66666.game2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Game2048App()
        }
    }
}

@Composable
fun Game2048App() {
    val gameState = remember { mutableStateOf(GameEngine.initializeGame()) }
    val swipeDirection = remember { mutableStateOf<Direction?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1912))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "2048",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE8B854)
                )
                Text(
                    text = "Jardin de cristaux",
                    fontSize = 14.sp,
                    color = Color(0xFF7C9789)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        color = Color(0xFF172D1E),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "SCORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C9789)
                )
                Text(
                    text = gameState.value.score.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEAF3EC)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        color = Color(0xFF172D1E),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "MEILLEUR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C9789)
                )
                Text(
                    text = gameState.value.best.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE8B854)
                )
            }
        }
        
        // Game Board with Swipe Detection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(
                    color = Color(0xFF172D1E),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val (dx, dy) = dragAmount
                        val direction = when {
                            abs(dx) > abs(dy) -> if (dx > 0) Direction.RIGHT else Direction.LEFT
                            else -> if (dy > 0) Direction.DOWN else Direction.UP
                        }
                        if (GameEngine.move(gameState.value, direction)) {
                            gameState.value = gameState.value.copy()
                        }
                    }
                }
        ) {
            GameBoard(gameState.value.board)
            
            if (gameState.value.isWon && !gameState.value.keepPlaying) {
                GameOverlay(
                    text = "Cristal 2048 obtenu !",
                    onContinue = { gameState.value = gameState.value.copy(keepPlaying = true) },
                    onNewGame = { gameState.value = GameEngine.initializeGame() }
                )
            } else if (gameState.value.isOver) {
                GameOverlay(
                    text = "Plus aucun mouvement",
                    onContinue = null,
                    onNewGame = { gameState.value = GameEngine.initializeGame() }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Control Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DirectionButton("↑") {
                    if (GameEngine.move(gameState.value, Direction.UP)) {
                        gameState.value = gameState.value.copy()
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DirectionButton("←") {
                    if (GameEngine.move(gameState.value, Direction.LEFT)) {
                        gameState.value = gameState.value.copy()
                    }
                }
                DirectionButton("↓") {
                    if (GameEngine.move(gameState.value, Direction.DOWN)) {
                        gameState.value = gameState.value.copy()
                    }
                }
                DirectionButton("→") {
                    if (GameEngine.move(gameState.value, Direction.RIGHT)) {
                        gameState.value = gameState.value.copy()
                    }
                }
            }
            
            Button(
                onClick = { gameState.value = GameEngine.initializeGame() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8B854)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Nouvelle partie",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0E1912)
                )
            }
        }
    }
}

@Composable
fun GameBoard(board: Array<IntArray>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(4) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(4) { col ->
                    GameTile(board[row][col], modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun GameTile(value: Int, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = getTileColors(value)
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            Text(
                text = value.toString(),
                fontSize = when {
                    value < 100 -> 32.sp
                    value < 1000 -> 24.sp
                    else -> 18.sp
                },
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun DirectionButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                color = Color(0xFF8F7A66),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun GameOverlay(
    text: String,
    onContinue: (() -> Unit)?,
    onNewGame: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x990E1912))
            .clickable { }, // Prevent interaction with board
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE8B854)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onContinue != null) {
                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8B854)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Continuer",
                            color = Color(0xFF0E1912),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Button(
                    onClick = onNewGame,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C9789)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Nouvelle partie",
                        color = Color(0xFF0E1912),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun getTileColors(value: Int): Pair<Color, Color> {
    return when (value) {
        0 -> Color(0xFF1D2C24) to Color.Transparent
        2 -> Color(0xFFD9EFDF) to Color(0xFF1B2E22)
        4 -> Color(0xFFC1E6CE) to Color(0xFF1B2E22)
        8 -> Color(0xFF93D8AB) to Color(0xFF12261A)
        16 -> Color(0xFF63C98D) to Color(0xFF0F211F)
        32 -> Color(0xFF38B673) to Color(0xFFF2FBF5)
        64 -> Color(0xFF209E63) to Color(0xFFF2FBF5)
        128 -> Color(0xFF17916F) to Color(0xFFF2FBF5)
        256 -> Color(0xFF147E93) to Color(0xFFF2FBF5)
        512 -> Color(0xFF2C63AE) to Color(0xFFF2FBF5)
        1024 -> Color(0xFF6A3FA8) to Color(0xFFF5F0FF)
        2048 -> Color(0xFFE8B854) to Color(0xFF2A1D06)
        else -> Color(0xFFFF5A3C) to Color(0xFF2A0D05)
    }
}