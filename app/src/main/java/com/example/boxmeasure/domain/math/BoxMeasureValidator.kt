package com.example.boxmeasure.domain.math

import com.example.boxmeasure.domain.model.BoxMeasureError
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.SolveMode
import java.math.BigDecimal

object BoxMeasureValidator {

    fun validateDimension(input: String, fieldName: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(ValidationException(BoxMeasureError.EmptyInput, "$fieldName is required to calculate the complete box measurements."))
        }

        val parseResult = BoxMeasureParser.parseNumber(trimmed)
        if (parseResult.isFailure) {
            val message = parseResult.exceptionOrNull()?.message ?: "Enter a valid number."
            val error = if (message.contains("500")) {
                BoxMeasureError.InputTooLong
            } else {
                BoxMeasureError.InvalidNumber
            }
            return Result.failure(ValidationException(error, message))
        }

        val value = parseResult.getOrThrow()
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.failure(
                ValidationException(
                    BoxMeasureError.NonPositiveDimension,
                    "$fieldName must be greater than zero."
                )
            )
        }

        return Result.success(value)
    }

    fun validateVolume(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(ValidationException(BoxMeasureError.EmptyInput, "Volume is required to calculate missing dimension."))
        }

        val parseResult = BoxMeasureParser.parseNumber(trimmed)
        if (parseResult.isFailure) {
            val message = parseResult.exceptionOrNull()?.message ?: "Enter a valid number."
            val error = if (message.contains("500")) {
                BoxMeasureError.InputTooLong
            } else {
                BoxMeasureError.InvalidNumber
            }
            return Result.failure(ValidationException(error, message))
        }

        val value = parseResult.getOrThrow()
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.failure(
                ValidationException(
                    BoxMeasureError.NonPositiveVolume,
                    "Volume must be greater than zero."
                )
            )
        }

        return Result.success(value)
    }

    fun validateForMode(
        solveMode: SolveMode,
        missingDimension: DimensionType,
        lengthInput: String,
        widthInput: String,
        heightInput: String,
        volumeInput: String
    ): Result<Unit> {
        return when (solveMode) {
            SolveMode.BOX_MEASUREMENTS -> {
                val lRes = validateDimension(lengthInput, "Length")
                if (lRes.isFailure) return Result.failure(lRes.exceptionOrNull()!!)
                val wRes = validateDimension(widthInput, "Width")
                if (wRes.isFailure) return Result.failure(wRes.exceptionOrNull()!!)
                val hRes = validateDimension(heightInput, "Height")
                if (hRes.isFailure) return Result.failure(hRes.exceptionOrNull()!!)
                Result.success(Unit)
            }
            SolveMode.FIND_MISSING_DIMENSION -> {
                val vRes = validateVolume(volumeInput)
                if (vRes.isFailure) return Result.failure(vRes.exceptionOrNull()!!)
                when (missingDimension) {
                    DimensionType.LENGTH -> {
                        val wRes = validateDimension(widthInput, "Width")
                        if (wRes.isFailure) return Result.failure(wRes.exceptionOrNull()!!)
                        val hRes = validateDimension(heightInput, "Height")
                        if (hRes.isFailure) return Result.failure(hRes.exceptionOrNull()!!)
                    }
                    DimensionType.WIDTH -> {
                        val lRes = validateDimension(lengthInput, "Length")
                        if (lRes.isFailure) return Result.failure(lRes.exceptionOrNull()!!)
                        val hRes = validateDimension(heightInput, "Height")
                        if (hRes.isFailure) return Result.failure(hRes.exceptionOrNull()!!)
                    }
                    DimensionType.HEIGHT -> {
                        val lRes = validateDimension(lengthInput, "Length")
                        if (lRes.isFailure) return Result.failure(lRes.exceptionOrNull()!!)
                        val wRes = validateDimension(widthInput, "Width")
                        if (wRes.isFailure) return Result.failure(wRes.exceptionOrNull()!!)
                    }
                }
                Result.success(Unit)
            }
        }
    }
}

class ValidationException(
    val error: BoxMeasureError,
    override val message: String
) : IllegalArgumentException(message)
