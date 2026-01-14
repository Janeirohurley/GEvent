package com.janeirohurley.gevent.data.api

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.janeirohurley.gevent.data.model.Event
import com.janeirohurley.gevent.data.model.Creator
import com.janeirohurley.gevent.data.model.Participant
import java.lang.reflect.Type

/**
 * Désérialiseur personnalisé pour Event qui gère le champ category
 * Le backend renvoie category comme objet: {"id": 1, "name": "Concert", ...}
 * On le convertit en String en extrayant juste le "name"
 */
class EventDeserializer : JsonDeserializer<Event> {

    companion object {
        // Type token pour la liste de participants (défini une seule fois)
        private val PARTICIPANT_LIST_TYPE: Type = object : TypeToken<List<Participant>>() {}.type
    }

    // Fonctions helper pour gérer les valeurs null de manière sûre
    private fun JsonElement?.toStringOrNull(): String? {
        return if (this == null || this.isJsonNull) null else this.asString
    }

    private fun JsonElement?.toIntOrDefault(default: Int): Int {
        return if (this == null || this.isJsonNull) default else this.asInt
    }

    private fun JsonElement?.toBooleanOrDefault(default: Boolean): Boolean {
        return if (this == null || this.isJsonNull) default else this.asBoolean
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Event {
        val jsonObject = json!!.asJsonObject

        // Extraire le nom de la catégorie si c'est un objet
        val categoryValue = jsonObject.get("category")
        val categoryName = when {
            categoryValue == null || categoryValue.isJsonNull -> null
            categoryValue.isJsonPrimitive -> categoryValue.asString
            categoryValue.isJsonObject -> {
                val nameElement = categoryValue.asJsonObject.get("name")
                if (nameElement == null || nameElement.isJsonNull) null else nameElement.asString
            }
            else -> null
        }

        // Gérer le champ creator (peut être null)
        val creatorElement = jsonObject.get("creator")
        val creator = if (creatorElement != null && !creatorElement.isJsonNull) {
            context!!.deserialize<Creator>(creatorElement, Creator::class.java)
        } else null

        // Gérer is_favorite vs is_favorited (le backend peut renvoyer les deux)
        val isFavoriteValue = when {
            jsonObject.has("is_favorite") -> jsonObject.get("is_favorite").toBooleanOrDefault(false)
            jsonObject.has("is_favorited") -> jsonObject.get("is_favorited").toBooleanOrDefault(false)
            else -> false
        }

        // Gérer total_tickets vs total_capacity
        val totalTickets = when {
            jsonObject.has("total_tickets") -> jsonObject.get("total_tickets").toIntOrDefault(0)
            jsonObject.has("total_capacity") -> jsonObject.get("total_capacity").toIntOrDefault(0)
            else -> 0
        }

        // Gérer available_tickets vs available_seats
        val availableTickets = when {
            jsonObject.has("available_tickets") -> jsonObject.get("available_tickets").toIntOrDefault(0)
            jsonObject.has("available_seats") -> jsonObject.get("available_seats").toIntOrDefault(0)
            else -> 0
        }

        // Gérer organizer_name pour creator si creator est null
        val finalCreator = creator ?: run {
            val organizerName = jsonObject.get("organizer_name").toStringOrNull()
            val organizerImage = jsonObject.get("organizer_image").toStringOrNull()
            if (organizerName != null) {
                Creator(
                    id = "0",
                    name = organizerName,
                    avatarUrl = organizerImage
                )
            } else null
        }

        // Gérer attendees pour participants si participants est null
        val finalParticipants = try {
            when {
                jsonObject.has("participants") && !jsonObject.get("participants").isJsonNull -> {
                    val participantsElement = jsonObject.get("participants")
                    if (participantsElement.isJsonArray) {
                        context!!.deserialize<List<Participant>>(participantsElement, PARTICIPANT_LIST_TYPE)
                    } else null
                }
                jsonObject.has("attendees") && !jsonObject.get("attendees").isJsonNull -> {
                    val attendeesElement = jsonObject.get("attendees")
                    if (attendeesElement.isJsonArray) {
                        context!!.deserialize<List<Participant>>(attendeesElement, PARTICIPANT_LIST_TYPE)
                    } else null
                }
                else -> null
            }
        } catch (e: Exception) {
            // En cas d'erreur de désérialisation, retourner null au lieu de crasher
            null
        }



        // Désérialiser les autres champs normalement
        return Event(
            id = jsonObject.get("id")?.asString ?: "0",
            title = jsonObject.get("title")?.asString ?: "",
            description = jsonObject.get("description").toStringOrNull(),
            date = jsonObject.get("date")?.asString ?: "",
            location = jsonObject.get("location").toStringOrNull(),
            imageUrl = jsonObject.get("image_url").toStringOrNull(),
            category = categoryName,
            isFree = jsonObject.get("is_free").toBooleanOrDefault(true),
            price = jsonObject.get("price").toStringOrNull(),
            creator = finalCreator,
            attendees = finalParticipants,
            totalTickets = totalTickets,
            availableTickets = availableTickets,
            isFavorite = isFavoriteValue,
            currency =jsonObject.get("currency").toString(),
        )
    }
}
