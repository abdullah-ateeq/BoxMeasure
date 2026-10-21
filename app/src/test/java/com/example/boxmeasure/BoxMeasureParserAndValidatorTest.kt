package com.example.boxmeasure

import com.example.boxmeasure.domain.math.BoxMeasureParser
import com.example.boxmeasure.domain.math.BoxMeasureValidator
import com.example.boxmeasure.domain.model.BoxMeasureError
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.SolveMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class BoxMeasureParserAndValidatorTest {

    // 15. Decimal dimensions
    @Test
    fun testDecimalDimensions() {
        val parsed = BoxMeasureParser.parseNumber("100.5").getOrThrow()
        assertEquals(0, BigDecimal("100.5").compareTo(parsed))
    }

    // 16. Leading decimal
    @Test
    fun testLeadingDecimal() {
        val parsed = BoxMeasureParser.parseNumber(".5").getOrThrow()
        assertEquals(0, BigDecimal("0.5").compareTo(parsed))
    }

    // 17. Trailing decimal
    @Test
    fun testTrailingDecimal() {
        val parsed = BoxMeasureParser.parseNumber("10.").getOrThrow()
        assertEquals(0, BigDecimal("10.0").compareTo(parsed))
    }

    // 18. Scientific notation
    @Test
    fun testScientificNotation() {
        val parsed1 = BoxMeasureParser.parseNumber("1E+5").getOrThrow()
        assertEquals(0, BigDecimal("100000").compareTo(parsed1))

        val parsed2 = BoxMeasureParser.parseNumber("1.5E-2").getOrThrow()
        assertEquals(0, BigDecimal("0.015").compareTo(parsed2))
    }

    // 19. Very small values
    @Test
    fun testVerySmallValues() {
        val l = BoxMeasureParser.parseNumber("0.000001").getOrThrow()
        val w = BoxMeasureParser.parseNumber("0.000002").getOrThrow()
        val h = BoxMeasureParser.parseNumber("0.000003").getOrThrow()

        val lRes = BoxMeasureValidator.validateDimension(l.toPlainString(), "Length")
        assertTrue(lRes.isSuccess)

        val vol = l.multiply(w).multiply(h)
        assertEquals(0, BigDecimal("0.000000000000000006").compareTo(vol))
    }

    // 20. Very large values
    @Test
    fun testVeryLargeValues() {
        val largeStr = "999999999999999999999999"
        val parsed = BoxMeasureParser.parseNumber(largeStr).getOrThrow()
        val validRes = BoxMeasureValidator.validateDimension(largeStr, "Length")
        assertTrue(validRes.isSuccess)
        assertEquals(largeStr, parsed.toPlainString())
    }

    // 21. Zero validation
    @Test
    fun testZeroValidation() {
        val result = BoxMeasureValidator.validateDimension("0", "Length")
        assertTrue(result.isFailure)
        assertEquals("Length must be greater than zero.", result.exceptionOrNull()?.message)
    }

    // 22. Negative validation
    @Test
    fun testNegativeValidation() {
        val result = BoxMeasureValidator.validateDimension("-5", "Width")
        assertTrue(result.isFailure)
        assertEquals("Width must be greater than zero.", result.exceptionOrNull()?.message)
    }

    // 23. Empty input
    @Test
    fun testEmptyInputValidation() {
        val result = BoxMeasureValidator.validateDimension("", "Height")
        assertTrue(result.isFailure)
        assertEquals("Height is required to calculate the complete box measurements.", result.exceptionOrNull()?.message)

        val modeRes = BoxMeasureValidator.validateForMode(
            solveMode = SolveMode.BOX_MEASUREMENTS,
            missingDimension = DimensionType.LENGTH,
            lengthInput = "10",
            widthInput = "5",
            heightInput = "",
            volumeInput = ""
        )
        assertTrue(modeRes.isFailure)
        assertEquals("Height is required to calculate the complete box measurements.", modeRes.exceptionOrNull()?.message)
    }

    // 24. Maximum input length (500 digits)
    @Test
    fun testMaxInputLength() {
        val str500 = "9".repeat(500)
        val parse500 = BoxMeasureParser.parseNumber(str500)
        assertTrue(parse500.isSuccess)

        val str501 = "9".repeat(501)
        val parse501 = BoxMeasureParser.parseNumber(str501)
        assertTrue(parse501.isFailure)
        assertEquals("Number is too long. Maximum 500 digits.", parse501.exceptionOrNull()?.message)

        val valid501 = BoxMeasureValidator.validateDimension(str501, "Length")
        assertTrue(valid501.isFailure)
        assertEquals("Number is too long. Maximum 500 digits.", valid501.exceptionOrNull()?.message)
    }

    @Test
    fun testInvalidNumbersRejected() {
        val invalidInputs = listOf("abc", "12..5", "1.2.3", "--")
        for (input in invalidInputs) {
            val result = BoxMeasureParser.parseNumber(input)
            assertTrue("Expected rejection for $input", result.isFailure)
            assertEquals("Enter a valid number.", result.exceptionOrNull()?.message)
        }
    }
}
