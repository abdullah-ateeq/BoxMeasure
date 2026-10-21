package com.example.boxmeasure

import com.example.boxmeasure.domain.math.AreaUnitConverter
import com.example.boxmeasure.domain.math.BoxCalculator
import com.example.boxmeasure.domain.math.LengthUnitConverter
import com.example.boxmeasure.domain.math.MathConstants
import com.example.boxmeasure.domain.math.VolumeUnitConverter
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.VolumeUnit
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class UnitConversionTest {

    // 25. Length unit conversion (100 cm = 1 m)
    @Test
    fun testLengthUnitConversion() {
        val cm100 = BigDecimal("100")
        val inMeters = LengthUnitConverter.toMeters(cm100, LengthUnit.CENTIMETER)
        assertEquals(0, BigDecimal("1").compareTo(inMeters))

        val convertedBack = LengthUnitConverter.fromMeters(inMeters, LengthUnit.CENTIMETER, MathConstants.MATH_CONTEXT)
        assertEquals(0, BigDecimal("100").compareTo(convertedBack))

        val mm1000 = BigDecimal("1000")
        val mmInMeters = LengthUnitConverter.toMeters(mm1000, LengthUnit.MILLIMETER)
        assertEquals(0, BigDecimal("1").compareTo(mmInMeters))

        val km1 = BigDecimal("1")
        val kmInMeters = LengthUnitConverter.toMeters(km1, LengthUnit.KILOMETER)
        assertEquals(0, BigDecimal("1000").compareTo(kmInMeters))

        val ft1 = BigDecimal("1")
        val ftInMeters = LengthUnitConverter.toMeters(ft1, LengthUnit.FOOT)
        assertEquals(0, BigDecimal("0.3048").compareTo(ftInMeters))

        val in1 = BigDecimal("1")
        val inInMeters = LengthUnitConverter.toMeters(in1, LengthUnit.INCH)
        assertEquals(0, BigDecimal("0.0254").compareTo(inInMeters))

        val yd1 = BigDecimal("1")
        val ydInMeters = LengthUnitConverter.toMeters(yd1, LengthUnit.YARD)
        assertEquals(0, BigDecimal("0.9144").compareTo(ydInMeters))
    }

    // 26. Area unit conversion (10000 cm² = 1 m²)
    @Test
    fun testAreaUnitConversion() {
        val cm2_10000 = BigDecimal("10000")
        val inSquareMeters = AreaUnitConverter.toSquareMeters(cm2_10000, AreaUnit.SQUARE_CENTIMETER)
        assertEquals(0, BigDecimal("1").compareTo(inSquareMeters))

        val mm2_1000000 = BigDecimal("1000000")
        val mm2InSqMeters = AreaUnitConverter.toSquareMeters(mm2_1000000, AreaUnit.SQUARE_MILLIMETER)
        assertEquals(0, BigDecimal("1").compareTo(mm2InSqMeters))

        val ft2_1 = BigDecimal("1")
        val ft2InSqMeters = AreaUnitConverter.toSquareMeters(ft2_1, AreaUnit.SQUARE_FOOT)
        assertEquals(0, BigDecimal("0.09290304").compareTo(ft2InSqMeters))
    }

    // 27. Volume unit conversion (1000000 cm³ = 1 m³)
    @Test
    fun testVolumeUnitConversion() {
        val cm3_1000000 = BigDecimal("1000000")
        val inCubicMeters = VolumeUnitConverter.toCubicMeters(cm3_1000000, VolumeUnit.CUBIC_CENTIMETER)
        assertEquals(0, BigDecimal("1").compareTo(inCubicMeters))

        val mm3_1000000000 = BigDecimal("1000000000")
        val mm3InCuMeters = VolumeUnitConverter.toCubicMeters(mm3_1000000000, VolumeUnit.CUBIC_MILLIMETER)
        assertEquals(0, BigDecimal("1").compareTo(mm3InCuMeters))

        val ft3_1 = BigDecimal("1")
        val ft3InCuMeters = VolumeUnitConverter.toCubicMeters(ft3_1, VolumeUnit.CUBIC_FOOT)
        assertEquals(0, BigDecimal("0.028316846592").compareTo(ft3InCuMeters))
    }

    // 28. Mixed input units (L = 1 m, W = 50 cm, H = 1000 mm -> V = 0.5 m³, SA = 5 m²)
    @Test
    fun testMixedInputUnits() {
        val l = BigDecimal("1")      // 1 m
        val w = BigDecimal("50")     // 50 cm
        val h = BigDecimal("1000")   // 1000 mm

        val lM = LengthUnitConverter.toMeters(l, LengthUnit.METER)
        val wM = LengthUnitConverter.toMeters(w, LengthUnit.CENTIMETER)
        val hM = LengthUnitConverter.toMeters(h, LengthUnit.MILLIMETER)

        assertEquals(0, BigDecimal("1").compareTo(lM))
        assertEquals(0, BigDecimal("0.5").compareTo(wM))
        assertEquals(0, BigDecimal("1").compareTo(hM))

        val volumeM3 = BoxCalculator.calculateVolume(lM, wM, hM)
        assertEquals(0, BigDecimal("0.5").compareTo(volumeM3))

        val surfaceAreaM2 = BoxCalculator.calculateSurfaceArea(lM, wM, hM)
        assertEquals(0, BigDecimal("4").compareTo(surfaceAreaM2))
    }

    // 29. Mixed result units (Input imperial -> result in metric or vice-versa)
    @Test
    fun testMixedResultUnits() {
        // L = 1 ft, W = 1 ft, H = 1 ft
        val lFt = BigDecimal("1")
        val wFt = BigDecimal("1")
        val hFt = BigDecimal("1")

        val lM = LengthUnitConverter.toMeters(lFt, LengthUnit.FOOT)
        val wM = LengthUnitConverter.toMeters(wFt, LengthUnit.FOOT)
        val hM = LengthUnitConverter.toMeters(hFt, LengthUnit.FOOT)

        val volM3 = BoxCalculator.calculateVolume(lM, wM, hM)
        val saM2 = BoxCalculator.calculateSurfaceArea(lM, wM, hM)

        // Convert result to Cubic Foot and Square Foot
        val volFt3 = VolumeUnitConverter.fromCubicMeters(volM3, VolumeUnit.CUBIC_FOOT, MathConstants.MATH_CONTEXT)
        val saFt2 = AreaUnitConverter.fromSquareMeters(saM2, AreaUnit.SQUARE_FOOT, MathConstants.MATH_CONTEXT)

        assertEquals(0, BigDecimal("1").compareTo(volFt3.setScale(6, RoundingMode.HALF_UP)))
        assertEquals(0, BigDecimal("6").compareTo(saFt2.setScale(6, RoundingMode.HALF_UP)))
    }
}
