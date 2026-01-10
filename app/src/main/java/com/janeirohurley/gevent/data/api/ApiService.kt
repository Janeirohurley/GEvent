package com.janeirohurley.gevent.data.api

import com.janeirohurley.gevent.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface API pour tous les endpoints de l'application
 */
interface ApiService {

    // ==================== AUTHENTIFICATION ====================

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // ==================== ÉVÉNEMENTS ====================

    /**
     * Récupérer tous les événements (avec pagination optionnelle)
     */
    @GET("events/")
    suspend fun getEvents(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): List<Event>

    /**
     * Récupérer un événement par ID
     */
    @GET("events/{id}/")
    suspend fun getEventById(@Path("id") eventId: String): Event

    /**
     * Récupérer les événements populaires
     */
    @GET("events/popular/")
    suspend fun getPopularEvents(@Query("limit") limit: Int = 10): List<Event>

    /**
     * Récupérer les événements à venir
     */
    @GET("events/upcoming/")
    suspend fun getUpcomingEvents(@Query("limit") limit: Int = 10): List<Event>

    /**
     * Rechercher des événements
     */
    @GET("events/search/")
    suspend fun searchEvents(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): List<Event>

    // ==================== FAVORIS ====================

    /**
     * Récupérer les événements favoris de l'utilisateur
     * Retourne une liste de Favorite qui contiennent l'événement imbriqué
     */
    @GET("favorites/")
    suspend fun getFavorites(): List<Favorite>

    /**
     * Toggle (Ajouter/Retirer) un événement aux/des favoris
     * Le backend gère automatiquement si c'est un ajout ou un retrait
     */
    @POST("favorites/toggle/")
    suspend fun toggleFavorite(@Body eventId: Map<String, Int>)

    // ==================== TICKETS ====================

    /**
     * Récupérer tous les tickets de l'utilisateur
     */
    @GET("tickets/")
    suspend fun getMyTickets(): List<Ticket>

    /**
     * Récupérer un ticket par ID
     */
    @GET("tickets/{id}/")
    suspend fun getTicketById(@Path("id") ticketId: String): Ticket

    /**
     * Réserver un ticket pour un événement
     */
    @POST("orders/")
    suspend fun bookTicket(
        @Body request: BookTicketRequest
    ): BookingResponse

    /**
     * Annuler un ticket
     */
    @POST("tickets/{id}/cancel/")
    suspend fun cancelTicket(
        @Path("id") ticketId: String,
        @Body request: CancelTicketRequest
    )

    // ==================== PROFIL UTILISATEUR ====================

    /**
     * Récupérer le profil de l'utilisateur connecté
     */
    @GET("auth/user/")
    suspend fun getProfile(): User

    /**
     * Mettre à jour le profil
     */
    @PUT("auth/user/")
    suspend fun updateProfile(@Body request: User): User

    /**
     * Changer le mot de passe
     */
    @POST("auth/change-password/")
    suspend fun changePassword(@Body request: ChangePasswordRequest)

    // ==================== CATÉGORIES ====================

    /**
     * Récupérer toutes les catégories
     */
    @GET("categories/")
    suspend fun getCategories(): List<Category>
}

// ==================== REQUÊTES ====================

data class BookTicketRequest(
    val event_id: String,
    val quantity: Int = 1,
    val payment_method: String = "cash",
    val seatNumber: String? = null
)

data class CancelTicketRequest(
    val reason: String,
    val comment: String? = null
)

data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val favoriteCategories: List<String>? = null,
    val language: String? = null
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
