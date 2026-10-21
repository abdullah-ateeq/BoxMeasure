package com.example.boxmeasure.domain.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object MathConstants {
    val MATH_CONTEXT: MathContext = MathContext(50, RoundingMode.HALF_UP)
    val TWO: BigDecimal = BigDecimal("2")
    val FOUR: BigDecimal = BigDecimal("4")
    val HALF: BigDecimal = BigDecimal("0.5")
}
