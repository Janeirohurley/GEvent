package com.janeirohurley.gevent.data.repository

import com.janeirohurley.gevent.data.api.AuthApiService
import com.janeirohurley.gevent.data.api.RetrofitClient
import com.janeirohurley.gevent.data.model.AuthResponse
import com.janeirohurley.gevent.data.model.LoginRequest
import com.janeirohurley.gevent.data.model.RegisterRequest
import com.janeirohurley.gevent.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Repository pour l'authentification
 */
class AuthRepository {
    private val authApiService: AuthApiService = RetrofitClient.retrofit.create(AuthApiService::class.java)

    /**
     * Connexion de l'utilisateur
     */
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(username, password)
                val response = authApiService.login(loginRequest)
                Result.success(response)
            } catch (e: UnknownHostException) {
                Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
            } catch (e: SocketTimeoutException) {
                Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
            } catch (e: IOException) {
                Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
            } catch (e: com.google.gson.JsonSyntaxException) {
                Result.failure(Exception("Erreur: Format de réponse invalide du serveur. Vérifiez que le backend renvoie {token, user}."))
            } catch (e: Exception) {
                Result.failure(Exception("Erreur: ${e.message ?: "Identifiants incorrects"}"))
            }
        }
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null,
        phoneNumber: String? = null
    ): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("AUTH_DEBUG", "========== REGISTER REQUEST ==========")
                val registerRequest = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )
                android.util.Log.d("AUTH_DEBUG", "Request: $registerRequest")
                
                val response = authApiService.register(registerRequest)
                
                android.util.Log.d("AUTH_DEBUG", "Response received: $response")
                android.util.Log.d("AUTH_DEBUG", "Token: ${response.token}")
                android.util.Log.d("AUTH_DEBUG", "User: ${response.user}")
                android.util.Log.d("AUTH_DEBUG", "======================================")
                
                Result.success(response)
            } catch (e: retrofit2.HttpException) {
                val errorBody = try {
                    e.response()?.errorBody()?.string()
                } catch (ex: Exception) {
                    "Unable to read error body"
                }
                android.util.Log.e("AUTH_ERROR", "HTTP ${e.code()}: $errorBody", e)
                android.util.Log.e("AUTH_ERROR", "Full exception: ", e)
                Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
            } catch (e: UnknownHostException) {
                android.util.Log.e("AUTH_ERROR", "UnknownHostException", e)
                Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
            } catch (e: SocketTimeoutException) {
                android.util.Log.e("AUTH_ERROR", "SocketTimeoutException", e)
                Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
            } catch (e: IOException) {
                android.util.Log.e("AUTH_ERROR", "IOException: ${e.message}", e)
                Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
            } catch (e: com.google.gson.JsonSyntaxException) {
                android.util.Log.e("AUTH_ERROR", "JsonSyntaxException: ${e.message}", e)
                android.util.Log.e("AUTH_ERROR", "Cause: ${e.cause}")
                Result.failure(Exception("Erreur de format JSON: ${e.message}"))
            } catch (e: Exception) {
                android.util.Log.e("AUTH_ERROR", "Exception: ${e.message}", e)
                android.util.Log.e("AUTH_ERROR", "Exception type: ${e.javaClass.name}")
                Result.failure(Exception("Erreur: ${e.message ?: "Impossible de créer le compte"}"))
            }
        }
    }

    /**
     * Récupérer le profil de l'utilisateur
     * Note: Le token est automatiquement ajouté par l'intercepteur de RetrofitClient
     */
    suspend fun getUserProfile(): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val profile = authApiService.getUserProfile()
                Result.success(profile)
            } catch (e: UnknownHostException) {
                Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
            } catch (e: SocketTimeoutException) {
                Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
            } catch (e: IOException) {
                Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
            } catch (e: Exception) {
                Result.failure(Exception("Erreur: ${e.message ?: "Impossible de récupérer le profil"}"))
            }
        }
    }

    /**
     * Déconnexion de l'utilisateur
     * Note: Le token est automatiquement ajouté par l'intercepteur de RetrofitClient
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                authApiService.logout()
                Result.success(Unit)
            } catch (e: Exception) {
                // Même en cas d'erreur réseau, on considère la déconnexion locale réussie
                Result.success(Unit)
            }
        }
    }
}
