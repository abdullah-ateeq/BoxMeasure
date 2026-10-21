package com.example.boxmeasure.domain.math

import java.math.BigDecimal
import java.math.RoundingMode

object BoxMeasureFormatter {

    fun format(
        value: BigDecimal,
        decimals: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP,
        keepTrailingZeros: Boolean = false
    ): String {
        // Round to target decimals
        val rounded = value.setScale(decimals, roundingMode)

        return if (keepTrailingZeros) {
            rounded.toPlainString()
        } else {
            // Strip unnecessary trailing zeros and ensure clean plain string
            var plain = rounded.stripTrailingZeros().toPlainString()
            if (plain == "-0" || plain == "+0") {
                plain = "0"
            }
            plain
        }
    }

    fun formatWithUnit(
        value: BigDecimal,
        unitSymbol: String,
        decimals: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP,
        keepTrailingZeros: Boolean = false
    ): String {
        val formattedNumber = format(value, decimals, roundingMode, keepTrailingZeros)
        return "$formattedNumber $unitSymbol"
    }
}
