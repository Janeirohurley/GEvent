package com.janeirohurley.gevent.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestionnaire pour la persistance du token d'authentification
 */
object TokenManager {
    private const val PREFS_NAME = "gevent_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"

    private lateinit var prefs: SharedPreferences

    /**
     * Initialiser le TokenManager avec le contexte de l'application
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Sauvegarder le token d'authentification
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
        println("💾 TokenManager: Token saved (length: ${token.length})")
    }

    /**
     * Récupérer le token d'authentification
     */
    fun getToken(): String? {
        val token = prefs.getString(KEY_AUTH_TOKEN, null)
        if (token != null) {
            println("🔓 TokenManager: Token retrieved (length: ${token.length})")
        } else {
            println("❌ TokenManager: No token found")
        }
        return token
    }

    /**
     * Supprimer le token d'authentification
     */
    fun clearToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
        println("🗑️ TokenManager: Token cleared")
    }

    /**
     * Vérifier si un token existe
     */
    fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}
