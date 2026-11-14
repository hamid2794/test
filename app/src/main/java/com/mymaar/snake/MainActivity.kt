package com.mymaar.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyMaarGame() }
    }
}

private data class Cell(val x: Int, val y: Int)
private enum class Direction { UP, DOWN, LEFT, RIGHT }

private val NokiaGreen = Color(0xFF9BBC0F)
private val DarkGreen = Color(0xFF0F380F)
private val MidGreen = Color(0xFF306230)
private val LightGreen = Color(0xFF8BAC0F)

@Composable
private fun MyMaarGame() {
    val columns = 20
    val rows = 24
    val snake = remember { mutableStateListOf(Cell(9, 12), Cell(8, 12), Cell(7, 12)) }
    var food by remember { mutableStateOf(Cell(14, 10)) }
    var direction by remember { mutableStateOf(Direction.RIGHT) }
    var nextDirection by remember { mutableStateOf(Direction.RIGHT) }
    var score by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }

    fun randomFood(currentSnake: List<Cell>): Cell {
        var candidate: Cell
        do {
            candidate = Cell(Random.nextInt(columns), Random.nextInt(rows))
        } while (candidate in currentSnake)
        return candidate
    }

    fun restart() {
        snake.clear()
        snake.addAll(listOf(Cell(9, 12), Cell(8, 12), Cell(7, 12)))
        food = Cell(14, 10)
        direction = Direction.RIGHT
        nextDirection = Direction.RIGHT
        score = 0
        gameOver = false
        running = true
    }

    fun turn(newDirection: Direction) {
        val opposite = when (direction) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.LEFT -> Direction.RIGHT
            Direction.RIGHT -> Direction.LEFT
        }
        if (newDirection != opposite) nextDirection = newDirection
    }

    LaunchedEffect(running, gameOver, score) {
        while (running && !gameOver) {
            delay((190L - score.toLong() * 3L).coerceAtLeast(75L))
            direction = nextDirection
            val head = snake.first()
            val newHead = when (direction) {
                Direction.UP -> Cell(head.x, head.y - 1)
                Direction.DOWN -> Cell(head.x, head.y + 1)
                Direction.LEFT -> Cell(head.x - 1, head.y)
                Direction.RIGHT -> Cell(head.x + 1, head.y)
            }
            val eating = newHead == food
            val bodyToCheck = if (eating) snake.toList() else snake.dropLast(1)
            if (newHead.x !in 0 until columns || newHead.y !in 0 until rows || newHead in bodyToCheck) {
                running = false
                gameOver = true
            } else {
                snake.add(0, newHead)
                if (eating) {
                    score += 10
                    food = randomFood(snake)
                } else {
                    snake.removeAt(snake.lastIndex)
                }
            }
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = NokiaGreen) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("MYMAAR", color = DarkGreen, fontSize = 32.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 5.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SCORE", color = DarkGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(score.toString().padStart(4, '0'), color = DarkGreen,
                        fontWeight = FontWeight.Black, fontSize = 22.sp)
                }
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .background(DarkGreen, RoundedCornerShape(8.dp)).padding(7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cellWidth = size.width / columns
                        val cellHeight = size.height / rows
                        for (x in 0 until columns) {
                            for (y in 0 until rows) {
                                drawRect(
                                    color = MidGreen.copy(alpha = 0.22f),
                                    topLeft = Offset(x * cellWidth, y * cellHeight),
                                    size = androidx.compose.ui.geometry.Size(cellWidth - 1f, cellHeight - 1f)
                                )
                            }
                        }
                        drawRect(
                            color = Color(0xFFFFD54F),
                            topLeft = Offset(food.x * cellWidth + cellWidth * 0.18f, food.y * cellHeight + cellHeight * 0.18f),
                            size = androidx.compose.ui.geometry.Size(cellWidth * 0.64f, cellHeight * 0.64f)
                        )
                        snake.forEachIndexed { index, cell ->
                            drawRect(
                                color = if (index == 0) LightGreen else NokiaGreen,
                                topLeft = Offset(cell.x * cellWidth + 1f, cell.y * cellHeight + 1f),
                                size = androidx.compose.ui.geometry.Size(cellWidth - 2f, cellHeight - 2f)
                            )
                        }
                    }
                    if (!running) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (gameOver) "GAME OVER" else "READY?",
                                color = NokiaGreen, fontWeight = FontWeight.Black, fontSize = 25.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(if (gameOver) "Final score: $score" else "Press START to play",
                                color = NokiaGreen, fontSize = 14.sp)
                        }
                    }
                }
                Text("USE THE ARROWS", color = DarkGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DirectionButton("▲") { turn(Direction.UP) }
                    Row(horizontalArrangement = Arrangement.spacedBy(34.dp)) {
                        DirectionButton("◀") { turn(Direction.LEFT) }
                        DirectionButton("▼") { turn(Direction.DOWN) }
                        DirectionButton("▶") { turn(Direction.RIGHT) }
                    }
                }
                Button(
                    onClick = { if (gameOver || !running) restart() else running = false },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = NokiaGreen)
                ) {
                    Text(when {
                        gameOver -> "PLAY AGAIN"
                        running -> "PAUSE"
                        else -> "START"
                    }, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                }
                Text("CLASSIC SNAKE • NOKIA STYLE", color = DarkGreen, fontSize = 10.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun DirectionButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 66.dp, height = 48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = NokiaGreen)
    ) {
        Text(label, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
}
