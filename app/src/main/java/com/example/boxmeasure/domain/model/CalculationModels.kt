package com.example.boxmeasure.domain.model

import java.math.BigDecimal

enum class DimensionType {
    LENGTH,
    WIDTH,
    HEIGHT
}

enum class SolveMode {
    BOX_MEASUREMENTS,
    FIND_MISSING_DIMENSION
}

data class CalculationStep(
    val label: String,
    val expression: String,
    val result: String
)

data class BoxResult(
    val length: BigDecimal,
    val width: BigDecimal,
    val height: BigDecimal,
    val volume: BigDecimal,
    val surfaceArea: BigDecimal,
    val lateralSurfaceArea: BigDecimal,
    val lengthWidthArea: BigDecimal,
    val lengthHeightArea: BigDecimal,
    val widthHeightArea: BigDecimal,
    val basePerimeter: BigDecimal,
    val spaceDiagonal: BigDecimal,
    val totalEdgeLength: BigDecimal,
    val calculationSteps: List<CalculationStep>,
    val calculatedAt: Long
)

data class MissingDimensionResult(
    val missingDimension: DimensionType,
    val length: BigDecimal,
    val width: BigDecimal,
    val height: BigDecimal,
    val volume: BigDecimal,
    val calculationSteps: List<CalculationStep>,
    val calculatedAt: Long
)

sealed interface BoxMeasureError {
    data object EmptyInput : BoxMeasureError
    data object InvalidNumber : BoxMeasureError
    data object NonPositiveDimension : BoxMeasureError
    data object NonPositiveVolume : BoxMeasureError
    data object InputTooLong : BoxMeasureError
    data object MissingDimensionInput : BoxMeasureError

    fun toUserMessage(fieldName: String = "Dimension"): String {
        return when (this) {
            is EmptyInput -> "$fieldName is required to calculate the complete box measurements."
            is InvalidNumber -> "Enter a valid number."
            is NonPositiveDimension -> "$fieldName must be greater than zero."
            is NonPositiveVolume -> "Volume must be greater than zero."
            is InputTooLong -> "Number is too long. Maximum 500 digits."
            is MissingDimensionInput -> "Please fill all required dimensions."
        }
    }
}
