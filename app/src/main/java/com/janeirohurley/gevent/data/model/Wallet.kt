package com.janeirohurley.gevent.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modèle pour le solde du wallet
 */
data class WalletBalance(
    @SerializedName("balance")
    val balance: String,
    
    @SerializedName("currency")
    val currency: String = "Fbu"
)

/**
 * Modèle pour une transaction
 */
data class Transaction(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("transaction_type")
    val transactionType: String, // "deposit", "purchase", "refund", "withdrawal"
    
    @SerializedName("amount")
    val amount: String,
    
    @SerializedName("balance_before")
    val balanceBefore: String,
    
    @SerializedName("balance_after")
    val balanceAfter: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("created_at")
    val createdAt: String
)

/**
 * Requête pour déposer de l'argent
 */
data class DepositRequest(
    @SerializedName("amount")
    val amount: String,
    
    @SerializedName("payment_method")
    val paymentMethod: String = "mobile_money"
)
