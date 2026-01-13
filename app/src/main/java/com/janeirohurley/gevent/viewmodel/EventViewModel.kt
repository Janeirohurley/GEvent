package com.janeirohurley.gevent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janeirohurley.gevent.data.model.Event
import com.janeirohurley.gevent.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour gérer les événements
 */
class EventViewModel(
    private val repository: EventRepository = EventRepository()
) : ViewModel() {

    // État des événements
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    // État des événements populaires
    private val _popularEvents = MutableStateFlow<List<Event>>(emptyList())
    val popularEvents: StateFlow<List<Event>> = _popularEvents.asStateFlow()

    // État des événements à venir
    private val _upcomingEvents = MutableStateFlow<List<Event>>(emptyList())
    val upcomingEvents: StateFlow<List<Event>> = _upcomingEvents.asStateFlow()

    // État des événements favoris
    private val _favoriteEvents = MutableStateFlow<List<Event>>(emptyList())
    val favoriteEvents: StateFlow<List<Event>> = _favoriteEvents.asStateFlow()

    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // État d'erreur
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // État d'un événement spécifique (pour les détails)
    private val _currentEvent = MutableStateFlow<Event?>(null)
    val currentEvent: StateFlow<Event?> = _currentEvent.asStateFlow()

    // Catégories
    private val _categories = MutableStateFlow<List<com.janeirohurley.gevent.data.model.Category>>(emptyList())
    val categories: StateFlow<List<com.janeirohurley.gevent.data.model.Category>> = _categories.asStateFlow()

    /**
     * Charger tous les événements
     */
    fun loadEvents(
        page: Int = 1,
        limit: Int = 20,
        category: String? = null,
        search: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getEvents(page, limit, category, search).fold(
                onSuccess = { eventList ->
                    _events.value = eventList
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur inconnue"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Charger les événements populaires
     */
    fun loadPopularEvents(limit: Int = 10) {
        viewModelScope.launch {
            repository.getPopularEvents(limit).fold(
                onSuccess = { eventList ->
                    _popularEvents.value = eventList
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur inconnue"
                }
            )
        }
    }

    /**
     * Charger les événements à venir
     */
    fun loadUpcomingEvents(limit: Int = 10) {
        viewModelScope.launch {
            repository.getUpcomingEvents(limit).fold(
                onSuccess = { eventList ->
                    _upcomingEvents.value = eventList
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur inconnue"
                }
            )
        }
    }

    /**
     * Rechercher des événements
     */
    fun searchEvents(query: String, page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.searchEvents(query, page).fold(
                onSuccess = { eventList ->
                    _events.value = eventList
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur inconnue"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Charger les événements favoris
     */
    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getFavorites().fold(
                onSuccess = { eventList ->
                    _favoriteEvents.value = eventList
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur inconnue"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Ajouter aux favoris
     */
    fun addToFavorites(eventId: String) {
        viewModelScope.launch {
            repository.addToFavorites(eventId).fold(
                onSuccess = {
                    // Mettre à jour les listes locales
                    _events.value = _events.value.map { event ->
                        if (event.id == eventId) event.copy(isFavorite = true) else event
                    }
                    _popularEvents.value = _popularEvents.value.map { event ->
                        if (event.id == eventId) event.copy(isFavorite = true) else event
                    }
                    _upcomingEvents.value = _upcomingEvents.value.map { event ->
                        if (event.id == eventId) event.copy(isFavorite = true) else event
                    }
                    // Recharger les favoris
                    loadFavorites()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur inconnue"
                }
            )
        }
    }

    /**
     * Retirer des favoris
     */
    fun removeFromFavorites(eventId: String) {
        print(eventId)
        viewModelScope.launch {
            repository.removeFromFavorites(eventId).fold(

                onSuccess = {

                    // Mettre à jour les listes locales
                    _events.value = _events.value.map { event ->
                        if (event.id == eventId) event.copy(isFavorite = false) else event
                    }
                    _popularEvents.value = _popularEvents.value.map { event ->
                        if (event.id == eventId) event.copy(isFavorite = false) else event
                    }
                    _upcomingEvents.value = _upcomingEvents.value.map { event ->
                        if (event.id == eventId) event.copy(isFavorite = false) else event
                    }
                    // Recharger les favoris
                    loadFavorites()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur inconnue"
                }
            )
        }
    }

    /**
     * Basculer l'état favori
     */
    fun toggleFavorite(eventId: String, currentState: Boolean) {
        if (currentState) {
            removeFromFavorites(eventId)
        } else {
            addToFavorites(eventId)
        }
    }

    /**
     * Charger un événement spécifique par ID
     */
    fun loadEventById(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // D'abord chercher dans les listes en cache
            val allEvents = _events.value + _popularEvents.value + _upcomingEvents.value + _favoriteEvents.value
            val cachedEvent = allEvents.find { it.id == eventId }

            if (cachedEvent != null) {
                _currentEvent.value = cachedEvent
                _isLoading.value = false
            } else {
                // Si pas trouvé en cache, charger depuis l'API
                repository.getEventById(eventId).fold(
                    onSuccess = { event ->
                        _currentEvent.value = event
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Événement non trouvé"
                        _isLoading.value = false
                    }
                )
            }
        }
    }

    /**
     * Réinitialiser l'erreur
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Charger les catégories
     */
    fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().fold(
                onSuccess = { categoryList ->
                    _categories.value = categoryList
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur de chargement des catégories"
                }
            )
        }
    }
}
