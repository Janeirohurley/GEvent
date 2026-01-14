package com.janeirohurley.gevent.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modèle de données pour une catégorie
 */
data class Category(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("icon")
    val icon: String? = null
)

/**
 * Modèle de données pour un événement provenant de l'API
 */
data class Event(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("date")
    val date: String,

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("is_free")
    val isFree: Boolean = true,

    @SerializedName("price")
    val price: String? = null,

    @SerializedName("creator")
    val creator: Creator? = null,

    @SerializedName("attendees")
    val attendees: List<Participant>? = null,

    @SerializedName("total_tickets")
    val totalTickets: Int = 0,

    @SerializedName("available_tickets")
    val availableTickets: Int = 0,

    @SerializedName("is_favorite")
    val isFavorite: Boolean = false,

    @SerializedName("carrency")
    val currency: String

)

data class Creator(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)

data class Participant(
    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val name: String,

    @SerializedName("profile_image")
    val avatarUrl: String? = null
)

/**
 * Modèle de données pour un favori
 * Structure: { id, event: {...}, created_at }
 */
data class Favorite(
    @SerializedName("id")
    val id: Int,

    @SerializedName("event")
    val event: Event,

    @SerializedName("created_at")
    val createdAt: String
)
