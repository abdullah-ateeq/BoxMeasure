package com.example.boxmeasure.data.model

import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.SolveMode
import com.example.boxmeasure.domain.model.VolumeUnit

data class BoxMeasureHistory(
    val id: String,
    val solveMode: SolveMode,
    val missingDimension: DimensionType?,
    val length: String,
    val width: String,
    val height: String,
    val volume: String?,
    val lengthUnit: LengthUnit,
    val widthUnit: LengthUnit,
    val heightUnit: LengthUnit,
    val resultLengthUnit: LengthUnit,
    val resultAreaUnit: AreaUnit,
    val resultVolumeUnit: VolumeUnit,
    val savedAt: Long
) {
    /**
     * Determines whether two history entries represent the identical calculation.
     */
    fun isDuplicateOf(other: BoxMeasureHistory): Boolean {
        return solveMode == other.solveMode &&
                missingDimension == other.missingDimension &&
                length == other.length &&
                width == other.width &&
                height == other.height &&
                volume == other.volume &&
                lengthUnit == other.lengthUnit &&
                widthUnit == other.widthUnit &&
                heightUnit == other.heightUnit &&
                resultLengthUnit == other.resultLengthUnit &&
                resultAreaUnit == other.resultAreaUnit &&
                resultVolumeUnit == other.resultVolumeUnit
    }
}
