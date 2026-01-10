package com.janeirohurley.gevent.data.repository

import android.util.Log
import com.janeirohurley.gevent.data.api.ApiService
import com.janeirohurley.gevent.data.api.BookTicketRequest
import com.janeirohurley.gevent.data.api.CancelTicketRequest
import com.janeirohurley.gevent.data.api.RetrofitClient
import com.janeirohurley.gevent.data.model.BookingResponse
import com.janeirohurley.gevent.data.model.Ticket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Repository pour gérer les tickets avec gestion d'erreur réseau
 */
class TicketRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    /**
     * Récupérer tous les tickets de l'utilisateur
     */
    suspend fun getMyTickets(): Result<List<Ticket>> = withContext(Dispatchers.IO) {
        try {
            Log.d("TICKET_DEBUG", "========== GET MY TICKETS ==========")
            Log.d("TICKET_DEBUG", "Fetching tickets from API...")

            val tickets = apiService.getMyTickets()

            Log.d("TICKET_DEBUG", "Tickets received: ${tickets.size}")
            Log.d("TICKET_DEBUG", "Tickets: $tickets")
            Log.d("TICKET_DEBUG", "====================================")

            Result.success(tickets)
        } catch (e: UnknownHostException) {
            Log.e("TICKET_ERROR", "UnknownHostException", e)
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Log.e("TICKET_ERROR", "SocketTimeoutException", e)
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Log.e("TICKET_ERROR", "IOException: ${e.message}", e)
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("TICKET_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("TICKET_ERROR", "Exception: ${e.message}", e)
            Log.e("TICKET_ERROR", "Stack trace:", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }

    /**
     * Récupérer un ticket par ID
     */
    suspend fun getTicketById(ticketId: String): Result<Ticket> = withContext(Dispatchers.IO) {
        try {
            val ticket = apiService.getTicketById(ticketId)
            Result.success(ticket)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Ticket non trouvé"}"))
        }
    }

    /**
     * Réserver un ticket
     */
    suspend fun bookTicket(
        eventId: String,
        quantity: Int = 1,
        paymentMethod: String = "cash",
        seatNumber: String? = null
    ): Result<BookingResponse> = withContext(Dispatchers.IO) {
        try {
            val request = BookTicketRequest(eventId, quantity, paymentMethod, seatNumber)

            // DEBUG: Afficher les données envoyées
            Log.d("BOOKING_DEBUG", "========== BOOKING REQUEST ==========")
            Log.d("BOOKING_DEBUG", "Event ID: $eventId")
            Log.d("BOOKING_DEBUG", "Quantity: $quantity")
            Log.d("BOOKING_DEBUG", "Payment Method: $paymentMethod")
            Log.d("BOOKING_DEBUG", "Seat Number: $seatNumber")
            Log.d("BOOKING_DEBUG", "Request Object: $request")
            Log.d("BOOKING_DEBUG", "=====================================")

            val bookingResponse = apiService.bookTicket(request)

            // DEBUG: Afficher la réponse
            Log.d("BOOKING_DEBUG", "========== BOOKING RESPONSE ==========")
            Log.d("BOOKING_DEBUG", "Response: $bookingResponse")
            Log.d("BOOKING_DEBUG", "======================================")

            Result.success(bookingResponse)
        } catch (e: UnknownHostException) {
            Log.e("BOOKING_ERROR", "UnknownHostException", e)
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Log.e("BOOKING_ERROR", "SocketTimeoutException", e)
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Log.e("BOOKING_ERROR", "IOException: ${e.message}", e)
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: retrofit2.HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                "Unable to read error body"
            }
            Log.e("BOOKING_ERROR", "HTTP ${e.code()}: $errorBody", e)
            Result.failure(Exception("Erreur HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("BOOKING_ERROR", "Exception: ${e.message}", e)
            Result.failure(Exception("Erreur: ${e.message ?: "Échec de la réservation"}"))
        }
    }

    /**
     * Annuler un ticket
     */
    suspend fun cancelTicket(
        ticketId: String,
        reason: String,
        comment: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = CancelTicketRequest(reason, comment)
            apiService.cancelTicket(ticketId, request)
            Result.success(Unit)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Erreur réseau: Impossible de se connecter au serveur"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Erreur réseau: Délai d'attente dépassé"))
        } catch (e: IOException) {
            Result.failure(Exception("Erreur réseau: ${e.message ?: "Problème de connexion"}"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Impossible d'annuler le ticket"}"))
        }
    }
}
