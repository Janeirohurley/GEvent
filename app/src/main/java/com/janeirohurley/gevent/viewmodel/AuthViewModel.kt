package com.janeirohurley.gevent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janeirohurley.gevent.data.api.RetrofitClient
import com.janeirohurley.gevent.data.model.User
import com.janeirohurley.gevent.data.repository.AuthRepository
import com.janeirohurley.gevent.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour l'authentification
 */
class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // État d'authentification
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Token d'authentification
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    // Profil utilisateur
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    // Message d'erreur
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Message de succès
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // État de vérification initiale
    private val _isCheckingAuth = MutableStateFlow(true)
    val isCheckingAuth: StateFlow<Boolean> = _isCheckingAuth.asStateFlow()

    init {
        checkAuthStatus()
    }

    /**
     * Vérifier le statut d'authentification au démarrage
     */
    private fun checkAuthStatus() {
        viewModelScope.launch {
            _isCheckingAuth.value = true
            val savedToken = TokenManager.getToken()

            if (savedToken != null) {
                // Token existe, le définir dans RetrofitClient pour toutes les requêtes
                RetrofitClient.setAuthToken(savedToken)
                _authToken.value = savedToken

                // Essayer de récupérer le profil utilisateur pour vérifier le token
                val result = repository.getUserProfile()
                result.onSuccess { user ->
                    _userProfile.value = user
                    _isAuthenticated.value = true
                }.onFailure { exception ->
                    // En cas d'erreur réseau, on garde le token
                    // mais on n'authentifie pas complètement
                    if (exception.message?.contains("réseau", ignoreCase = true) == true ||
                        exception.message?.contains("connexion", ignoreCase = true) == true) {
                        _errorMessage.value = "Erreur réseau. Vos identifiants sont sauvegardés."
                        // On garde le token pour réessayer plus tard
                    } else {
                        // Token invalide, le supprimer
                        TokenManager.clearToken()
                        RetrofitClient.setAuthToken(null)
                        _authToken.value = null
                        _isAuthenticated.value = false
                    }
                }
            } else {
                _isAuthenticated.value = false
            }

            _isCheckingAuth.value = false
        }
    }

    /**
     * Connexion de l'utilisateur
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val result = repository.login(username, password)

            result.onSuccess { authResponse ->
                _authToken.value = authResponse.token
                _userProfile.value = authResponse.user
                _isAuthenticated.value = true
                _successMessage.value = "Connexion réussie!"

                // Sauvegarder le token de manière persistante
                TokenManager.saveToken(authResponse.token)
                RetrofitClient.setAuthToken(authResponse.token)
            }.onFailure { exception ->
                _errorMessage.value = exception.message
                _isAuthenticated.value = false
            }

            _isLoading.value = false
        }
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    fun register(
        username: String,
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null,
        phoneNumber: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val result = repository.register(
                username = username,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber
            )

            result.onSuccess { authResponse ->
                _authToken.value = authResponse.token
                _userProfile.value = authResponse.user
                _isAuthenticated.value = true
                _successMessage.value = "Compte créé avec succès!"

                // Sauvegarder le token de manière persistante
                TokenManager.saveToken(authResponse.token)
                RetrofitClient.setAuthToken(authResponse.token)
            }.onFailure { exception ->
                _errorMessage.value = exception.message
                _isAuthenticated.value = false
            }

            _isLoading.value = false
        }
    }

    /**
     * Déconnexion de l'utilisateur
     */
    fun logout() {
        viewModelScope.launch {
            // Appeler l'API de déconnexion si un token existe
            if (_authToken.value != null) {
                repository.logout()
            }

            // Réinitialiser l'état
            _authToken.value = null
            _userProfile.value = null
            _isAuthenticated.value = false
            _successMessage.value = "Déconnexion réussie"

            // Supprimer le token de manière persistante
            TokenManager.clearToken()
            RetrofitClient.setAuthToken(null)
        }
    }

    /**
     * Effacer les messages d'erreur
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Effacer les messages de succès
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
