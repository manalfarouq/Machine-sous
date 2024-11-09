package com.example.jeu

import android.content.Context
import android.widget.Toast

object ToastManager {
    private var toast: Toast? = null

    fun showToast(context: Context, message: String) {
        // Annule le toast précédent s'il est encore affiché
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}
