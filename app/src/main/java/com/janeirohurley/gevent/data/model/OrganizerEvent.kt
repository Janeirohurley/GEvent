package com.janeirohurley.gevent.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modèle pour les événements de l'organisateur
 * Contient des statistiques supplémentaires pour la gestion
 */
data class OrganizerEvent(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("category")
    val category: Category?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("date")
    val date: String,

    @SerializedName("end_date")
    val endDate: String?,

    @SerializedName("duration")
    val duration: String?,

    @SerializedName("is_free")
    val isFree: Boolean,

    @SerializedName("price")
    val price: String?,

    @SerializedName("tva_rate")
    val tvaRate: String?,

    @SerializedName("tva_amount")
    val tvaAmount: String?,

    @SerializedName("price_with_tva")
    val priceWithTva: String?,

    @SerializedName("currency")
    val currency: String = "Fbu",

    @SerializedName("total_capacity")
    val totalCapacity: Int,

    @SerializedName("available_seats")
    val availableSeats: Int,

    @SerializedName("organizer_name")
    val organizerName: String?,

    @SerializedName("organizer_image")
    val organizerImage: String?,

    @SerializedName("status")
    val status: String, // draft, published, ongoing, completed, cancelled

    @SerializedName("is_popular")
    val isPopular: Boolean = false,

    @SerializedName("rating")
    val rating: Float?,

    @SerializedName("total_reviews")
    val totalReviews: Int?,

    @SerializedName("attendee_count")
    val attendeeCount: Int,

    @SerializedName("images")
    val images: List<EventImage>?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
) {
    // Propriétés calculées
    val ticketsSold: Int
        get() = totalCapacity - availableSeats

    val percentageSold: Float
        get() = if (totalCapacity > 0) {
            (ticketsSold.toFloat() / totalCapacity.toFloat()) * 100f
        } else 0f

    val isActive: Boolean
        get() = status == "published" || status == "ongoing"

    val isUpcoming: Boolean
        get() = status == "published"

    val isOngoing: Boolean
        get() = status == "ongoing"

    val isCompleted: Boolean
        get() = status == "completed"

    val isCancelled: Boolean
        get() = status == "cancelled"
    
    val isDeleted: Boolean
        get() = status == "deleted"
}

/**
 * Modèle pour les images d'événement
 */
data class EventImage(
    @SerializedName("id")
    val id: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("order")
    val order: Int
)

/**
 * Requête pour créer un événement
 */
data class CreateEventRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("category_id")
    val categoryId: Int,

    @SerializedName("location")
    val location: String?,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("date")
    val date: String, // Format: yyyy-MM-dd'T'HH:mm:ss

    @SerializedName("end_date")
    val endDate: String?,

    @SerializedName("duration")
    val duration: String?,

    @SerializedName("is_free")
    val isFree: Boolean,

    @SerializedName("price")
    val price: String?,

    @SerializedName("tva_rate")
    val tvaRate: String?,

    @SerializedName("currency")
    val currency: String = "Fbu",

    @SerializedName("total_capacity")
    val totalCapacity: Int,

    @SerializedName("organizer_name")
    val organizerName: String?,

    @SerializedName("status")
    val status: String = "upcoming"
)

/**
 * Requête pour mettre à jour un événement
 */
data class UpdateEventRequest(
    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("category_id")
    val categoryId: Int?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("date")
    val date: String?,

    @SerializedName("end_date")
    val endDate: String?,

    @SerializedName("duration")
    val duration: String?,

    @SerializedName("is_free")
    val isFree: Boolean?,

    @SerializedName("price")
    val price: String?,

    @SerializedName("tva_rate")
    val tvaRate: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("total_capacity")
    val totalCapacity: Int?
)

/**
 * Statistiques d'un événement pour l'organisateur
 */
data class EventStats(
    @SerializedName("event_id")
    val eventId: String,

    @SerializedName("total_tickets")
    val totalTickets: Int,

    @SerializedName("tickets_sold")
    val ticketsSold: Int,

    @SerializedName("tickets_used")
    val ticketsUsed: Int,

    @SerializedName("tickets_cancelled")
    val ticketsCancelled: Int,

    @SerializedName("revenue")
    val revenue: String,

    @SerializedName("attendees")
    val attendees: List<Participant>?
)
