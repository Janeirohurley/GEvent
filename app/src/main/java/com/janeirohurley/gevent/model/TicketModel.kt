package com.janeirohurley.gevent.model

data class TicketModel(
    val code: String,
    val eventTitle: String,
    val eventDate: String,
    val eventLocation: String,
    val holderName: String,
    val seat: String? = null,
    val price: String,
    val purchaseDate: String,
    val qrCode: String? = null // Peut contenir une URL ou un texte à encoder
)