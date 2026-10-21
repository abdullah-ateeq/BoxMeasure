package com.example.boxmeasure.ui.home

import com.example.boxmeasure.data.model.BoxMeasureHistory
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.BoxResult
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.MissingDimensionResult
import com.example.boxmeasure.domain.model.SolveMode
import com.example.boxmeasure.domain.model.VolumeUnit

data class BoxMeasureUiState(
    val solveMode: SolveMode = SolveMode.BOX_MEASUREMENTS,
    val missingDimension: DimensionType = DimensionType.LENGTH,

    val lengthInput: String = "10",
    val widthInput: String = "5",
    val heightInput: String = "4",
    val volumeInput: String = "",

    val lengthUnit: LengthUnit = LengthUnit.CENTIMETER,
    val widthUnit: LengthUnit = LengthUnit.CENTIMETER,
    val heightUnit: LengthUnit = LengthUnit.CENTIMETER,
    val volumeInputUnit: VolumeUnit = VolumeUnit.CUBIC_CENTIMETER,

    val resultLengthUnit: LengthUnit = LengthUnit.CENTIMETER,
    val resultAreaUnit: AreaUnit = AreaUnit.SQUARE_CENTIMETER,
    val resultVolumeUnit: VolumeUnit = VolumeUnit.CUBIC_CENTIMETER,

    val result: BoxResult? = null,
    val missingResult: MissingDimensionResult? = null,

    val history: List<BoxMeasureHistory> = emptyList(),
    val errorMessage: String? = null,
    val isCalculating: Boolean = false
)
