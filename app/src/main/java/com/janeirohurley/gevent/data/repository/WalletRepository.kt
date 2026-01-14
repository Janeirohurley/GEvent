package com.janeirohurley.gevent.data.repository

import com.janeirohurley.gevent.data.api.ApiService
import com.janeirohurley.gevent.data.api.RetrofitClient
import com.janeirohurley.gevent.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    
    suspend fun getBalance(): Result<WalletBalance> = withContext(Dispatchers.IO) {
        try {
            val balance = apiService.getWalletBalance()
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }
    
    suspend fun getTransactions(): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val transactions = apiService.getTransactions()
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }
    
    suspend fun deposit(amount: String, paymentMethod: String = "mobile_money"): Result<Transaction> = withContext(Dispatchers.IO) {
        try {
            val request = DepositRequest(amount, paymentMethod)
            val transaction = apiService.depositMoney(request)
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur de dépôt"}"))
        }
    }
}
