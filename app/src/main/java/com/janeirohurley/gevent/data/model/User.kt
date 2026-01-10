package com.janeirohurley.gevent.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modèle de données pour l'utilisateur (UserProfile)
 */
data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("first_name")
    val firstName: String?,

    @SerializedName("last_name")
    val lastName: String?,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("profile_image")
    val profileImage: String? = null,

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName("date_of_birth")
    val dateOfBirth: String? = null,

    @SerializedName("created_at")
    val createdAt: String
)

/**
 * Réponse de login/register
 */
data class AuthResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: User
)

/**
 * Requête de login
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Requête de register
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null
)
