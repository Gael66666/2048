package com.gael66666.game2048

import android.os.Bundle
import android.app.Activity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.Gravity
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : Activity(), GestureDetector.OnGestureListener {

    private lateinit var gestureDetector: GestureDetector
    private val grid = Array(4) { IntArray(4) { 0 } }
    private lateinit var gridLayout: GridLayout
    private val textViews = Array(4) { arrayOfNulls<TextView>(4) }
    private var score = 0
    private lateinit var scoreTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestureDetector = GestureDetector(this, this)

        // Fond vert très sombre extrait de ta version PC
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.parseColor("#0D1F17"))
        }

        // Titre 2048 en Doré/Orange comme sur ton PC
        val titleTextView = TextView(this).apply {
            text = "2048"
            textSize = 38f
            setTextColor(Color.parseColor("#EBB859"))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 70, 0, 5)
        }
        mainLayout.addView(titleTextView)

        // Sous-titre "Fusionnez les cristaux"
        val subtitleTextView = TextView(this).apply {
            text = "Fusionnez les cristaux"
            textSize = 15f
            setTextColor(Color.parseColor("#7FA392"))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
        }
        mainLayout.addView(subtitleTextView)

        // Score (Texte blanc basique, centré)
        scoreTextView = TextView(this).apply {
            text = "SCORE\n0"
            textSize = 18f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 50)
        }
        mainLayout.addView(scoreTextView)

        // Grille de jeu avec le contour vert foncé transparent de ton PC
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val gridPadding = 40
        val gridWidth = screenWidth - gridPadding

        gridLayout = GridLayout(this).apply {
            rowCount = 4
            columnCount = 4
            setBackgroundColor(Color.parseColor("#142A20"))
            setPadding(18, 18, 18, 18)
            
            layoutParams = LinearLayout.LayoutParams(gridWidth, gridWidth).apply {
                gravity = Gravity.CENTER
            }
        }

        val tileSize = (gridWidth - 36 - (8 * 8)) / 4

        for (i in 0..3) {
            for (j in 0..3) {
                val tv = TextView(this).apply {
                    width = tileSize
                    height = tileSize
                    gravity = Gravity.CENTER
                    textSize = 28f
                    typeface = Typeface.DEFAULT_BOLD
                    
                    val params = GridLayout.LayoutParams().apply {
                        rowSpec = GridLayout.spec(i)
                        columnSpec = GridLayout.spec(j)
                        setMargins(8, 8, 8, 8)
                    }
                    layoutParams = params
                }
                textViews[i][j] = tv
                gridLayout.addView(tv)
            }
        }

        mainLayout.addView(gridLayout)
        setContentView(mainLayout)

        initGame()
    }

    private fun initGame() {
        score = 0
        for (i in 0..3) {
            for (j in 0..3) {
                grid[i][j] = 0
            }
        }
        addNewTile()
        addNewTile()
        updateUI()
    }

    private fun addNewTile() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..3) {
            for (j in 0..3) {
                if (grid[i][j] == 0) emptyCells.add(Pair(i, j))
            }
        }
        if (emptyCells.isNotEmpty()) {
            val (r, c) = emptyCells[Random.nextInt(emptyCells.size)]
            grid[r][c] = if (Random.nextFloat() < 0.9f) 2 else 4
        }
    }

    private fun updateUI() {
        scoreTextView.text = "SCORE\n$score"
        for (i in 0..3) {
            for (j in 0..3) {
                val value = grid[i][j]
                val tv = textViews[i][j]!!
                if (value == 0) {
                    tv.text = ""
                    // Case vide vert/gris foncé du fond de la grille PC
                    tv.setBackgroundColor(Color.parseColor("#1B3327"))
                } else {
                    tv.text = value.toString()
                    
                    when (value) {
                        // Le fameux "2" couleur menthe/vert pastel très clair avec texte foncé
                        2 -> { tv.setBackgroundColor(Color.parseColor("#D5EAD8")); tv.setTextColor(Color.parseColor("#153020")) }
                        // Dégradés de verts et couleurs de cristaux pour la suite
                        4 -> { tv.setBackgroundColor(Color.parseColor("#A8D8B9")); tv.setTextColor(Color.parseColor("#153020")) }
                        8 -> { tv.setBackgroundColor(Color.parseColor("#71B38D")); tv.setTextColor(Color.WHITE) }
                        16 -> { tv.setBackgroundColor(Color.parseColor("#448E65")); tv.setTextColor(Color.WHITE) }
                        32 -> { tv.setBackgroundColor(Color.parseColor("#236B43")); tv.setTextColor(Color.WHITE) }
                        64 -> { tv.setBackgroundColor(Color.parseColor("#0E4728")); tv.setTextColor(Color.WHITE) }
                        128 -> { tv.setBackgroundColor(Color.parseColor("#EBB859")); tv.setTextColor(Color.WHITE) }
                        256 -> { tv.setBackgroundColor(Color.parseColor("#E5A63B")); tv.setTextColor(Color.WHITE) }
                        512 -> { tv.setBackgroundColor(Color.parseColor("#DF931E")); tv.setTextColor(Color.WHITE) }
                        1024 -> { tv.setBackgroundColor(Color.parseColor("#D77E00")); tv.setTextColor(Color.WHITE) }
                        2048 -> { tv.setBackgroundColor(Color.parseColor("#C96500")); tv.setTextColor(Color.WHITE) }
                        else -> { tv.setBackgroundColor(Color.parseColor("#000000")); tv.setTextColor(Color.WHITE) }
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y
        var moved = false

        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > 100 && abs(velocityX) > 100) {
                if (diffX > 0) moved = slideRight() else moved = slideLeft()
            }
        } else {
            if (abs(diffY) > 100 && abs(velocityY) > 100) {
                if (diffY > 0) moved = slideDown() else moved = slideUp()
            }
        }

        if (moved) {
            addNewTile()
            updateUI()
        }
        return true
    }

    private fun slideLeft(): Boolean {
        var moved = false
        for (i in 0..3) {
            val row = grid[i].filter { it != 0 }.toMutableList()
            val newRow = mutableListOf<Int>()
            var j = 0
            while (j < row.size) {
                if (j + 1 < row.size && row[j] == row[j + 1]) {
                    val merged = row[j] * 2
                    newRow.add(merged)
                    score += merged
                    j += 2
                    moved = true
                } else {
                    newRow.add(row[j])
                    j++
                }
            }
            while (newRow.size < 4) newRow.add(0)
            
            val newArray = newRow.toIntArray()
            if (!grid[i].contentEquals(newArray)) {
                moved = true
            }
            grid[i] = newArray
        }
        return moved
    }

    private fun slideRight(): Boolean {
        rotateGrid()
        rotateGrid()
        val moved = slideLeft()
        rotateGrid()
        rotateGrid()
        return moved
    }

    private fun slideUp(): Boolean {
        rotateGrid()
        rotateGrid()
        rotateGrid()
        val moved = slideLeft()
        rotateGrid()
        return moved
    }

    private fun slideDown(): Boolean {
        rotateGrid()
        val moved = slideLeft()
        rotateGrid()
        rotateGrid()
        rotateGrid()
        return moved
    }

    private fun rotateGrid() {
        val temp = Array(4) { IntArray(4) }
        for (i in 0..3) {
            for (j in 0..3) {
                temp[j][3 - i] = grid[i][j]
            }
        }
        for (i in 0..3) grid[i] = temp[i].clone()
    }

    override fun onDown(e: MotionEvent): Boolean = true
    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean = false
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent) {}
}