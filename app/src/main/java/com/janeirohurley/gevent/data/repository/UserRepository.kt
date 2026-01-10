package com.janeirohurley.gevent.data.repository

import com.janeirohurley.gevent.data.api.*
import com.janeirohurley.gevent.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Repository pour gérer le profil utilisateur
 * Note: L'authentification (login/register) est gérée par AuthRepository
 */
class UserRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    /**
     * Récupérer le profil utilisateur
     */
    suspend fun getProfile(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val user = apiService.getProfile()
            Result.success(user)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Profil non trouvé"}"))
        }
    }

    /**
     * Mettre à jour le profil utilisateur
     */
    suspend fun updateProfile(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val updatedUser = apiService.updateProfile(user)
            Result.success(updatedUser)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Échec de la mise à jour"}"))
        }
    }

    /**
     * Changer le mot de passe
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = ChangePasswordRequest(currentPassword, newPassword)
            apiService.changePassword(request)
            Result.success(Unit)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Mot de passe incorrect"}"))
        }
    }
}
