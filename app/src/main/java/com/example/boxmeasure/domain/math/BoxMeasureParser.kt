package com.example.boxmeasure.domain.math

import java.math.BigDecimal

object BoxMeasureParser {
    const val MAX_DIGITS = 500

    fun parseNumber(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Empty input"))
        }

        // Count digit characters
        val digitCount = trimmed.count { it.isDigit() }
        if (digitCount > MAX_DIGITS) {
            return Result.failure(IllegalArgumentException("Number is too long. Maximum 500 digits."))
        }

        // Normalize leading / trailing decimal points:
        // Examples: ".5" -> "0.5", "+.5" -> "+0.5", "-.5" -> "-0.5", "10." -> "10.0"
        var normalized = trimmed
        if (normalized.startsWith(".")) {
            normalized = "0$normalized"
        } else if (normalized.startsWith("+.") || normalized.startsWith("-.")) {
            val sign = normalized[0]
            normalized = "$sign" + "0" + normalized.substring(1)
        }

        if (normalized.endsWith(".")) {
            normalized = "${normalized}0"
        }

        return try {
            val bigDecimal = BigDecimal(normalized)
            Result.success(bigDecimal)
        } catch (e: NumberFormatException) {
            Result.failure(IllegalArgumentException("Enter a valid number."))
        }
    }
}
