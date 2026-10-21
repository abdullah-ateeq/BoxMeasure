package com.example.boxmeasure.domain.math

import java.math.BigDecimal
import java.math.MathContext

object BoxCalculator {

    fun calculateVolume(
        length: BigDecimal,
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        return length.multiply(width, mathContext).multiply(height, mathContext)
    }

    fun calculateSurfaceArea(
        length: BigDecimal,
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        val lw = calculateFaceAreaLW(length, width, mathContext)
        val lh = calculateFaceAreaLH(length, height, mathContext)
        val wh = calculateFaceAreaWH(width, height, mathContext)
        val sum = lw.add(lh, mathContext).add(wh, mathContext)
        return MathConstants.TWO.multiply(sum, mathContext)
    }

    fun calculateLateralSurfaceArea(
        length: BigDecimal,
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // LSA = 2H(L + W)
        val lengthPlusWidth = length.add(width, mathContext)
        val twoH = MathConstants.TWO.multiply(height, mathContext)
        return twoH.multiply(lengthPlusWidth, mathContext)
    }

    fun calculateFaceAreaLW(
        length: BigDecimal,
        width: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        return length.multiply(width, mathContext)
    }

    fun calculateFaceAreaLH(
        length: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        return length.multiply(height, mathContext)
    }

    fun calculateFaceAreaWH(
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        return width.multiply(height, mathContext)
    }

    fun calculateSpaceDiagonal(
        length: BigDecimal,
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // d = √(L² + W² + H²)
        val l2 = length.multiply(length, mathContext)
        val w2 = width.multiply(width, mathContext)
        val h2 = height.multiply(height, mathContext)
        val sumOfSquares = l2.add(w2, mathContext).add(h2, mathContext)
        return BigDecimalMath.sqrt(sumOfSquares, mathContext)
    }

    fun calculateBasePerimeter(
        length: BigDecimal,
        width: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // P = 2(L + W)
        val sum = length.add(width, mathContext)
        return MathConstants.TWO.multiply(sum, mathContext)
    }

    fun calculateTotalEdgeLength(
        length: BigDecimal,
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // E = 4(L + W + H)
        val sum = length.add(width, mathContext).add(height, mathContext)
        return MathConstants.FOUR.multiply(sum, mathContext)
    }

    fun calculateMissingLength(
        volume: BigDecimal,
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // L = V / (W × H)
        val baseArea = width.multiply(height, mathContext)
        if (baseArea.compareTo(BigDecimal.ZERO) == 0) {
            throw ArithmeticException("Division by zero in missing length calculation")
        }
        return volume.divide(baseArea, mathContext)
    }

    fun calculateMissingWidth(
        volume: BigDecimal,
        length: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // W = V / (L × H)
        val baseArea = length.multiply(height, mathContext)
        if (baseArea.compareTo(BigDecimal.ZERO) == 0) {
            throw ArithmeticException("Division by zero in missing width calculation")
        }
        return volume.divide(baseArea, mathContext)
    }

    fun calculateMissingHeight(
        volume: BigDecimal,
        length: BigDecimal,
        width: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // H = V / (L × W)
        val baseArea = length.multiply(width, mathContext)
        if (baseArea.compareTo(BigDecimal.ZERO) == 0) {
            throw ArithmeticException("Division by zero in missing height calculation")
        }
        return volume.divide(baseArea, mathContext)
    }
}
