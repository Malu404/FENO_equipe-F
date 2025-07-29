package com.example.fenoapp

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeConverter {

    private val formatoConvencional = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    private val formatoISO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Converte "dd/MM/yyyy HH:mm" -> "yyyy-MM-dd HH:mm:ss"
    fun paraISO(input: String): String? {
        return try {
            val dataHora = LocalDateTime.parse(input, formatoConvencional)
            dataHora.format(formatoISO)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Converte "yyyy-MM-dd HH:mm:ss" -> "dd/MM/yyyy HH:mm"
    fun paraConvencional(input: String): String? {
        return try {
            val dataHora = LocalDateTime.parse(input, formatoISO)
            dataHora.format(formatoConvencional)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}