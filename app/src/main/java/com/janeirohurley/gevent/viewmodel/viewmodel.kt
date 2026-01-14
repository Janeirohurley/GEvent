package com.janeirohurley.gevent.viewmodel;

data class EventUiModel(
    val id: String,
    val title: String,
    val date: String,
    val imageRes: Any, // Peut être Int (ressource locale) ou String (URL)
    val isFavorite: Boolean = false,
    val creatorImageRes: Any, // Peut être Int (ressource locale) ou String (URL)
    val creatorName: String,
    val joinedAvatars: List<Any>, // Peut contenir Int ou String
    val isFree: Boolean = true,
    val price: String? = null,
    val location: String? = null, // Localisation de l'événement
    val description: String? = null, // Description de l'événement
    val categorieName: String ,// Nom de la catégorie de l'événement
    val currency:String
)

data class TicketUiModel(
    val id: String,
    val code: String,
    val eventTitle: String,
    val eventDate: String,
    val eventLocation: String,
    val eventImageRes: Any, // Peut être Int (ressource locale) ou String (URL)
    val holderName: String,
    val seat: String?,
    val price: String,
    val purchaseDate: String,
    val qrCode: String?,
    val status: String // "confirmed", "cancelled", "used"
)

