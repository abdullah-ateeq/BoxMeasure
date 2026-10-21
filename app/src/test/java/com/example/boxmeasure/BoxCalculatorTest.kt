package com.example.boxmeasure

import com.example.boxmeasure.domain.math.BoxCalculator
import com.example.boxmeasure.domain.math.BoxMeasureFormatter
import com.example.boxmeasure.domain.math.MathConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class BoxCalculatorTest {

    // 1. Volume
    @Test
    fun testVolumeCalculation() {
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val volume = BoxCalculator.calculateVolume(l, w, h)
        assertEquals(0, BigDecimal("200").compareTo(volume))
    }

    // 2. Total surface area
    @Test
    fun testTotalSurfaceArea() {
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val sa = BoxCalculator.calculateSurfaceArea(l, w, h)
        assertEquals(0, BigDecimal("220").compareTo(sa))
    }

    // 3. Lateral surface area
    @Test
    fun testLateralSurfaceArea() {
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val lsa = BoxCalculator.calculateLateralSurfaceArea(l, w, h)
        assertEquals(0, BigDecimal("120").compareTo(lsa))
    }

    // 4. Length × width face area
    @Test
    fun testFaceAreaLW() {
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val lw = BoxCalculator.calculateFaceAreaLW(l, w)
        assertEquals(0, BigDecimal("50").compareTo(lw))
    }

    // 5. Length × height face area
    @Test
    fun testFaceAreaLH() {
        val l = BigDecimal("10")
        val h = BigDecimal("4")
        val lh = BoxCalculator.calculateFaceAreaLH(l, h)
        assertEquals(0, BigDecimal("40").compareTo(lh))
    }

    // 6. Width × height face area
    @Test
    fun testFaceAreaWH() {
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val wh = BoxCalculator.calculateFaceAreaWH(w, h)
        assertEquals(0, BigDecimal("20").compareTo(wh))
    }

    // 7. Base perimeter
    @Test
    fun testBasePerimeter() {
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val p = BoxCalculator.calculateBasePerimeter(l, w)
        assertEquals(0, BigDecimal("30").compareTo(p))
    }

    // 8. Space diagonal
    @Test
    fun testSpaceDiagonal() {
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val d = BoxCalculator.calculateSpaceDiagonal(l, w, h)
        // d = sqrt(100 + 25 + 16) = sqrt(141) = 11.874342087...
        val formatted = BoxMeasureFormatter.format(d, decimals = 6, roundingMode = RoundingMode.HALF_UP)
        assertTrue("Expected 11.874342 but was $formatted", formatted == "11.874342" || formatted == "11.874343")
    }

    // 9. Total edge length
    @Test
    fun testTotalEdgeLength() {
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val e = BoxCalculator.calculateTotalEdgeLength(l, w, h)
        // E = 4(10 + 5 + 4) = 4(19) = 76
        assertEquals(0, BigDecimal("76").compareTo(e))
    }

    // 10. Find length
    @Test
    fun testFindLength() {
        val v = BigDecimal("200")
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val l = BoxCalculator.calculateMissingLength(v, w, h)
        assertEquals(0, BigDecimal("10").compareTo(l))
    }

    // 11. Find width
    @Test
    fun testFindWidth() {
        val v = BigDecimal("200")
        val l = BigDecimal("10")
        val h = BigDecimal("4")
        val w = BoxCalculator.calculateMissingWidth(v, l, h)
        assertEquals(0, BigDecimal("5").compareTo(w))
    }

    // 12. Find height
    @Test
    fun testFindHeight() {
        val v = BigDecimal("200")
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val h = BoxCalculator.calculateMissingHeight(v, l, w)
        assertEquals(0, BigDecimal("4").compareTo(h))
    }

    // 13. Cube (L = W = H = 5 cm)
    @Test
    fun testCubeCalculations() {
        val s = BigDecimal("5")
        val volume = BoxCalculator.calculateVolume(s, s, s)
        val sa = BoxCalculator.calculateSurfaceArea(s, s, s)
        val lsa = BoxCalculator.calculateLateralSurfaceArea(s, s, s)
        val p = BoxCalculator.calculateBasePerimeter(s, s)
        val e = BoxCalculator.calculateTotalEdgeLength(s, s, s)
        val d = BoxCalculator.calculateSpaceDiagonal(s, s, s)

        assertEquals(0, BigDecimal("125").compareTo(volume))
        assertEquals(0, BigDecimal("150").compareTo(sa))
        assertEquals(0, BigDecimal("100").compareTo(lsa))
        assertEquals(0, BigDecimal("20").compareTo(p))
        assertEquals(0, BigDecimal("60").compareTo(e))

        val formattedDiag = BoxMeasureFormatter.format(d, decimals = 6, roundingMode = RoundingMode.HALF_UP)
        assertEquals("8.660254", formattedDiag)
    }

    // 14. Rectangular box (L = 2, W = 1, H = 0.5)
    @Test
    fun testRectangularBoxCalculations() {
        val l = BigDecimal("2")
        val w = BigDecimal("1")
        val h = BigDecimal("0.5")

        val volume = BoxCalculator.calculateVolume(l, w, h)
        val sa = BoxCalculator.calculateSurfaceArea(l, w, h)
        val lsa = BoxCalculator.calculateLateralSurfaceArea(l, w, h)
        val p = BoxCalculator.calculateBasePerimeter(l, w)
        val e = BoxCalculator.calculateTotalEdgeLength(l, w, h)
        val d = BoxCalculator.calculateSpaceDiagonal(l, w, h)

        assertEquals(0, BigDecimal("1").compareTo(volume))
        assertEquals(0, BigDecimal("7").compareTo(sa))
        assertEquals(0, BigDecimal("3").compareTo(lsa))
        assertEquals(0, BigDecimal("6").compareTo(p))
        assertEquals(0, BigDecimal("14").compareTo(e))

        val formattedDiag = BoxMeasureFormatter.format(d, decimals = 6, roundingMode = RoundingMode.HALF_UP)
        assertEquals("2.291288", formattedDiag)
    }
}
