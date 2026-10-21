package com.example.boxmeasure.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmeasure.data.model.BoxMeasureHistory
import com.example.boxmeasure.data.repository.AppSettings
import com.example.boxmeasure.data.repository.HistoryRepository
import com.example.boxmeasure.data.repository.SettingsRepository
import com.example.boxmeasure.domain.math.AreaUnitConverter
import com.example.boxmeasure.domain.math.BigDecimalMath
import com.example.boxmeasure.domain.math.BoxCalculationStepsGenerator
import com.example.boxmeasure.domain.math.BoxCalculator
import com.example.boxmeasure.domain.math.BoxMeasureFormatter
import com.example.boxmeasure.domain.math.BoxMeasureValidator
import com.example.boxmeasure.domain.math.LengthUnitConverter
import com.example.boxmeasure.domain.math.MathConstants
import com.example.boxmeasure.domain.math.VolumeUnitConverter
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.BoxResult
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.MissingDimensionResult
import com.example.boxmeasure.domain.model.SolveMode
import com.example.boxmeasure.domain.model.VolumeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.UUID

class BoxMeasureViewModel @JvmOverloads constructor(
    application: Application,
    private val settingsRepository: SettingsRepository = SettingsRepository(application),
    private val historyRepository: HistoryRepository = HistoryRepository(application)
) : AndroidViewModel(application) {

    private val _settings = MutableStateFlow(settingsRepository.loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _uiState = MutableStateFlow(
        BoxMeasureUiState(
            solveMode = _settings.value.lastSolveMode,
            missingDimension = _settings.value.lastMissingDimension,
            lengthInput = _settings.value.lastLengthInput,
            widthInput = _settings.value.lastWidthInput,
            heightInput = _settings.value.lastHeightInput,
            volumeInput = _settings.value.lastVolumeInput,
            lengthUnit = _settings.value.lastLengthUnit,
            widthUnit = _settings.value.lastWidthUnit,
            heightUnit = _settings.value.lastHeightUnit,
            volumeInputUnit = _settings.value.lastVolumeInputUnit,
            resultLengthUnit = _settings.value.resultLengthUnit,
            resultAreaUnit = _settings.value.resultAreaUnit,
            resultVolumeUnit = _settings.value.resultVolumeUnit,
            history = historyRepository.getHistory()
        )
    )
    val uiState: StateFlow<BoxMeasureUiState> = _uiState.asStateFlow()

    init {
        // Run initial calculation with saved/default inputs
        calculate(saveInputs = false)
    }

    fun setSolveMode(mode: SolveMode) {
        if (_uiState.value.solveMode == mode) return
        _uiState.update { it.copy(solveMode = mode, errorMessage = null) }
        persistCurrentInputs()
        calculate(saveInputs = true)
    }

    fun setMissingDimension(dimension: DimensionType) {
        if (_uiState.value.missingDimension == dimension) return
        _uiState.update { it.copy(missingDimension = dimension, errorMessage = null) }
        persistCurrentInputs()
        calculate(saveInputs = true)
    }

    fun setLengthInput(input: String) {
        _uiState.update { it.copy(lengthInput = input, errorMessage = null) }
        autoCalculateIfValid()
    }

    fun setWidthInput(input: String) {
        _uiState.update { it.copy(widthInput = input, errorMessage = null) }
        autoCalculateIfValid()
    }

    fun setHeightInput(input: String) {
        _uiState.update { it.copy(heightInput = input, errorMessage = null) }
        autoCalculateIfValid()
    }

    fun setVolumeInput(input: String) {
        _uiState.update { it.copy(volumeInput = input, errorMessage = null) }
        autoCalculateIfValid()
    }

    fun setLengthUnit(unit: LengthUnit) {
        _uiState.update { it.copy(lengthUnit = unit) }
        persistCurrentInputs()
        calculate(saveInputs = false)
    }

    fun setWidthUnit(unit: LengthUnit) {
        _uiState.update { it.copy(widthUnit = unit) }
        persistCurrentInputs()
        calculate(saveInputs = false)
    }

    fun setHeightUnit(unit: LengthUnit) {
        _uiState.update { it.copy(heightUnit = unit) }
        persistCurrentInputs()
        calculate(saveInputs = false)
    }

    fun setVolumeInputUnit(unit: VolumeUnit) {
        _uiState.update { it.copy(volumeInputUnit = unit) }
        persistCurrentInputs()
        calculate(saveInputs = false)
    }

    fun setResultLengthUnit(unit: LengthUnit) {
        _uiState.update { it.copy(resultLengthUnit = unit) }
        persistCurrentInputs()
        calculate(saveInputs = false)
    }

    fun setResultAreaUnit(unit: AreaUnit) {
        _uiState.update { it.copy(resultAreaUnit = unit) }
        persistCurrentInputs()
        calculate(saveInputs = false)
    }

    fun setResultVolumeUnit(unit: VolumeUnit) {
        _uiState.update { it.copy(resultVolumeUnit = unit) }
        persistCurrentInputs()
        calculate(saveInputs = false)
    }

    private fun autoCalculateIfValid() {
        val state = _uiState.value
        val validation = BoxMeasureValidator.validateForMode(
            state.solveMode,
            state.missingDimension,
            state.lengthInput,
            state.widthInput,
            state.heightInput,
            state.volumeInput
        )
        if (validation.isSuccess) {
            calculate(saveInputs = true)
        }
    }

    /**
     * Executes authoritative calculation on background dispatcher.
     */
    fun calculate(saveInputs: Boolean = true) {
        val state = _uiState.value
        val currentSettings = _settings.value

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true) }

            val validation = BoxMeasureValidator.validateForMode(
                state.solveMode,
                state.missingDimension,
                state.lengthInput,
                state.widthInput,
                state.heightInput,
                state.volumeInput
            )

            if (validation.isFailure) {
                val errorMsg = validation.exceptionOrNull()?.message ?: "Validation error"
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        errorMessage = errorMsg,
                        result = null,
                        missingResult = null
                    )
                }
                return@launch
            }

            val calcResult = withContext(Dispatchers.Default) {
                runCatching {
                    when (state.solveMode) {
                        SolveMode.BOX_MEASUREMENTS -> calculateBoxMeasurements(state, currentSettings)
                        SolveMode.FIND_MISSING_DIMENSION -> calculateMissingDimension(state, currentSettings)
                    }
                }
            }

            if (calcResult.isSuccess) {
                val outcome = calcResult.getOrThrow()
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        errorMessage = null,
                        result = outcome.boxResult,
                        missingResult = outcome.missingResult
                    )
                }
                if (saveInputs) {
                    persistCurrentInputs()
                }
            } else {
                val msg = calcResult.exceptionOrNull()?.message ?: "Calculation error"
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        errorMessage = msg,
                        result = null,
                        missingResult = null
                    )
                }
            }
        }
    }

    private data class CalculationOutcome(
        val boxResult: BoxResult? = null,
        val missingResult: MissingDimensionResult? = null
    )

    private fun calculateBoxMeasurements(
        state: BoxMeasureUiState,
        settings: AppSettings
    ): CalculationOutcome {
        val lRaw = BoxMeasureValidator.validateDimension(state.lengthInput, "Length").getOrThrow()
        val wRaw = BoxMeasureValidator.validateDimension(state.widthInput, "Width").getOrThrow()
        val hRaw = BoxMeasureValidator.validateDimension(state.heightInput, "Height").getOrThrow()

        // Normalize to meters (internal SI unit)
        val lMeters = LengthUnitConverter.toMeters(lRaw, state.lengthUnit)
        val wMeters = LengthUnitConverter.toMeters(wRaw, state.widthUnit)
        val hMeters = LengthUnitConverter.toMeters(hRaw, state.heightUnit)

        // Authoritative SI calculations using MathConstants.MATH_CONTEXT (50 digits)
        val vMeters3 = BoxCalculator.calculateVolume(lMeters, wMeters, hMeters)
        val saMeters2 = BoxCalculator.calculateSurfaceArea(lMeters, wMeters, hMeters)
        val lsaMeters2 = BoxCalculator.calculateLateralSurfaceArea(lMeters, wMeters, hMeters)
        val lwMeters2 = BoxCalculator.calculateFaceAreaLW(lMeters, wMeters)
        val lhMeters2 = BoxCalculator.calculateFaceAreaLH(lMeters, hMeters)
        val whMeters2 = BoxCalculator.calculateFaceAreaWH(wMeters, hMeters)
        val pMeters = BoxCalculator.calculateBasePerimeter(lMeters, wMeters)
        val dMeters = BoxCalculator.calculateSpaceDiagonal(lMeters, wMeters, hMeters)
        val eMeters = BoxCalculator.calculateTotalEdgeLength(lMeters, wMeters, hMeters)

        // Convert to target result units
        val lResult = LengthUnitConverter.fromMeters(lMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
        val wResult = LengthUnitConverter.fromMeters(wMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
        val hResult = LengthUnitConverter.fromMeters(hMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
        val vResult = VolumeUnitConverter.fromCubicMeters(vMeters3, state.resultVolumeUnit, MathConstants.MATH_CONTEXT)
        val saResult = AreaUnitConverter.fromSquareMeters(saMeters2, state.resultAreaUnit, MathConstants.MATH_CONTEXT)
        val lsaResult = AreaUnitConverter.fromSquareMeters(lsaMeters2, state.resultAreaUnit, MathConstants.MATH_CONTEXT)
        val lwResult = AreaUnitConverter.fromSquareMeters(lwMeters2, state.resultAreaUnit, MathConstants.MATH_CONTEXT)
        val lhResult = AreaUnitConverter.fromSquareMeters(lhMeters2, state.resultAreaUnit, MathConstants.MATH_CONTEXT)
        val whResult = AreaUnitConverter.fromSquareMeters(whMeters2, state.resultAreaUnit, MathConstants.MATH_CONTEXT)
        val pResult = LengthUnitConverter.fromMeters(pMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
        val dResult = LengthUnitConverter.fromMeters(dMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
        val eResult = LengthUnitConverter.fromMeters(eMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)

        val steps = BoxCalculationStepsGenerator.generateBoxSteps(
            l = lResult,
            w = wResult,
            h = hResult,
            volume = vResult,
            surfaceArea = saResult,
            lateralSurfaceArea = lsaResult,
            lw = lwResult,
            lh = lhResult,
            wh = whResult,
            basePerimeter = pResult,
            spaceDiagonal = dResult,
            totalEdgeLength = eResult,
            lengthUnit = state.resultLengthUnit,
            areaUnit = state.resultAreaUnit,
            volumeUnit = state.resultVolumeUnit,
            decimals = settings.decimalPlaces,
            roundingMode = settings.roundingMode
        )

        val result = BoxResult(
            length = lResult,
            width = wResult,
            height = hResult,
            volume = vResult,
            surfaceArea = saResult,
            lateralSurfaceArea = lsaResult,
            lengthWidthArea = lwResult,
            lengthHeightArea = lhResult,
            widthHeightArea = whResult,
            basePerimeter = pResult,
            spaceDiagonal = dResult,
            totalEdgeLength = eResult,
            calculationSteps = steps,
            calculatedAt = System.currentTimeMillis()
        )

        return CalculationOutcome(boxResult = result)
    }

    private fun calculateMissingDimension(
        state: BoxMeasureUiState,
        settings: AppSettings
    ): CalculationOutcome {
        val vRaw = BoxMeasureValidator.validateVolume(state.volumeInput).getOrThrow()
        val vMeters3 = VolumeUnitConverter.toCubicMeters(vRaw, state.volumeInputUnit)

        val missingType = state.missingDimension
        val lResult: BigDecimal
        val wResult: BigDecimal
        val hResult: BigDecimal

        val steps: List<com.example.boxmeasure.domain.model.CalculationStep>

        when (missingType) {
            DimensionType.LENGTH -> {
                val wRaw = BoxMeasureValidator.validateDimension(state.widthInput, "Width").getOrThrow()
                val hRaw = BoxMeasureValidator.validateDimension(state.heightInput, "Height").getOrThrow()
                val wMeters = LengthUnitConverter.toMeters(wRaw, state.widthUnit)
                val hMeters = LengthUnitConverter.toMeters(hRaw, state.heightUnit)

                val lMeters = BoxCalculator.calculateMissingLength(vMeters3, wMeters, hMeters)

                lResult = LengthUnitConverter.fromMeters(lMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
                wResult = LengthUnitConverter.fromMeters(wMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
                hResult = LengthUnitConverter.fromMeters(hMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)

                val vDisplay = VolumeUnitConverter.fromCubicMeters(vMeters3, state.resultVolumeUnit, MathConstants.MATH_CONTEXT)

                steps = BoxCalculationStepsGenerator.generateMissingDimensionSteps(
                    missingType = DimensionType.LENGTH,
                    known1Label = "Width",
                    known1Val = wResult,
                    known2Label = "Height",
                    known2Val = hResult,
                    volume = vDisplay,
                    resultVal = lResult,
                    lengthUnit = state.resultLengthUnit,
                    volumeUnit = state.resultVolumeUnit,
                    decimals = settings.decimalPlaces,
                    roundingMode = settings.roundingMode
                )
            }
            DimensionType.WIDTH -> {
                val lRaw = BoxMeasureValidator.validateDimension(state.lengthInput, "Length").getOrThrow()
                val hRaw = BoxMeasureValidator.validateDimension(state.heightInput, "Height").getOrThrow()
                val lMeters = LengthUnitConverter.toMeters(lRaw, state.lengthUnit)
                val hMeters = LengthUnitConverter.toMeters(hRaw, state.heightUnit)

                val wMeters = BoxCalculator.calculateMissingWidth(vMeters3, lMeters, hMeters)

                lResult = LengthUnitConverter.fromMeters(lMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
                wResult = LengthUnitConverter.fromMeters(wMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
                hResult = LengthUnitConverter.fromMeters(hMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)

                val vDisplay = VolumeUnitConverter.fromCubicMeters(vMeters3, state.resultVolumeUnit, MathConstants.MATH_CONTEXT)

                steps = BoxCalculationStepsGenerator.generateMissingDimensionSteps(
                    missingType = DimensionType.WIDTH,
                    known1Label = "Length",
                    known1Val = lResult,
                    known2Label = "Height",
                    known2Val = hResult,
                    volume = vDisplay,
                    resultVal = wResult,
                    lengthUnit = state.resultLengthUnit,
                    volumeUnit = state.resultVolumeUnit,
                    decimals = settings.decimalPlaces,
                    roundingMode = settings.roundingMode
                )
            }
            DimensionType.HEIGHT -> {
                val lRaw = BoxMeasureValidator.validateDimension(state.lengthInput, "Length").getOrThrow()
                val wRaw = BoxMeasureValidator.validateDimension(state.widthInput, "Width").getOrThrow()
                val lMeters = LengthUnitConverter.toMeters(lRaw, state.lengthUnit)
                val wMeters = LengthUnitConverter.toMeters(wRaw, state.widthUnit)

                val hMeters = BoxCalculator.calculateMissingHeight(vMeters3, lMeters, wMeters)

                lResult = LengthUnitConverter.fromMeters(lMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
                wResult = LengthUnitConverter.fromMeters(wMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)
                hResult = LengthUnitConverter.fromMeters(hMeters, state.resultLengthUnit, MathConstants.MATH_CONTEXT)

                val vDisplay = VolumeUnitConverter.fromCubicMeters(vMeters3, state.resultVolumeUnit, MathConstants.MATH_CONTEXT)

                steps = BoxCalculationStepsGenerator.generateMissingDimensionSteps(
                    missingType = DimensionType.HEIGHT,
                    known1Label = "Length",
                    known1Val = lResult,
                    known2Label = "Width",
                    known2Val = wResult,
                    volume = vDisplay,
                    resultVal = hResult,
                    lengthUnit = state.resultLengthUnit,
                    volumeUnit = state.resultVolumeUnit,
                    decimals = settings.decimalPlaces,
                    roundingMode = settings.roundingMode
                )
            }
        }

        val vResult = VolumeUnitConverter.fromCubicMeters(vMeters3, state.resultVolumeUnit, MathConstants.MATH_CONTEXT)

        val missingResult = MissingDimensionResult(
            missingDimension = missingType,
            length = lResult,
            width = wResult,
            height = hResult,
            volume = vResult,
            calculationSteps = steps,
            calculatedAt = System.currentTimeMillis()
        )

        return CalculationOutcome(missingResult = missingResult)
    }

    /**
     * Saves the current calculation to history on explicit user tap.
     * Returns true if saved, false if skipped duplicate.
     */
    fun saveCalculation(): Boolean {
        val state = _uiState.value
        val historyItem = BoxMeasureHistory(
            id = UUID.randomUUID().toString(),
            solveMode = state.solveMode,
            missingDimension = if (state.solveMode == SolveMode.FIND_MISSING_DIMENSION) state.missingDimension else null,
            length = state.lengthInput,
            width = state.widthInput,
            height = state.heightInput,
            volume = if (state.solveMode == SolveMode.FIND_MISSING_DIMENSION) state.volumeInput else null,
            lengthUnit = state.lengthUnit,
            widthUnit = state.widthUnit,
            heightUnit = state.heightUnit,
            resultLengthUnit = state.resultLengthUnit,
            resultAreaUnit = state.resultAreaUnit,
            resultVolumeUnit = state.resultVolumeUnit,
            savedAt = System.currentTimeMillis()
        )

        val saved = historyRepository.save(historyItem)
        if (saved) {
            _uiState.update { it.copy(history = historyRepository.getHistory()) }
        }
        return saved
    }

    fun restoreHistory(item: BoxMeasureHistory) {
        _uiState.update {
            it.copy(
                solveMode = item.solveMode,
                missingDimension = item.missingDimension ?: DimensionType.LENGTH,
                lengthInput = item.length,
                widthInput = item.width,
                heightInput = item.height,
                volumeInput = item.volume ?: "",
                lengthUnit = item.lengthUnit,
                widthUnit = item.widthUnit,
                heightUnit = item.heightUnit,
                resultLengthUnit = item.resultLengthUnit,
                resultAreaUnit = item.resultAreaUnit,
                resultVolumeUnit = item.resultVolumeUnit,
                errorMessage = null
            )
        }
        persistCurrentInputs()
        calculate(saveInputs = false)
    }

    fun deleteHistoryItem(id: String) {
        historyRepository.delete(id)
        _uiState.update { it.copy(history = historyRepository.getHistory()) }
    }

    fun clearHistory() {
        historyRepository.clear()
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun resetToDefaults() {
        _uiState.update {
            it.copy(
                solveMode = SolveMode.BOX_MEASUREMENTS,
                missingDimension = DimensionType.LENGTH,
                lengthInput = "10",
                widthInput = "5",
                heightInput = "4",
                volumeInput = "",
                lengthUnit = LengthUnit.CENTIMETER,
                widthUnit = LengthUnit.CENTIMETER,
                heightUnit = LengthUnit.CENTIMETER,
                volumeInputUnit = VolumeUnit.CUBIC_CENTIMETER,
                resultLengthUnit = LengthUnit.CENTIMETER,
                resultAreaUnit = AreaUnit.SQUARE_CENTIMETER,
                resultVolumeUnit = VolumeUnit.CUBIC_CENTIMETER,
                errorMessage = null
            )
        }
        persistCurrentInputs()
        calculate(saveInputs = true)
    }

    fun updateSettings(newSettings: AppSettings) {
        settingsRepository.saveSettings(newSettings)
        _settings.value = newSettings
        // Recalculate with updated formatting / precision if valid
        calculate(saveInputs = false)
    }

    fun resetSettings() {
        settingsRepository.resetSettings()
        val defaultSettings = settingsRepository.loadSettings()
        _settings.value = defaultSettings
        calculate(saveInputs = false)
    }

    private fun persistCurrentInputs() {
        val state = _uiState.value
        val updated = _settings.value.copy(
            lastSolveMode = state.solveMode,
            lastMissingDimension = state.missingDimension,
            lastLengthInput = state.lengthInput,
            lastWidthInput = state.widthInput,
            lastHeightInput = state.heightInput,
            lastVolumeInput = state.volumeInput,
            lastLengthUnit = state.lengthUnit,
            lastWidthUnit = state.widthUnit,
            lastHeightUnit = state.heightUnit,
            lastVolumeInputUnit = state.volumeInputUnit,
            resultLengthUnit = state.resultLengthUnit,
            resultAreaUnit = state.resultAreaUnit,
            resultVolumeUnit = state.resultVolumeUnit
        )
        settingsRepository.saveSettings(updated)
        _settings.value = updated
    }

    fun buildShareText(): String {
        val state = _uiState.value
        val settings = _settings.value
        val decimals = settings.decimalPlaces
        val rounding = settings.roundingMode

        val sb = StringBuilder()
        sb.appendLine("BoxMeasure")
        sb.appendLine("Calculate box volume, surface area, and dimensions.")
        sb.appendLine()

        if (state.solveMode == SolveMode.BOX_MEASUREMENTS) {
            val res = state.result
            if (res != null) {
                sb.appendLine("Dimensions:")
                sb.appendLine("Length: ${BoxMeasureFormatter.formatWithUnit(res.length, state.resultLengthUnit.symbol, decimals, rounding)}")
                sb.appendLine("Width: ${BoxMeasureFormatter.formatWithUnit(res.width, state.resultLengthUnit.symbol, decimals, rounding)}")
                sb.appendLine("Height: ${BoxMeasureFormatter.formatWithUnit(res.height, state.resultLengthUnit.symbol, decimals, rounding)}")
                sb.appendLine()
                sb.appendLine("Volume: ${BoxMeasureFormatter.formatWithUnit(res.volume, state.resultVolumeUnit.symbol, decimals, rounding)}")
                sb.appendLine("Surface Area: ${BoxMeasureFormatter.formatWithUnit(res.surfaceArea, state.resultAreaUnit.symbol, decimals, rounding)}")
                sb.appendLine("Lateral Surface Area: ${BoxMeasureFormatter.formatWithUnit(res.lateralSurfaceArea, state.resultAreaUnit.symbol, decimals, rounding)}")
                sb.appendLine("Space Diagonal: ${BoxMeasureFormatter.formatWithUnit(res.spaceDiagonal, state.resultLengthUnit.symbol, decimals, rounding)}")
                sb.appendLine("Base Perimeter: ${BoxMeasureFormatter.formatWithUnit(res.basePerimeter, state.resultLengthUnit.symbol, decimals, rounding)}")
                sb.appendLine("Total Edge Length: ${BoxMeasureFormatter.formatWithUnit(res.totalEdgeLength, state.resultLengthUnit.symbol, decimals, rounding)}")
                sb.appendLine()
                sb.appendLine("Face Areas:")
                sb.appendLine("Top/Bottom (LW): ${BoxMeasureFormatter.formatWithUnit(res.lengthWidthArea, state.resultAreaUnit.symbol, decimals, rounding)}")
                sb.appendLine("Front/Back (LH): ${BoxMeasureFormatter.formatWithUnit(res.lengthHeightArea, state.resultAreaUnit.symbol, decimals, rounding)}")
                sb.appendLine("Left/Right (WH): ${BoxMeasureFormatter.formatWithUnit(res.widthHeightArea, state.resultAreaUnit.symbol, decimals, rounding)}")
            } else {
                sb.appendLine("Inputs: L=${state.lengthInput} ${state.lengthUnit.symbol}, W=${state.widthInput} ${state.widthUnit.symbol}, H=${state.heightInput} ${state.heightUnit.symbol}")
            }
        } else {
            val missing = state.missingResult
            if (missing != null) {
                sb.appendLine("Find Missing ${missing.missingDimension.name.lowercase().replaceFirstChar { it.uppercase() }}:")
                sb.appendLine("Volume: ${BoxMeasureFormatter.formatWithUnit(missing.volume, state.resultVolumeUnit.symbol, decimals, rounding)}")
                sb.appendLine("Length: ${BoxMeasureFormatter.formatWithUnit(missing.length, state.resultLengthUnit.symbol, decimals, rounding)}")
                sb.appendLine("Width: ${BoxMeasureFormatter.formatWithUnit(missing.width, state.resultLengthUnit.symbol, decimals, rounding)}")
                sb.appendLine("Height: ${BoxMeasureFormatter.formatWithUnit(missing.height, state.resultLengthUnit.symbol, decimals, rounding)}")
            }
        }
        return sb.toString().trimEnd()
    }
}
