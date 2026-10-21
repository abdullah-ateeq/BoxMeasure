package com.example.boxmeasure

import com.example.boxmeasure.domain.math.BigDecimalMath
import com.example.boxmeasure.domain.math.BoxCalculationStepsGenerator
import com.example.boxmeasure.domain.math.BoxMeasureFormatter
import com.example.boxmeasure.domain.math.MathConstants
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.VolumeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalMathAndFormattingTest {

    // 30. BigDecimal multiplication
    @Test
    fun testBigDecimalMultiplication() {
        val a = BigDecimal("123456789.987654321")
        val b = BigDecimal("987654321.123456789")
        val result = a.multiply(b, MathConstants.MATH_CONTEXT)
        assertEquals(0, BigDecimal("121932632103337905.6620941931").compareTo(result.setScale(10, RoundingMode.HALF_UP)))
    }

    // 31. BigDecimal division
    @Test
    fun testBigDecimalDivision() {
        val a = BigDecimal("10")
        val b = BigDecimal("3")
        val quotient = a.divide(b, MathConstants.MATH_CONTEXT)
        assertTrue(quotient.toPlainString().startsWith("3.333333333333333333333333333333"))
    }

    // 32. BigDecimal square root
    @Test
    fun testBigDecimalSquareRoot() {
        // sqrt(141) = 11.8743420870379... which rounds to 11.874342 at 6 decimals
        val sqrt141 = BigDecimalMath.sqrt(BigDecimal("141"), MathConstants.MATH_CONTEXT)
        val formatted = BoxMeasureFormatter.format(sqrt141, decimals = 6, roundingMode = RoundingMode.HALF_UP)
        assertTrue("Expected 11.874342 but was $formatted", formatted == "11.874342" || formatted == "11.874343")

        // sqrt(4) = 2
        val sqrt4 = BigDecimalMath.sqrt(BigDecimal("4"), MathConstants.MATH_CONTEXT)
        assertEquals(0, BigDecimal("2").compareTo(sqrt4))

        // sqrt(0) = 0
        val sqrt0 = BigDecimalMath.sqrt(BigDecimal("0"), MathConstants.MATH_CONTEXT)
        assertEquals(0, BigDecimal.ZERO.compareTo(sqrt0))

        // sqrt(0.25) = 0.5
        val sqrt025 = BigDecimalMath.sqrt(BigDecimal("0.25"), MathConstants.MATH_CONTEXT)
        assertEquals(0, BigDecimal("0.5").compareTo(sqrt025))

        // Negative value throws IllegalArgumentException
        try {
            BigDecimalMath.sqrt(BigDecimal("-4"), MathConstants.MATH_CONTEXT)
            assertFalse("Expected exception on negative sqrt", true)
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("negative") == true)
        }
    }

    // 33. Result formatting
    @Test
    fun testResultFormatting() {
        val value = BigDecimal("11.874342887")
        val formatted = BoxMeasureFormatter.format(value, decimals = 6, roundingMode = RoundingMode.HALF_UP)
        assertEquals("11.874343", formatted)

        val formattedWithUnit = BoxMeasureFormatter.formatWithUnit(
            value = BigDecimal("200"),
            unitSymbol = "cm³",
            decimals = 6,
            roundingMode = RoundingMode.HALF_UP
        )
        assertEquals("200 cm³", formattedWithUnit)
    }

    // 34. Trailing-zero removal
    @Test
    fun testTrailingZeroRemoval() {
        val value = BigDecimal("200.000000")
        val formatted = BoxMeasureFormatter.format(value, decimals = 6, roundingMode = RoundingMode.HALF_UP, keepTrailingZeros = false)
        assertEquals("200", formatted)

        val valueDec = BigDecimal("10.500000")
        val formattedDec = BoxMeasureFormatter.format(valueDec, decimals = 6, roundingMode = RoundingMode.HALF_UP, keepTrailingZeros = false)
        assertEquals("10.5", formattedDec)
    }

    // 35. HALF_UP (2.5 -> 3, 2.4 -> 2)
    @Test
    fun testRoundingHalfUp() {
        val num = BigDecimal("2.5")
        val res = BoxMeasureFormatter.format(num, decimals = 0, roundingMode = RoundingMode.HALF_UP)
        assertEquals("3", res)

        val num2 = BigDecimal("2.4")
        val res2 = BoxMeasureFormatter.format(num2, decimals = 0, roundingMode = RoundingMode.HALF_UP)
        assertEquals("2", res2)
    }

    // 36. HALF_DOWN (2.5 -> 2, 2.51 -> 3)
    @Test
    fun testRoundingHalfDown() {
        val num = BigDecimal("2.5")
        val res = BoxMeasureFormatter.format(num, decimals = 0, roundingMode = RoundingMode.HALF_DOWN)
        assertEquals("2", res)

        val num2 = BigDecimal("2.51")
        val res2 = BoxMeasureFormatter.format(num2, decimals = 0, roundingMode = RoundingMode.HALF_DOWN)
        assertEquals("3", res2)
    }

    // 37. HALF_EVEN (Banker's rounding: 2.5 -> 2, 3.5 -> 4)
    @Test
    fun testRoundingHalfEven() {
        val num1 = BigDecimal("2.5")
        val res1 = BoxMeasureFormatter.format(num1, decimals = 0, roundingMode = RoundingMode.HALF_EVEN)
        assertEquals("2", res1)

        val num2 = BigDecimal("3.5")
        val res2 = BoxMeasureFormatter.format(num2, decimals = 0, roundingMode = RoundingMode.HALF_EVEN)
        assertEquals("4", res2)
    }

    // 38. UP (Always away from zero: 2.1 -> 3, -2.1 -> -3)
    @Test
    fun testRoundingUp() {
        val num = BigDecimal("2.1")
        val res = BoxMeasureFormatter.format(num, decimals = 0, roundingMode = RoundingMode.UP)
        assertEquals("3", res)

        val numNeg = BigDecimal("-2.1")
        val resNeg = BoxMeasureFormatter.format(numNeg, decimals = 0, roundingMode = RoundingMode.UP)
        assertEquals("-3", resNeg)
    }

    // 39. DOWN (Truncate towards zero: 2.9 -> 2, -2.9 -> -2)
    @Test
    fun testRoundingDown() {
        val num = BigDecimal("2.9")
        val res = BoxMeasureFormatter.format(num, decimals = 0, roundingMode = RoundingMode.DOWN)
        assertEquals("2", res)

        val numNeg = BigDecimal("-2.9")
        val resNeg = BoxMeasureFormatter.format(numNeg, decimals = 0, roundingMode = RoundingMode.DOWN)
        assertEquals("-2", resNeg)
    }

    // 40. CEILING (Towards positive infinity: -2.9 -> -2, 2.1 -> 3)
    @Test
    fun testRoundingCeiling() {
        val num = BigDecimal("2.1")
        val res = BoxMeasureFormatter.format(num, decimals = 0, roundingMode = RoundingMode.CEILING)
        assertEquals("3", res)

        val numNeg = BigDecimal("-2.9")
        val resNeg = BoxMeasureFormatter.format(numNeg, decimals = 0, roundingMode = RoundingMode.CEILING)
        assertEquals("-2", resNeg)
    }

    // 41. FLOOR (Towards negative infinity: 2.9 -> 2, -2.1 -> -3)
    @Test
    fun testRoundingFloor() {
        val num = BigDecimal("2.9")
        val res = BoxMeasureFormatter.format(num, decimals = 0, roundingMode = RoundingMode.FLOOR)
        assertEquals("2", res)

        val numNeg = BigDecimal("-2.1")
        val resNeg = BoxMeasureFormatter.format(numNeg, decimals = 0, roundingMode = RoundingMode.FLOOR)
        assertEquals("-3", resNeg)
    }

    // 42. Calculation steps
    @Test
    fun testCalculationSteps() {
        val steps = BoxCalculationStepsGenerator.generateBoxSteps(
            l = BigDecimal("10"),
            w = BigDecimal("5"),
            h = BigDecimal("4"),
            volume = BigDecimal("200"),
            surfaceArea = BigDecimal("220"),
            lateralSurfaceArea = BigDecimal("120"),
            lw = BigDecimal("50"),
            lh = BigDecimal("40"),
            wh = BigDecimal("20"),
            basePerimeter = BigDecimal("30"),
            spaceDiagonal = BigDecimal("11.874343"),
            totalEdgeLength = BigDecimal("76"),
            lengthUnit = LengthUnit.CENTIMETER,
            areaUnit = AreaUnit.SQUARE_CENTIMETER,
            volumeUnit = VolumeUnit.CUBIC_CENTIMETER
        )

        assertTrue(steps.any { it.label == "Volume" && it.expression.contains("V = L × W × H") && it.result == "200 cm³" })
        assertTrue(steps.any { it.label == "Total Surface Area" && it.expression.contains("SA = 2(LW + LH + WH)") && it.result == "220 cm²" })
        assertTrue(steps.any { it.label == "Space Diagonal" && it.expression.contains("d = √(L² + W² + H²)") })
        assertTrue(steps.any { it.label == "Base Perimeter" && it.result == "30 cm" })
        assertTrue(steps.any { it.label == "Total Edge Length" && it.result == "76 cm" })
    }
}
