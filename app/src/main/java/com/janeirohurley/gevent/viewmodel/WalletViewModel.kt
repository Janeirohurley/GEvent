package com.janeirohurley.gevent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janeirohurley.gevent.data.model.Transaction
import com.janeirohurley.gevent.data.model.WalletBalance
import com.janeirohurley.gevent.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel(
    private val repository: WalletRepository = WalletRepository()
) : ViewModel() {
    
    private val _balance = MutableStateFlow<WalletBalance?>(null)
    val balance: StateFlow<WalletBalance?> = _balance.asStateFlow()
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()
    
    fun loadBalance() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getBalance().fold(
                onSuccess = { balance ->
                    _balance.value = balance
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
            )
        }
    }
    
    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getTransactions().fold(
                onSuccess = { transactions ->
                    _transactions.value = transactions
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
            )
        }
    }
    
    fun deposit(amount: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deposit(amount).fold(
                onSuccess = { transaction ->
                    _operationSuccess.value = "Dépôt effectué avec succès"
                    _isLoading.value = false
                    loadBalance()
                    loadTransactions()
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
            )
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccess() {
        _operationSuccess.value = null
    }
}
