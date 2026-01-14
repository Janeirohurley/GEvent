package com.janeirohurley.gevent.utils

/**
 * Utilitaires pour la validation et le nettoyage des données
 */
object ValidationUtils {
    
    /**
     * Nettoyer le username : supprimer espaces et caractères spéciaux
     * Garde seulement les lettres, chiffres et underscores
     */
    fun cleanUsername(username: String): String {
        val cleaned = username.trim()
            .replace("\\s+".toRegex(), "") // Supprimer tous les espaces
            .replace("[^a-zA-Z0-9_]".toRegex(), "") // Garder seulement lettres, chiffres et underscore
        
        // Si vide après nettoyage, retourner un username par défaut
        return if (cleaned.isEmpty()) "user${System.currentTimeMillis()}" else cleaned
    }
    
    /**
     * Valider un email
     */
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(emailPattern)
    }
    
    /**
     * Valider un mot de passe (minimum 6 caractères)
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    /**
     * Valider un numéro de téléphone burundais
     */
    fun isValidBurundiPhone(phone: String): Boolean {
        // Format: 79123456 ou 61123456 (8 chiffres)
        val phonePattern = "^[67][0-9]{7}$".toRegex()
        return phone.matches(phonePattern)
    }
}
