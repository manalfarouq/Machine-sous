package com.example.jeu

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var extremeModeCheckBox: CheckBox
    private lateinit var balanceTextView: TextView
    private lateinit var betRadioGroup: RadioGroup
    private lateinit var secretCodeEditText: EditText
    private lateinit var playButton: Button
    private lateinit var addBalanceButton: Button
    private lateinit var noCodeButton: Button
    private lateinit var reelImageView1: ImageView
    private lateinit var reelImageView2: ImageView
    private lateinit var reelImageView3: ImageView
    private var balance = 100
    private var codeUsed = false // Indicateur pour l'utilisation du code secret

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser les vues
        balanceTextView = findViewById(R.id.soldeTextView)
        betRadioGroup = findViewById(R.id.miseRadioGroup)
        secretCodeEditText = findViewById(R.id.codeSecretEditText)
        playButton = findViewById(R.id.jouerButton)
        addBalanceButton = findViewById(R.id.ajouterSoldeButton)
        noCodeButton = findViewById(R.id.pasDeCodeButton)
        reelImageView1 = findViewById(R.id.monImageView1)
        reelImageView2 = findViewById(R.id.monImageView2)
        reelImageView3 = findViewById(R.id.monImageView3)
        extremeModeCheckBox = findViewById(R.id.casseCouCheckBox)


        // Afficher le solde initial
        updateBalance()



        // Bouton pour ajouter 100$ si le code est correct
        addBalanceButton.setOnClickListener {
            val secretCode = secretCodeEditText.text.toString()
            if (secretCode == "0000" && !codeUsed) {
                balance += 100
                updateBalance()
                secretCodeEditText.text.clear()
                Toast.makeText(this, "100$ ajoutés à votre solde !", Toast.LENGTH_SHORT).show()
                codeUsed = true
            } else if (codeUsed) {
                Toast.makeText(this, "Le code a déjà été utilisé.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Code incorrect.", Toast.LENGTH_SHORT).show()
            }
            hideButtons()
        }

        // Mode sans code
        noCodeButton.setOnClickListener {
            Toast.makeText(this, "Vous jouez sans code secret.", Toast.LENGTH_SHORT).show()
            hideButtons()
        }

        // Logique du bouton "Jouer"
        playButton.setOnClickListener {
            Log.d("MainActivity", "Bouton 'Jouer' cliqué")
            try {
                val bet = getBet()
                Log.d("MainActivity", "Mise sélectionnée : $bet, Solde disponible : $balance")

                if (bet == 0) {
                    Toast.makeText(this, "Veuillez choisir une mise", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (balance >= bet) {
                    balance -= bet // Déduire la mise du solde
                    updateBalance()

                    // Faire tourner les rouleaux
                    val reels = arrayOf(reelImageView1, reelImageView2, reelImageView3)
                    val images = arrayOf(
                        R.drawable.img7, R.drawable.fsa, R.drawable.banane,
                        R.drawable.rubis, R.drawable.diamant, R.drawable.sacargent, R.drawable.piece,
                        R.drawable.emeraude, R.drawable.charbon
                    )

                    // Assigner une image aléatoire à chaque rouleau et stocker la ressource dans le tag
                    reels.forEach {
                        val randomImage = images.random()
                        it.setImageResource(randomImage)
                        it.tag = randomImage // Stocker la ressource d'image dans le tag
                    }

                    // Vérifier le résultat
                    val mode = if (extremeModeCheckBox.isChecked) "Extrême" else "Normal"
                    val img1Res = reelImageView1.tag as Int
                    val img2Res = reelImageView2.tag as Int
                    val img3Res = reelImageView3.tag as Int

                    checkGameResult(bet, img1Res, img2Res, img3Res, mode)

                } else {
                    Toast.makeText(this, "Solde insuffisant.", Toast.LENGTH_SHORT).show()
                }


            } catch (e: Exception) {
                Log.e("MainActivity", "Erreur lors du clic sur le bouton 'Jouer'", e)
                Toast.makeText(this, "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Mettre à jour l'affichage du solde
    private fun updateBalance() {
        balanceTextView.text = "Solde : $balance"
    }

    // Obtenir la mise sélectionnée
    private fun getBet(): Int {
        val selectedBetId = betRadioGroup.checkedRadioButtonId
        return when (selectedBetId) {
            R.id.mise1 -> 1
            R.id.mise2 -> 2
            R.id.mise5 -> 5
            else -> 0
        }
    }

    // Cacher les boutons après l'utilisation du code ou en mode sans code
    private fun hideButtons() {
        addBalanceButton.visibility = View.INVISIBLE
        noCodeButton.visibility = View.INVISIBLE
    }

    // Vérifier le résultat du jeu en fonction du mode sélectionné
    private fun checkGameResult(bet: Int, img1Res: Int, img2Res: Int, img3Res: Int, mode: String) {
        when (mode) {
            "Normal" -> {
                when {
                    img1Res == img2Res && img2Res == img3Res -> {
                        // Trois images identiques
                        balance += bet * 25
                        updateBalance()
                        Toast.makeText(this, "Vous avez gagné ! Vous avez multiplié votre mise par 25.", Toast.LENGTH_SHORT).show()
                    }
                    img1Res == img2Res || img2Res == img3Res || img1Res == img3Res -> {
                        // Deux images identiques
                        balance += bet
                        updateBalance()
                        Toast.makeText(this, "Vous avez gagné ! Vous avez multiplié votre mise par 1.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Aucun gain
                        Toast.makeText(this, "Vous avez perdu ! Essayez encore.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            "Extrême" -> {
                when {
                    // Condition 1: Une image "img7" et deux images identiques
                    (img1Res == img2Res || img1Res == img3Res || img2Res == img3Res) &&
                            (img1Res == R.drawable.img7 || img2Res == R.drawable.img7 || img3Res == R.drawable.img7) -> {
                        balance += bet * 10
                        updateBalance()
                        Toast.makeText(this, "Vous avez gagné ! Vous avez multiplié votre mise par 10.", Toast.LENGTH_SHORT).show()
                    }
                    // Condition 2: Trois images identiques et "fsa"
                    img1Res == img2Res && img2Res == img3Res && img1Res == R.drawable.fsa -> {
                        balance += bet * 100
                        updateBalance()
                        Toast.makeText(this, "Vous avez gagné ! Vous avez multiplié votre mise par 100.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Aucun gain
                        Toast.makeText(this, "Vous avez perdu ! Essayez encore.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
