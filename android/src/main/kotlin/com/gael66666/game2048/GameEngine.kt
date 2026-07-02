package com.gael66666.game2048

import kotlin.random.Random

enum class Direction {
    LEFT, RIGHT, UP, DOWN
}

data class GameState(
    val board: Array<IntArray> = Array(4) { IntArray(4) },
    val score: Int = 0,
    val best: Int = 0,
    val isWon: Boolean = false,
    val isOver: Boolean = false,
    val keepPlaying: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameState) return false
        if (score != other.score) return false
        if (best != other.best) return false
        if (isWon != other.isWon) return false
        if (isOver != other.isOver) return false
        if (keepPlaying != other.keepPlaying) return false
        if (!board.contentDeepEquals(other.board)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + score
        result = 31 * result + best
        result = 31 * result + isWon.hashCode()
        result = 31 * result + isOver.hashCode()
        result = 31 * result + keepPlaying.hashCode()
        return result
    }
}

object GameEngine {
    
    fun initializeGame(): GameState {
        val board = Array(4) { IntArray(4) }
        val state = GameState(board = board)
        addRandomTile(state)
        addRandomTile(state)
        return state
    }
    
    fun move(state: GameState, direction: Direction): Boolean {
        val board = state.board
        val oldBoard = board.map { it.copyOf() }.toTypedArray()
        var score = 0
        
        when (direction) {
            Direction.LEFT -> {
                repeat(4) { row ->
                    val line = board[row]
                    val (newLine, gain) = slideLine(line)
                    board[row] = newLine
                    score += gain
                }
            }
            Direction.RIGHT -> {
                repeat(4) { row ->
                    val line = board[row].reversedArray()
                    val (newLine, gain) = slideLine(line)
                    board[row] = newLine.reversedArray()
                    score += gain
                }
            }
            Direction.UP -> {
                repeat(4) { col ->
                    val line = IntArray(4) { row -> board[row][col] }
                    val (newLine, gain) = slideLine(line)
                    repeat(4) { row -> board[row][col] = newLine[row] }
                    score += gain
                }
            }
            Direction.DOWN -> {
                repeat(4) { col ->
                    val line = IntArray(4) { row -> board[3 - row][col] }
                    val (newLine, gain) = slideLine(line)
                    repeat(4) { row -> board[3 - row][col] = newLine[row] }
                    score += gain
                }
            }
        }
        
        val moved = !board.contentDeepEquals(oldBoard)
        
        if (moved) {
            val newState = state.copy(
                score = state.score + score,
                best = maxOf(state.best, state.score + score)
            )
            addRandomTile(newState)
            
            // Check for win
            var won = newState.isWon
            if (!won) {
                for (row in 0..3) {
                    for (col in 0..3) {
                        if (newState.board[row][col] >= 2048) won = true
                    }
                }
            }
            
            // Check for game over
            val over = !hasMovesAvailable(newState)
            
            return true
        }
        
        return false
    }
    
    private fun slideLine(line: IntArray): Pair<IntArray, Int> {
        // Compact
        val temp = IntArray(4)
        var idx = 0
        for (v in line) {
            if (v != 0) temp[idx++] = v
        }
        
        // Merge
        var gain = 0
        var i = 0
        while (i < 3) {
            if (temp[i] != 0 && temp[i] == temp[i + 1]) {
                temp[i] *= 2
                gain += temp[i]
                for (j in i + 1..2) temp[j] = temp[j + 1]
                temp[3] = 0
            }
            i++
        }
        
        return temp to gain
    }
    
    private fun addRandomTile(state: GameState) {
        val emptyPositions = mutableListOf<Pair<Int, Int>>()
        for (row in 0..3) {
            for (col in 0..3) {
                if (state.board[row][col] == 0) {
                    emptyPositions.add(row to col)
                }
            }
        }
        
        if (emptyPositions.isNotEmpty()) {
            val (row, col) = emptyPositions.random()
            state.board[row][col] = if (Random.nextDouble() < 0.9) 2 else 4
        }
    }
    
    private fun hasMovesAvailable(state: GameState): Boolean {
        val board = state.board
        for (row in 0..3) {
            for (col in 0..3) {
                if (board[row][col] == 0) return true
                if (col < 3 && board[row][col] == board[row][col + 1]) return true
                if (row < 3 && board[row][col] == board[row + 1][col]) return true
            }
        }
        return false
    }
}