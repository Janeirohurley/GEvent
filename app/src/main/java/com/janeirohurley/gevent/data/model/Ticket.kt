package com.janeirohurley.gevent.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modèle de données pour un ticket
 */
data class Ticket(
    @SerializedName("id")
    val id: String,

    @SerializedName("code")
    val code: String,

    @SerializedName("event")
    val event: Event,

    @SerializedName("holder_name")
    val holderName: String,

    @SerializedName("seat")
    val seat: String? = null,

    @SerializedName("price")
    val price: String,

    @SerializedName("purchase_date")
    val purchaseDate: String,

    @SerializedName("qr_code")
    val qrCode: String? = null,

    @SerializedName("status")
    val status: String = "active" // active, cancelled, used
)

/**
 * Modèle de données pour la réponse de validation de ticket
 */
data class TicketValidationResponse(
    @SerializedName("valid")
    val valid: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("ticket")
    val ticket: Ticket? = null
)
