package com.janeirohurley.gevent.data.repository

import com.janeirohurley.gevent.data.api.ApiService
import com.janeirohurley.gevent.data.api.RetrofitClient
import com.janeirohurley.gevent.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Repository pour gérer les événements avec gestion d'erreur réseau
 */
class EventRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    /**
     * Récupérer tous les événements
     */
    suspend fun getEvents(
        page: Int = 1,
        limit: Int = 20,
        category: String? = null,
        search: String? = null
    ): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val events = apiService.getEvents(page, limit, category, search)
            Result.success(events)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Récupérer un événement par ID
     */
    suspend fun getEventById(eventId: String): Result<Event> = withContext(Dispatchers.IO) {
        try {
            val event = apiService.getEventById(eventId)
            Result.success(event)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Récupérer les événements populaires
     */
    suspend fun getPopularEvents(
        limit: Int = 10,
        category: String? = null,
        search: String? = null
    ): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val events = apiService.getPopularEvents(limit, category, search)
            Result.success(events)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Récupérer les événements à venir
     */
    suspend fun getUpcomingEvents(
        limit: Int = 10,
        category: String? = null,
        search: String? = null
    ): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val events = apiService.getUpcomingEvents(limit, category, search)
            Result.success(events)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Rechercher des événements
     */
    suspend fun searchEvents(query: String, page: Int = 1): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val events = apiService.searchEvents(query, page)
            Result.success(events)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Récupérer les favoris
     * Retourne la liste des événements extraits des objets Favorite
     */
    suspend fun getFavorites(): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val favorites = apiService.getFavorites()
            // Extraire les événements de la structure Favorite
            val events = favorites.map { it.event }
            Result.success(events)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Toggle favori (Ajouter ou retirer)
     * Le backend gère automatiquement l'ajout ou le retrait
     */
    private suspend fun toggleFavorite(eventId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Convertir l'ID String en Int pour le backend
            val eventIdInt = eventId.toIntOrNull() ?: throw IllegalArgumentException("ID invalide: $eventId")
            apiService.toggleFavorite(mapOf("event_id" to eventIdInt))
            Result.success(Unit)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Ajouter aux favoris
     */
    suspend fun addToFavorites(eventId: String): Result<Unit> = toggleFavorite(eventId)

    /**
     * Retirer des favoris
     */
    suspend fun removeFromFavorites(eventId: String): Result<Unit> = toggleFavorite(eventId)

    /**
     * Récupérer toutes les catégories
     */
    suspend fun getCategories(): Result<List<com.janeirohurley.gevent.data.model.Category>> = withContext(Dispatchers.IO) {
        try {
            val categories = apiService.getCategories()
            Result.success(categories)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }
}
