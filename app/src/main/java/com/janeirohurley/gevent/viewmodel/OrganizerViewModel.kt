package com.janeirohurley.gevent.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janeirohurley.gevent.data.model.*
import com.janeirohurley.gevent.data.repository.OrganizerRepository
import com.janeirohurley.gevent.ui.screen.TicketValidationResult
import com.janeirohurley.gevent.ui.screen.TicketInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour gérer les événements de l'organisateur
 */
class OrganizerViewModel(
    private val repository: OrganizerRepository = OrganizerRepository()
) : ViewModel() {

    // État des événements
    private val _events = MutableStateFlow<List<OrganizerEvent>>(emptyList())
    val events: StateFlow<List<OrganizerEvent>> = _events.asStateFlow()

    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // État d'erreur
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // État de succès pour les opérations
    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    // État du ticket validé
    private val _validatedTicket = MutableStateFlow<Ticket?>(null)
    val validatedTicket: StateFlow<Ticket?> = _validatedTicket.asStateFlow()

    // Statistiques de l'événement sélectionné
    private val _eventStats = MutableStateFlow<EventStats?>(null)
    val eventStats: StateFlow<EventStats?> = _eventStats.asStateFlow()

    // Catégories
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Résultat du scan QR
    private val _scanResult = MutableStateFlow<TicketValidationResult?>(null)
    val scanResult: StateFlow<TicketValidationResult?> = _scanResult.asStateFlow()

    /**
     * Charger tous les événements de l'organisateur
     */
    fun loadMyEvents() {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== LOAD MY EVENTS ==========")
                _isLoading.value = true
                _error.value = null

                repository.getMyOrganizedEvents().fold(
                    onSuccess = { eventList ->
                        Log.d("ORGANIZER_VM", "Events loaded successfully: ${eventList.size} events")
                        _events.value = eventList
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error loading events: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur inconnue"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "====================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in loadMyEvents", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Créer un nouvel événement
     */
    fun createEvent(request: CreateEventRequest) {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== CREATE EVENT ==========")
                _isLoading.value = true
                _error.value = null
                _operationSuccess.value = null

                repository.createEvent(request).fold(
                    onSuccess = { event ->
                        Log.d("ORGANIZER_VM", "Event created successfully: ${event.id}")
                        _operationSuccess.value = "Événement créé avec succès"
                        _isLoading.value = false
                        loadMyEvents()
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error creating event: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur de création"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "==================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in createEvent", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Créer un événement avec image
     */
    fun createEventWithImage(request: CreateEventRequest, imageUri: android.net.Uri, context: android.content.Context) {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== CREATE EVENT WITH IMAGE ==========")
                _isLoading.value = true
                _error.value = null
                _operationSuccess.value = null

                repository.createEventWithImage(request, imageUri, context).fold(
                    onSuccess = { event ->
                        Log.d("ORGANIZER_VM", "Event created with image successfully: ${event.id}")
                        _operationSuccess.value = "Événement créé avec succès"
                        _isLoading.value = false
                        loadMyEvents()
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error creating event with image: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur de création"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "=============================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in createEventWithImage", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Charger les catégories
     */
    fun loadCategories() {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== LOAD CATEGORIES ==========")
                _isLoading.value = true
                _error.value = null

                repository.getCategories().fold(
                    onSuccess = { categoryList ->
                        Log.d("ORGANIZER_VM", "Categories loaded successfully: ${categoryList.size} categories")
                        _categories.value = categoryList
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error loading categories: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur de chargement des catégories"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "====================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in loadCategories", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Mettre à jour un événement
     */
    fun updateEvent(eventId: String, request: UpdateEventRequest) {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== UPDATE EVENT ==========")
                _isLoading.value = true
                _error.value = null
                _operationSuccess.value = null

                repository.updateEvent(eventId, request).fold(
                    onSuccess = { event ->
                        Log.d("ORGANIZER_VM", "Event updated successfully: ${event.id}")
                        _operationSuccess.value = "Événement mis à jour"
                        _isLoading.value = false
                        loadMyEvents()
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error updating event: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur de mise à jour"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "==================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in updateEvent", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Annuler un événement
     */
    fun cancelEvent(eventId: String) {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== CANCEL EVENT ==========")
                _isLoading.value = true
                _error.value = null
                _operationSuccess.value = null

                repository.cancelEvent(eventId).fold(
                    onSuccess = { event ->
                        Log.d("ORGANIZER_VM", "Event cancelled successfully: ${event.id}")
                        _operationSuccess.value = "Événement annulé"
                        _isLoading.value = false
                        loadMyEvents()
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error cancelling event: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur d'annulation"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "==================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in cancelEvent", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Marquer un événement comme terminé
     */
    fun completeEvent(eventId: String) {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== COMPLETE EVENT ==========")
                _isLoading.value = true
                _error.value = null
                _operationSuccess.value = null

                repository.completeEvent(eventId).fold(
                    onSuccess = { event ->
                        Log.d("ORGANIZER_VM", "Event completed successfully: ${event.id}")
                        _operationSuccess.value = "Événement marqué comme terminé"
                        _isLoading.value = false
                        loadMyEvents()
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error completing event: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur de complétion"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "====================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in completeEvent", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtenir les statistiques d'un événement
     */
    fun loadEventStats(eventId: String) {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== LOAD EVENT STATS ==========")
                _isLoading.value = true
                _error.value = null

                repository.getEventStats(eventId).fold(
                    onSuccess = { stats ->
                        Log.d("ORGANIZER_VM", "Stats loaded successfully")
                        _eventStats.value = stats
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error loading stats: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur de chargement des stats"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "======================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in loadEventStats", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Valider un ticket via QR code
     */
    fun validateTicket(qrCode: String) {
        viewModelScope.launch {
            try {
                Log.d("ORGANIZER_VM", "========== VALIDATE TICKET ==========")
                _isLoading.value = true
                _error.value = null
                _validatedTicket.value = null
                _scanResult.value = null

                repository.validateTicket(qrCode).fold(
                    onSuccess = { response ->
                        Log.d("ORGANIZER_VM", "Validation response: valid=${response.valid}, message=${response.message}")
                        
                        if (response.valid && response.ticket != null) {
                            _validatedTicket.value = response.ticket
                            _scanResult.value = TicketValidationResult(
                                isValid = true,
                                message = response.message,
                                ticketInfo = TicketInfo(
                                    eventTitle = response.ticket.event.title,
                                    participantName = response.ticket.holderName,
                                    ticketType = response.ticket.seat ?: "Standard"
                                )
                            )
                            _operationSuccess.value = "Ticket validé avec succès"
                        } else {
                            _scanResult.value = TicketValidationResult(
                                isValid = false,
                                message = response.message
                            )
                        }
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        Log.e("ORGANIZER_VM", "Error validating ticket: ${exception.message}", exception)
                        _scanResult.value = TicketValidationResult(
                            isValid = false,
                            message = exception.message ?: "Ticket invalide ou expiré"
                        )
                        _error.value = exception.message ?: "Erreur de validation"
                        _isLoading.value = false
                    }
                )
                Log.d("ORGANIZER_VM", "=====================================")
            } catch (e: Exception) {
                Log.e("ORGANIZER_VM", "Unexpected error in validateTicket", e)
                _scanResult.value = TicketValidationResult(
                    isValid = false,
                    message = "Erreur lors de la validation du ticket"
                )
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Réinitialiser le message de succès
     */
    fun clearOperationSuccess() {
        _operationSuccess.value = null
    }

    /**
     * Réinitialiser l'erreur
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Réinitialiser le ticket validé
     */
    fun clearValidatedTicket() {
        _validatedTicket.value = null
    }

    /**
     * Réinitialiser le résultat du scan
     */
    fun clearScanResult() {
        _scanResult.value = null
    }
}