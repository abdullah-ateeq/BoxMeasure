package com.example.boxmeasure.domain.math

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

object BigDecimalMath {

    /**
     * High-precision square root calculation using Newton-Raphson method.
     * Guaranteed to use BigDecimal arithmetic without Double/Float conversions.
     */
    fun sqrt(
        value: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        val signum = value.signum()
        if (signum < 0) {
            throw IllegalArgumentException("Square root of negative number is undefined: $value")
        }
        if (signum == 0) {
            return BigDecimal.ZERO
        }
        if (value.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ONE
        }

        // Add 10 guard digits for intermediate Newton-Raphson iterations
        val workingPrecision = mathContext.precision + 10
        val workingContext = MathContext(workingPrecision, RoundingMode.HALF_UP)

        // Compute an initial approximation without using Double
        // For value = m * 10^e, sqrt(value) ≈ sqrt(m) * 10^(e/2)
        var x0 = getInitialApproximation(value, workingContext)

        val half = MathConstants.HALF
        val maxIterations = 200
        var iterations = 0
        var lastDifference: BigDecimal? = null

        while (iterations < maxIterations) {
            // x_{k+1} = 0.5 * (x_k + value / x_k)
            val quotient = value.divide(x0, workingContext)
            val sum = x0.add(quotient, workingContext)
            val nextX = sum.multiply(half, workingContext)

            val difference = nextX.subtract(x0).abs()
            if (difference.compareTo(BigDecimal.ZERO) == 0) {
                x0 = nextX
                break
            }

            // If the difference hasn't decreased or is extremely small (within precision threshold)
            val threshold = BigDecimal.ONE.movePointLeft(mathContext.precision + 2)
            if (difference.compareTo(threshold) <= 0) {
                x0 = nextX
                break
            }

            if (lastDifference != null && difference >= lastDifference) {
                // Converged to oscillation within precision
                x0 = nextX
                break
            }

            lastDifference = difference
            x0 = nextX
            iterations++
        }

        return x0.round(mathContext)
    }

    /**
     * Determines a reliable initial guess without floating-point conversion.
     */
    private fun getInitialApproximation(value: BigDecimal, context: MathContext): BigDecimal {
        // Shift decimal point so value is an integer or find magnitude
        val unscaled = value.unscaledValue().abs()
        val scale = value.scale()
        // Approximate magnitude in base 10:
        val numDigits = unscaled.toString().length - scale
        val shift = numDigits / 2
        return if (shift > 0) {
            BigDecimal.TEN.pow(shift, context)
        } else if (shift < 0) {
            BigDecimal.ONE.movePointRight(shift) // moves point left since shift is negative
        } else {
            BigDecimal.ONE
        }
    }
}
