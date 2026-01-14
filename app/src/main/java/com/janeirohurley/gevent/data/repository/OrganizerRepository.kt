package com.janeirohurley.gevent.data.repository

import android.util.Log
import com.janeirohurley.gevent.data.api.ApiService
import com.janeirohurley.gevent.data.api.RetrofitClient
import com.janeirohurley.gevent.data.api.RetrofitClient.apiService
import com.janeirohurley.gevent.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Repository pour la gestion des événements par l'organisateur
 */
class OrganizerRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    /**
     * Extraire le message d'erreur du JSON
     */
    private fun extractErrorMessage(errorBody: String?): String {
        return try {
            if (errorBody != null) {
                val jsonObject = JSONObject(errorBody)
                jsonObject.optString("message", "Erreur inconnue")
            } else {
                "Erreur inconnue"
            }
        } catch (e: Exception) {
            "Erreur de validation"
        }
    }

    /**
     * Récupérer tous les événements de l'organisateur
     */
    suspend fun getMyOrganizedEvents(): Result<List<OrganizerEvent>> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== GET MY EVENTS ==========")
            val events = apiService.getMyOrganizedEvents()
            Log.d("ORGANIZER_DEBUG", "Events received: ${events.size}")
            Log.d("ORGANIZER_DEBUG", "===================================")
            Result.success(events)
        } catch (e: UnknownHostException) {
            Log.e("ORGANIZER_ERROR", "UnknownHostException", e)
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Log.e("ORGANIZER_ERROR", "SocketTimeoutException", e)
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Log.e("ORGANIZER_ERROR", "IOException: ${e.message}", e)
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Créer un nouvel événement
     */
    suspend fun createEvent(request: CreateEventRequest): Result<OrganizerEvent> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== CREATE EVENT ==========")
            Log.d("ORGANIZER_DEBUG", "Request: $request")
            val event = apiService.createEvent(request)
            Log.d("ORGANIZER_DEBUG", "Event created: ${event.id}")
            Log.d("ORGANIZER_DEBUG", "==================================")
            Result.success(event)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de création"}"))
        }
    }

    /**
     * Créer un nouvel événement avec image
     */
    suspend fun createEventWithImage(
        request: CreateEventRequest,
        imageUri: android.net.Uri?,
        context: android.content.Context
    ): Result<OrganizerEvent> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== CREATE EVENT WITH IMAGE ===========")
            Log.d("ORGANIZER_DEBUG", "Request: $request")
            
            val titleBody = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), request.title)
            val descriptionBody = request.description?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val categoryIdBody = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), request.categoryId.toString())
            val locationBody = request.location?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val latitudeBody = request.latitude?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
            val longitudeBody = request.longitude?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
            val dateBody = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), request.date)
            val endDateBody = request.endDate?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val durationBody = request.duration?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val isFreeBody = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), request.isFree.toString())
            val priceBody = request.price?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val tvaRateBody = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), request.tvaRate ?: "18.00")
            val totalCapacityBody = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), request.totalCapacity.toString())
            val organizerNameBody = request.organizerName?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            
            val imagePart = imageUri?.let {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes() ?: return@let null
                inputStream?.close()
                
                val requestBody = okhttp3.RequestBody.create(
                    (contentResolver.getType(it) ?: "image/*").toMediaTypeOrNull(),
                    bytes
                )
                
                okhttp3.MultipartBody.Part.createFormData(
                    "image_url",
                    "event_image.jpg",
                    requestBody
                )
            }
            
            val event = apiService.createEventWithImage(
                titleBody, descriptionBody, categoryIdBody, locationBody,
                latitudeBody, longitudeBody, dateBody, endDateBody, durationBody,
                isFreeBody, priceBody, tvaRateBody, totalCapacityBody, organizerNameBody, imagePart
            )
            
            Log.d("ORGANIZER_DEBUG", "Event created with image: ${event.id}")
            Log.d("ORGANIZER_DEBUG", "=============================================")
            Result.success(event)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de création"}"))
        }
    }

    /**
     * Uploader l'image d'un événement
     */
    suspend fun uploadEventImage(eventId: String, imageUri: android.net.Uri, context: android.content.Context): Result<OrganizerEvent> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== UPLOAD EVENT IMAGE ==========")
            Log.d("ORGANIZER_DEBUG", "Event ID: $eventId")

            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes() ?: throw Exception("Impossible de lire l'image")
            inputStream?.close()

            val requestBody = okhttp3.RequestBody.create(
                (contentResolver.getType(imageUri) ?: "image/*").toMediaTypeOrNull(),
                bytes
            )

            val imagePart = okhttp3.MultipartBody.Part.createFormData(
                "image",
                "event_image.jpg",
                requestBody
            )

            val event = apiService.uploadEventImage(eventId, imagePart)
            Log.d("ORGANIZER_DEBUG", "Image uploaded for event: ${event.id}")
            Log.d("ORGANIZER_DEBUG", "========================================")
            Result.success(event)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur d'upload"}"))
        }
    }

    /**
     * Récupérer toutes les catégories
     */
    suspend fun getCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== GET CATEGORIES ==========")
            val categories = apiService.getCategories()
            Log.d("ORGANIZER_DEBUG", "Categories received: ${categories.size}")
            Log.d("ORGANIZER_DEBUG", "===================================")
            Result.success(categories)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de récupération des catégories"}"))
        }
    }

    /**
     * Mettre à jour un événement
     */
    suspend fun updateEvent(eventId: String, request: UpdateEventRequest): Result<OrganizerEvent> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== UPDATE EVENT ==========")
            Log.d("ORGANIZER_DEBUG", "Event ID: $eventId")
            Log.d("ORGANIZER_DEBUG", "Request: $request")
            val event = apiService.updateEvent(eventId, request)
            Log.d("ORGANIZER_DEBUG", "Event updated: ${event.id}")
            Log.d("ORGANIZER_DEBUG", "==================================")
            Result.success(event)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de mise à jour"}"))
        }
    }

    /**
     * Mettre à jour un événement avec image
     */
    suspend fun updateEventWithImage(
        eventId: String,
        title: String,
        description: String?,
        categoryId: Int?,
        location: String?,
        date: String?,
        endDate: String?,
        duration: String?,
        isFree: Boolean?,
        price: String?,
        tvaRate: String?,
        totalCapacity: Int?,
        imageUri: android.net.Uri,
        context: android.content.Context
    ): Result<OrganizerEvent> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== UPDATE EVENT WITH IMAGE ==========")
            Log.d("ORGANIZER_DEBUG", "Event ID: $eventId")
            
            val titleBody = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), title)
            val descriptionBody = description?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val categoryIdBody = categoryId?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
            val locationBody = location?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val dateBody = date?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val endDateBody = endDate?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val durationBody = duration?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val isFreeBody = isFree?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
            val priceBody = price?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val tvaRateBody = tvaRate?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
            val totalCapacityBody = totalCapacity?.let { okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
            
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes() ?: throw Exception("Impossible de lire l'image")
            inputStream?.close()
            
            val requestBody = okhttp3.RequestBody.create(
                (contentResolver.getType(imageUri) ?: "image/*").toMediaTypeOrNull(),
                bytes
            )
            
            val imagePart = okhttp3.MultipartBody.Part.createFormData(
                "image_url",
                "event_image.jpg",
                requestBody
            )
            
            val event = apiService.updateEventWithImage(
                eventId, titleBody, descriptionBody, categoryIdBody, locationBody,
                dateBody, endDateBody, durationBody, isFreeBody, priceBody, tvaRateBody,
                totalCapacityBody, imagePart
            )
            
            Log.d("ORGANIZER_DEBUG", "Event updated with image: ${event.id}")
            Log.d("ORGANIZER_DEBUG", "=============================================")
            Result.success(event)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de mise à jour"}"))
        }
    }

    /**
     * Supprimer un événement (soft delete)
     */
    suspend fun deleteEvent(eventId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== SOFT DELETE EVENT ==========")
            Log.d("ORGANIZER_DEBUG", "Event ID: $eventId")
            val response = apiService.softDeleteEvent(eventId)
            if (response.isSuccessful) {
                val message = response.body()?.get("message") ?: "Événement supprimé"
                Log.d("ORGANIZER_DEBUG", "Event soft deleted: $message")
                Log.d("ORGANIZER_DEBUG", "=======================================")
                Result.success(message)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = extractErrorMessage(errorBody)
                Log.e("ORGANIZER_ERROR", "HTTP ${response.code()}: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                null
            }
            val errorMessage = extractErrorMessage(errorBody)
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorMessage", e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de suppression"}"))
        }
    }

    /**
     * Annuler un événement et rembourser
     */
    suspend fun cancelEvent(eventId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== CANCEL EVENT ==========")
            Log.d("ORGANIZER_DEBUG", "Event ID: $eventId")
            val response = apiService.cancelEvent(eventId)
            if (response.isSuccessful) {
                val body = response.body()
                val message = body?.get("message") as? String ?: "Événement annulé avec succès"
                Log.d("ORGANIZER_DEBUG", "Event cancelled: $message")
                Log.d("ORGANIZER_DEBUG", "==================================")
                Result.success(message)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = extractErrorMessage(errorBody)
                Log.e("ORGANIZER_ERROR", "HTTP ${response.code()}: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                null
            }
            val errorMessage = extractErrorMessage(errorBody)
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorMessage", e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur d'annulation"}"))
        }
    }

    /**
     * Changer le statut d'un événement
     */
    suspend fun changeEventStatus(eventId: String, status: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== CHANGE EVENT STATUS ==========")
            Log.d("ORGANIZER_DEBUG", "Event ID: $eventId, New Status: $status")
            val event = apiService.changeEventStatus(eventId, mapOf("status" to status))
            Log.d("ORGANIZER_DEBUG", "Status changed successfully")
            Log.d("ORGANIZER_DEBUG", "=========================================")
            Result.success("Statut changé avec succès")
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                null
            }
            val errorMessage = extractErrorMessage(errorBody)
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorMessage", e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de changement de statut"}"))
        }
    }

    /**
     * Marquer un événement comme terminé
     */
    suspend fun completeEvent(eventId: String): Result<OrganizerEvent> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== COMPLETE EVENT ==========")
            Log.d("ORGANIZER_DEBUG", "Event ID: $eventId")
            val event = apiService.completeEvent(eventId)
            Log.d("ORGANIZER_DEBUG", "Event completed: ${event.id}")
            Log.d("ORGANIZER_DEBUG", "====================================")
            Result.success(event)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de complétion"}"))
        }
    }

    /**
     * Obtenir les statistiques d'un événement
     */
    suspend fun getEventStats(eventId: String): Result<EventStats> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== GET EVENT STATS ==========")
            Log.d("ORGANIZER_DEBUG", "Event ID: $eventId")
            val stats = apiService.getEventStats(eventId)
            Log.d("ORGANIZER_DEBUG", "Stats received")
            Log.d("ORGANIZER_DEBUG", "=====================================")
            Result.success(stats)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de récupération des stats"}"))
        }
    }

    /**
     * Valider un ticket via QR code
     */
    suspend fun validateTicket(qrCode: String): Result<TicketValidationResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d("ORGANIZER_DEBUG", "========== VALIDATE TICKET ==========")
            Log.d("ORGANIZER_DEBUG", "QR Code: $qrCode")
            val response = apiService.validateTicket(mapOf("qr_data" to qrCode))
            Log.d("ORGANIZER_DEBUG", "Validation response: valid=${response.valid}, message=${response.message}")
            Log.d("ORGANIZER_DEBUG", "=====================================")
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                null
            }
            val errorMessage = extractErrorMessage(errorBody)
            Log.e("ORGANIZER_ERROR", "HTTP ${e.code()}: $errorMessage", e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("ORGANIZER_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de validation"}"))
        }
    }
}