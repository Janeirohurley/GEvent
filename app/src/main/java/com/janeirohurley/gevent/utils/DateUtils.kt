package com.janeirohurley.gevent.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(date: Date, pattern: String = "dd MMM yyyy", locale: Locale = Locale.FRENCH): String {
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(date)
    }

    fun formatDateFromString(dateString: String, inputPattern: String = "yyyy-MM-dd'T'HH:mm:ss", outputPattern: String = "dd MMM yyyy", locale: Locale = Locale.FRENCH): String? {
        return try {
            val parser = SimpleDateFormat(inputPattern, locale)
            val date = parser.parse(dateString)
            if (date != null) formatDate(date, outputPattern, locale) else null
        } catch (e: Exception) {
            null
        }
    }
}


fun truncateByWords(text: String, maxWords: Int): String {
    val words = text.trim().split(Regex("\\s+"))
    return if (words.size <= maxWords) {
        text
    } else {
        words.take(maxWords).joinToString(" ") + "…"
    }
}



