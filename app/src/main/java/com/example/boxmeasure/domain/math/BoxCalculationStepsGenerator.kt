package com.example.boxmeasure.domain.math

import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.CalculationStep
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.VolumeUnit
import java.math.BigDecimal
import java.math.RoundingMode

object BoxCalculationStepsGenerator {

    fun generateBoxSteps(
        l: BigDecimal,
        w: BigDecimal,
        h: BigDecimal,
        volume: BigDecimal,
        surfaceArea: BigDecimal,
        lateralSurfaceArea: BigDecimal,
        lw: BigDecimal,
        lh: BigDecimal,
        wh: BigDecimal,
        basePerimeter: BigDecimal,
        spaceDiagonal: BigDecimal,
        totalEdgeLength: BigDecimal,
        lengthUnit: LengthUnit,
        areaUnit: AreaUnit,
        volumeUnit: VolumeUnit,
        decimals: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): List<CalculationStep> {
        val lStr = BoxMeasureFormatter.format(l, decimals, roundingMode)
        val wStr = BoxMeasureFormatter.format(w, decimals, roundingMode)
        val hStr = BoxMeasureFormatter.format(h, decimals, roundingMode)

        val vStr = BoxMeasureFormatter.formatWithUnit(volume, volumeUnit.symbol, decimals, roundingMode)
        val saStr = BoxMeasureFormatter.formatWithUnit(surfaceArea, areaUnit.symbol, decimals, roundingMode)
        val lsaStr = BoxMeasureFormatter.formatWithUnit(lateralSurfaceArea, areaUnit.symbol, decimals, roundingMode)
        val lwStr = BoxMeasureFormatter.formatWithUnit(lw, areaUnit.symbol, decimals, roundingMode)
        val lhStr = BoxMeasureFormatter.formatWithUnit(lh, areaUnit.symbol, decimals, roundingMode)
        val whStr = BoxMeasureFormatter.formatWithUnit(wh, areaUnit.symbol, decimals, roundingMode)
        val pStr = BoxMeasureFormatter.formatWithUnit(basePerimeter, lengthUnit.symbol, decimals, roundingMode)
        val dStr = BoxMeasureFormatter.formatWithUnit(spaceDiagonal, lengthUnit.symbol, decimals, roundingMode)
        val eStr = BoxMeasureFormatter.formatWithUnit(totalEdgeLength, lengthUnit.symbol, decimals, roundingMode)

        // Sum of squares for diagonal
        val l2 = l.multiply(l, MathConstants.MATH_CONTEXT)
        val w2 = w.multiply(w, MathConstants.MATH_CONTEXT)
        val h2 = h.multiply(h, MathConstants.MATH_CONTEXT)
        val sumSq = l2.add(w2, MathConstants.MATH_CONTEXT).add(h2, MathConstants.MATH_CONTEXT)
        val sumSqStr = BoxMeasureFormatter.format(sumSq, decimals, roundingMode)

        return listOf(
            CalculationStep(
                label = "Volume",
                expression = "V = L × W × H = $lStr × $wStr × $hStr",
                result = vStr
            ),
            CalculationStep(
                label = "Total Surface Area",
                expression = "SA = 2(LW + LH + WH) = 2(($lStr × $wStr) + ($lStr × $hStr) + ($wStr × $hStr))",
                result = saStr
            ),
            CalculationStep(
                label = "Lateral Surface Area",
                expression = "LSA = 2H(L + W) = 2 × $hStr × ($lStr + $wStr)",
                result = lsaStr
            ),
            CalculationStep(
                label = "Face Areas",
                expression = "Top/Bottom = $lStr × $wStr ($lwStr) | Front/Back = $lStr × $hStr ($lhStr) | Left/Right = $wStr × $hStr ($whStr)",
                result = "LW: $lwStr, LH: $lhStr, WH: $whStr"
            ),
            CalculationStep(
                label = "Space Diagonal",
                expression = "d = √(L² + W² + H²) = √($lStr² + $wStr² + $hStr²) = √$sumSqStr",
                result = "≈ $dStr"
            ),
            CalculationStep(
                label = "Base Perimeter",
                expression = "P = 2(L + W) = 2($lStr + $wStr)",
                result = pStr
            ),
            CalculationStep(
                label = "Total Edge Length",
                expression = "E = 4(L + W + H) = 4($lStr + $wStr + $hStr)",
                result = eStr
            )
        )
    }

    fun generateMissingDimensionSteps(
        missingType: DimensionType,
        known1Label: String,
        known1Val: BigDecimal,
        known2Label: String,
        known2Val: BigDecimal,
        volume: BigDecimal,
        resultVal: BigDecimal,
        lengthUnit: LengthUnit,
        volumeUnit: VolumeUnit,
        decimals: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): List<CalculationStep> {
        val k1Str = BoxMeasureFormatter.format(known1Val, decimals, roundingMode)
        val k2Str = BoxMeasureFormatter.format(known2Val, decimals, roundingMode)
        val vStr = BoxMeasureFormatter.format(volume, decimals, roundingMode)
        val resStr = BoxMeasureFormatter.formatWithUnit(resultVal, lengthUnit.symbol, decimals, roundingMode)

        val mult = known1Val.multiply(known2Val, MathConstants.MATH_CONTEXT)
        val multStr = BoxMeasureFormatter.format(mult, decimals, roundingMode)

        return when (missingType) {
            DimensionType.LENGTH -> listOf(
                CalculationStep(
                    label = "Missing Length Formula",
                    expression = "L = V / (W × H)",
                    result = ""
                ),
                CalculationStep(
                    label = "Substitution",
                    expression = "L = $vStr / ($k1Str × $k2Str) = $vStr / $multStr",
                    result = resStr
                )
            )
            DimensionType.WIDTH -> listOf(
                CalculationStep(
                    label = "Missing Width Formula",
                    expression = "W = V / (L × H)",
                    result = ""
                ),
                CalculationStep(
                    label = "Substitution",
                    expression = "W = $vStr / ($k1Str × $k2Str) = $vStr / $multStr",
                    result = resStr
                )
            )
            DimensionType.HEIGHT -> listOf(
                CalculationStep(
                    label = "Missing Height Formula",
                    expression = "H = V / (L × W)",
                    result = ""
                ),
                CalculationStep(
                    label = "Substitution",
                    expression = "H = $vStr / ($k1Str × $k2Str) = $vStr / $multStr",
                    result = resStr
                )
            )
        }
    }
}
