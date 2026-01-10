package com.janeirohurley.gevent.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utilitaire pour formater les dates de manière lisible
 */
object DateFormatter {

    /**
     * Formate une date ISO 8601 en format lisible
     * Ex: "2024-03-15T14:30:00Z" -> "15 Mars 2024"
     */
    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString

            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("fr", "FR"))
            outputFormat.format(date)
        } catch (e: Exception) {
            // Si le format est juste "yyyy-MM-dd"
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString) ?: return dateString

                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("fr", "FR"))
                outputFormat.format(date)
            } catch (e: Exception) {
                dateString
            }
        }
    }

    /**
     * Formate une date avec l'heure
     * Ex: "2024-03-15T14:30:00Z" -> "15 Mars 2024 à 14h30"
     */
    fun formatDateTime(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString

            val outputFormat = SimpleDateFormat("dd MMMM yyyy 'à' HH'h'mm", Locale("fr", "FR"))
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Formate une date en format court
     * Ex: "2024-03-15T14:30:00Z" -> "15 Mar"
     */
    fun formatShortDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString

            val outputFormat = SimpleDateFormat("dd MMM", Locale("fr", "FR"))
            outputFormat.format(date)
        } catch (e: Exception) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString) ?: return dateString

                val outputFormat = SimpleDateFormat("dd MMM", Locale("fr", "FR"))
                outputFormat.format(date)
            } catch (e: Exception) {
                dateString
            }
        }
    }

    /**
     * Formate seulement l'heure
     * Ex: "2024-03-15T14:30:00Z" -> "14h30"
     */
    fun formatTime(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString

            val outputFormat = SimpleDateFormat("HH'h'mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Retourne le jour de la semaine
     * Ex: "2024-03-15T14:30:00Z" -> "Vendredi"
     */
    fun getDayOfWeek(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return ""

            val outputFormat = SimpleDateFormat("EEEE", Locale("fr", "FR"))
            outputFormat.format(date).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Retourne le numéro du jour du mois
     * Ex: "2024-03-15T14:30:00Z" -> "15"
     */
    fun getDayNumber(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return ""

            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        } catch (e: Exception) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString) ?: return ""

                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.get(Calendar.DAY_OF_MONTH).toString()
            } catch (e: Exception) {
                ""
            }
        }
    }

    /**
     * Retourne le mois abrégé
     * Ex: "2024-03-15T14:30:00Z" -> "Mar"
     */
    fun getMonthShort(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return ""

            val outputFormat = SimpleDateFormat("MMM", Locale("fr", "FR"))
            outputFormat.format(date).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString) ?: return ""

                val outputFormat = SimpleDateFormat("MMM", Locale("fr", "FR"))
                outputFormat.format(date).replaceFirstChar { it.uppercase() }
            } catch (e: Exception) {
                ""
            }
        }
    }
}
