package com.janeirohurley.gevent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janeirohurley.gevent.data.model.User
import com.janeirohurley.gevent.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour gérer le profil utilisateur
 * Note: L'authentification (login/register) est gérée par AuthViewModel
 */
class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    // État de l'utilisateur connecté
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // État d'erreur
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Message de succès
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    /**
     * Charger le profil utilisateur
     */
    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getProfile().fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur lors du chargement du profil"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Mettre à jour le profil utilisateur
     */
    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null

            repository.updateProfile(updatedUser).fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _successMessage.value = "Profil mis à jour avec succès"
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur lors de la mise à jour"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Changer le mot de passe
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null

            repository.changePassword(currentPassword, newPassword).fold(
                onSuccess = {
                    _successMessage.value = "Mot de passe changé avec succès"
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur lors du changement de mot de passe"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Réinitialiser les messages d'erreur
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Réinitialiser les messages de succès
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
