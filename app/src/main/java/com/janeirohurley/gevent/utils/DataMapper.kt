package com.janeirohurley.gevent.utils

import android.util.Log
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.data.model.Event
import com.janeirohurley.gevent.data.model.Ticket
import com.janeirohurley.gevent.viewmodel.EventUiModel
import com.janeirohurley.gevent.viewmodel.TicketUiModel

import com.janeirohurley.gevent.utils.DateUtils

/**
 * Convertit les données API en modèles UI
 */
object DataMapper {

    /**
     * Convertir Event (API) en EventUiModel (UI)
     */
    fun Event.toUiModel(): EventUiModel {
        return EventUiModel(
            id = this.id,
            title = this.title,
            date = DateUtils.formatDateFromString(
                this.date,
                inputPattern = "yyyy-MM-dd'T'HH:mm:ss",
                outputPattern = "dd MMM yyyy à HH:mm"
            ) ?: this.date,
            // Si imageUrl est null, utiliser une image par défaut
            imageRes = this.imageUrl ?: R.drawable.logo,
            isFavorite = this.isFavorite,
            creatorImageRes = this.creator?.avatarUrl ?: R.drawable.logo,
            creatorName = this.creator?.name ?: "creator name",
            // Ne montrer que les avatars réels, pas de fallback si vide
            joinedAvatars = this.attendees?.mapNotNull { it.avatarUrl } ?: emptyList(),
            isFree = this.isFree,
            price = this.price,
            location = this.location,
            description = this.description,
            categorieName = this.category ?: "Non classé",
            currency = this.currency
        )
    }

    /**
     * Convertir une liste d'Event en liste d'EventUiModel
     */
    fun List<Event>.toUiModelList(): List<EventUiModel> {
        return this.map { it.toUiModel() }
    }

    /**
     * Convertir Ticket (API) en TicketUiModel (UI)
     */
    fun Ticket.toUiModel(): TicketUiModel {
        return TicketUiModel(
            id = this.id,
            code = this.code,
            eventTitle = this.event.title,
            eventDate = DateUtils.formatDateFromString(
                this.event.date,
                inputPattern = "yyyy-MM-dd'T'HH:mm:ss",
                outputPattern = "dd MMM yyyy à HH:mm"
            ) ?: this.event.date,
            eventLocation = this.event.location ?: "Non spécifié",
            eventImageRes = this.event.imageUrl ?: R.drawable.event_image,
            holderName = this.holderName,
            seat = this.seat,
            price = this.price,
            purchaseDate = DateUtils.formatDateFromString(
                this.purchaseDate,
                inputPattern = "yyyy-MM-dd'T'HH:mm:ss",
                outputPattern = "dd MMM yyyy à HH:mm"
            ) ?: this.purchaseDate,
            qrCode = this.qrCode,
            status = this.status
        )
    }

    /**
     * Convertir une liste de Ticket en liste de TicketUiModel
     */
    fun List<Ticket>.toTicketUiModelList(): List<TicketUiModel> {
        return try {
            Log.d("DATA_MAPPER", "Converting ${this.size} tickets to UI models")
            this.mapIndexed { index, ticket ->
                try {
                    Log.d("DATA_MAPPER", "Converting ticket $index: ${ticket.id}")
                    ticket.toUiModel()
                } catch (e: Exception) {
                    Log.e("DATA_MAPPER", "Error converting ticket $index (${ticket.id})", e)
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e("DATA_MAPPER", "Error in toTicketUiModelList", e)
            throw e
        }
    }
}



