package com.example.jeu

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val symbols = intArrayOf(R.drawable.strawberry, R.drawable.berry, R.drawable.peach)
    private var solde = 1000
    private var mise = 100
    val spinButton = findViewById<Button>(R.id.btnPlay)
    val resetButton = findViewById<Button>(R.id.btnReset)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<TextView>(R.id.tvBalance).text = "Solde : $solde"
        spinButton.setOnClickListener {
            spinReels()
        }
        resetButton.setOnClickListener {
            resetGame()
        }
    }

    private fun spinReels() {
        // Deduct the bet amount
        solde -= mise
        findViewById<TextView>(R.id.tvBalance).text = "Solde : $solde"

        // Check if the balance is 0 or less
        if (solde <= 0) {
            solde = 0 // Prevent negative balance
            findViewById<TextView>(R.id.tvBalance).text = "Solde : $solde"
            showLoseDialog() // Show dialog when solde reaches 0
            return // Exit the function to prevent further checks
        }
        // Generate random reel indices
        val reel1Index = Random.nextInt(symbols.size)
        val reel2Index = Random.nextInt(symbols.size)
        val reel3Index = Random.nextInt(symbols.size)
        // Set images for each reel
        findViewById<ImageView>(R.id.reel1).setImageResource(symbols[reel1Index])
        findViewById<ImageView>(R.id.reel2).setImageResource(symbols[reel2Index])
        findViewById<ImageView>(R.id.reel3).setImageResource(symbols[reel3Index])
        // Check for win if the player hasn't lost
        checkWin(reel1Index, reel2Index, reel3Index)
    }

    private fun showLoseDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_lose)
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun checkWin(reel1Index: Int, reel2Index: Int, reel3Index: Int) {
        if (reel1Index == reel2Index && reel2Index == reel3Index) {
            solde += mise * 5
            findViewById<TextView>(R.id.tvBalance).text = "Solde : $solde"
            playWinSound()
            println("Félicitation! VOUS AVEZ GAGNÉ!")
        } else {
            println("DOMMAGE, RÉESSAYEZ!")
        }
    }

    private fun resetGame() {
        solde = 1000
        findViewById<TextView>(R.id.tvBalance).text = "Solde : $solde"
        // Reset the reels to a default state, if needed
        findViewById<ImageView>(R.id.reel1).setImageResource(R.drawable.strawberry)
        findViewById<ImageView>(R.id.reel2).setImageResource(R.drawable.peach)
        findViewById<ImageView>(R.id.reel3).setImageResource(R.drawable.berry)
    }

    private fun playWinSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.win_sound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }
}