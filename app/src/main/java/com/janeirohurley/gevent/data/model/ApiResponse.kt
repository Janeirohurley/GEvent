package com.janeirohurley.gevent.data.model

import com.google.gson.annotations.SerializedName

/**
 * Réponse API générique
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("error")
    val error: String? = null
)

/**
 * Liste paginée
 */
data class PaginatedResponse<T>(
    @SerializedName("data")
    val data: List<T>,

    @SerializedName("page")
    val page: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_items")
    val totalItems: Int
)

/**
 * Réponse de booking (Order)
 */
data class BookingResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("order_number")
    val orderNumber: String,

    @SerializedName("event")
    val event: Event,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("total_ttc")
    val totalTtc: String,

    @SerializedName("payment_method")
    val paymentMethod: String,

    @SerializedName("payment_status")
    val paymentStatus: String,

    @SerializedName("tickets")
    val tickets: List<Ticket>,

    @SerializedName("created_at")
    val createdAt: String
) {
    // Propriété helper pour récupérer le premier ticket (compatibilité)
    // Retourne null si aucun ticket au lieu de lancer une exception
    val ticket: Ticket?
        get() = tickets.firstOrNull()
}
