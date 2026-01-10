package com.janeirohurley.gevent.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janeirohurley.gevent.data.model.BookingResponse
import com.janeirohurley.gevent.data.model.Ticket
import com.janeirohurley.gevent.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour gérer les tickets
 */
class TicketViewModel(
    private val repository: TicketRepository = TicketRepository()
) : ViewModel() {

    // État des tickets
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // État d'erreur
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // État de réservation
    private val _bookingSuccess = MutableStateFlow<BookingResponse?>(null)
    val bookingSuccess: StateFlow<BookingResponse?> = _bookingSuccess.asStateFlow()

    /**
     * Charger tous les tickets de l'utilisateur
     */
    fun loadMyTickets() {
        viewModelScope.launch {
            try {
                Log.d("TICKET_VM", "========== LOAD MY TICKETS ==========")
                _isLoading.value = true
                _error.value = null

                repository.getMyTickets().fold(
                    onSuccess = { ticketList ->
                        Log.d("TICKET_VM", "Tickets loaded successfully: ${ticketList.size} tickets")
                        _tickets.value = ticketList
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        Log.e("TICKET_VM", "Error loading tickets: ${exception.message}", exception)
                        _error.value = exception.message ?: "Erreur inconnue"
                        _isLoading.value = false
                    }
                )
                Log.d("TICKET_VM", "=====================================")
            } catch (e: Exception) {
                Log.e("TICKET_VM", "Unexpected error in loadMyTickets", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Réserver un ticket
     */
    fun bookTicket(
        eventId: String,
        quantity: Int = 1,
        paymentMethod: String = "cash",
        seatNumber: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _bookingSuccess.value = null

            repository.bookTicket(eventId, quantity, paymentMethod, seatNumber).fold(
                onSuccess = { booking ->
                    _bookingSuccess.value = booking
                    _isLoading.value = false
                    // Recharger les tickets
                    loadMyTickets()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur lors de la réservation"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Annuler un ticket
     */
    fun cancelTicket(
        ticketId: String,
        reason: String,
        comment: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.cancelTicket(ticketId, reason, comment).fold(
                onSuccess = {
                    _isLoading.value = false
                    // Recharger les tickets
                    loadMyTickets()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erreur lors de l'annulation"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Réinitialiser le succès de la réservation
     */
    fun clearBookingSuccess() {
        _bookingSuccess.value = null
    }

    /**
     * Réinitialiser l'erreur
     */
    fun clearError() {
        _error.value = null
    }
}
