package com.example.boxmeasure.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.SolveMode
import com.example.boxmeasure.domain.model.VolumeUnit
import java.math.RoundingMode

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val decimalPlaces: Int = 6,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    val showFormula: Boolean = true,
    val showCalculationSteps: Boolean = true,
    val showBoxDiagram: Boolean = true,
    val showMoreDetails: Boolean = true,
    val confirmDelete: Boolean = true,
    val lastSolveMode: SolveMode = SolveMode.BOX_MEASUREMENTS,
    val lastMissingDimension: DimensionType = DimensionType.LENGTH,
    val lastLengthInput: String = "10",
    val lastWidthInput: String = "5",
    val lastHeightInput: String = "4",
    val lastVolumeInput: String = "",
    val lastLengthUnit: LengthUnit = LengthUnit.CENTIMETER,
    val lastWidthUnit: LengthUnit = LengthUnit.CENTIMETER,
    val lastHeightUnit: LengthUnit = LengthUnit.CENTIMETER,
    val lastVolumeInputUnit: VolumeUnit = VolumeUnit.CUBIC_CENTIMETER,
    val resultLengthUnit: LengthUnit = LengthUnit.CENTIMETER,
    val resultAreaUnit: AreaUnit = AreaUnit.SQUARE_CENTIMETER,
    val resultVolumeUnit: VolumeUnit = VolumeUnit.CUBIC_CENTIMETER
)

class SettingsRepository(
    private val prefs: SharedPreferences
) {
    constructor(context: Context) : this(
        context.getSharedPreferences("boxmeasure_settings", Context.MODE_PRIVATE)
    )

    fun loadSettings(): AppSettings {
        val themeStr = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        val theme = runCatching { AppTheme.valueOf(themeStr) }.getOrDefault(AppTheme.SYSTEM)

        val roundingStr = prefs.getString(KEY_ROUNDING_MODE, RoundingMode.HALF_UP.name) ?: RoundingMode.HALF_UP.name
        val rounding = runCatching { RoundingMode.valueOf(roundingStr) }.getOrDefault(RoundingMode.HALF_UP)

        val modeStr = prefs.getString(KEY_LAST_SOLVE_MODE, SolveMode.BOX_MEASUREMENTS.name) ?: SolveMode.BOX_MEASUREMENTS.name
        val mode = runCatching { SolveMode.valueOf(modeStr) }.getOrDefault(SolveMode.BOX_MEASUREMENTS)

        val missingStr = prefs.getString(KEY_LAST_MISSING_DIM, DimensionType.LENGTH.name) ?: DimensionType.LENGTH.name
        val missing = runCatching { DimensionType.valueOf(missingStr) }.getOrDefault(DimensionType.LENGTH)

        val lengthUnitStr = prefs.getString(KEY_LAST_LENGTH_UNIT, LengthUnit.CENTIMETER.name) ?: LengthUnit.CENTIMETER.name
        val lengthUnit = runCatching { LengthUnit.valueOf(lengthUnitStr) }.getOrDefault(LengthUnit.CENTIMETER)

        val widthUnitStr = prefs.getString(KEY_LAST_WIDTH_UNIT, LengthUnit.CENTIMETER.name) ?: LengthUnit.CENTIMETER.name
        val widthUnit = runCatching { LengthUnit.valueOf(widthUnitStr) }.getOrDefault(LengthUnit.CENTIMETER)

        val heightUnitStr = prefs.getString(KEY_LAST_HEIGHT_UNIT, LengthUnit.CENTIMETER.name) ?: LengthUnit.CENTIMETER.name
        val heightUnit = runCatching { LengthUnit.valueOf(heightUnitStr) }.getOrDefault(LengthUnit.CENTIMETER)

        val volumeInputUnitStr = prefs.getString(KEY_LAST_VOLUME_INPUT_UNIT, VolumeUnit.CUBIC_CENTIMETER.name) ?: VolumeUnit.CUBIC_CENTIMETER.name
        val volumeInputUnit = runCatching { VolumeUnit.valueOf(volumeInputUnitStr) }.getOrDefault(VolumeUnit.CUBIC_CENTIMETER)

        val resLengthUnitStr = prefs.getString(KEY_RES_LENGTH_UNIT, LengthUnit.CENTIMETER.name) ?: LengthUnit.CENTIMETER.name
        val resLengthUnit = runCatching { LengthUnit.valueOf(resLengthUnitStr) }.getOrDefault(LengthUnit.CENTIMETER)

        val resAreaUnitStr = prefs.getString(KEY_RES_AREA_UNIT, AreaUnit.SQUARE_CENTIMETER.name) ?: AreaUnit.SQUARE_CENTIMETER.name
        val resAreaUnit = runCatching { AreaUnit.valueOf(resAreaUnitStr) }.getOrDefault(AreaUnit.SQUARE_CENTIMETER)

        val resVolumeUnitStr = prefs.getString(KEY_RES_VOLUME_UNIT, VolumeUnit.CUBIC_CENTIMETER.name) ?: VolumeUnit.CUBIC_CENTIMETER.name
        val resVolumeUnit = runCatching { VolumeUnit.valueOf(resVolumeUnitStr) }.getOrDefault(VolumeUnit.CUBIC_CENTIMETER)

        return AppSettings(
            theme = theme,
            dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true),
            hapticFeedback = prefs.getBoolean(KEY_HAPTIC, true),
            animations = prefs.getBoolean(KEY_ANIMATIONS, true),
            decimalPlaces = prefs.getInt(KEY_DECIMALS, 6),
            roundingMode = rounding,
            showFormula = prefs.getBoolean(KEY_SHOW_FORMULA, true),
            showCalculationSteps = prefs.getBoolean(KEY_SHOW_STEPS, true),
            showBoxDiagram = prefs.getBoolean(KEY_SHOW_DIAGRAM, true),
            showMoreDetails = prefs.getBoolean(KEY_SHOW_MORE_DETAILS, true),
            confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, true),
            lastSolveMode = mode,
            lastMissingDimension = missing,
            lastLengthInput = prefs.getString(KEY_LAST_LENGTH_INPUT, "10") ?: "10",
            lastWidthInput = prefs.getString(KEY_LAST_WIDTH_INPUT, "5") ?: "5",
            lastHeightInput = prefs.getString(KEY_LAST_HEIGHT_INPUT, "4") ?: "4",
            lastVolumeInput = prefs.getString(KEY_LAST_VOLUME_INPUT, "") ?: "",
            lastLengthUnit = lengthUnit,
            lastWidthUnit = widthUnit,
            lastHeightUnit = heightUnit,
            lastVolumeInputUnit = volumeInputUnit,
            resultLengthUnit = resLengthUnit,
            resultAreaUnit = resAreaUnit,
            resultVolumeUnit = resVolumeUnit
        )
    }

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putString(KEY_THEME, settings.theme.name)
            .putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            .putBoolean(KEY_HAPTIC, settings.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, settings.animations)
            .putInt(KEY_DECIMALS, settings.decimalPlaces)
            .putString(KEY_ROUNDING_MODE, settings.roundingMode.name)
            .putBoolean(KEY_SHOW_FORMULA, settings.showFormula)
            .putBoolean(KEY_SHOW_STEPS, settings.showCalculationSteps)
            .putBoolean(KEY_SHOW_DIAGRAM, settings.showBoxDiagram)
            .putBoolean(KEY_SHOW_MORE_DETAILS, settings.showMoreDetails)
            .putBoolean(KEY_CONFIRM_DELETE, settings.confirmDelete)
            .putString(KEY_LAST_SOLVE_MODE, settings.lastSolveMode.name)
            .putString(KEY_LAST_MISSING_DIM, settings.lastMissingDimension.name)
            .putString(KEY_LAST_LENGTH_INPUT, settings.lastLengthInput)
            .putString(KEY_LAST_WIDTH_INPUT, settings.lastWidthInput)
            .putString(KEY_LAST_HEIGHT_INPUT, settings.lastHeightInput)
            .putString(KEY_LAST_VOLUME_INPUT, settings.lastVolumeInput)
            .putString(KEY_LAST_LENGTH_UNIT, settings.lastLengthUnit.name)
            .putString(KEY_LAST_WIDTH_UNIT, settings.lastWidthUnit.name)
            .putString(KEY_LAST_HEIGHT_UNIT, settings.lastHeightUnit.name)
            .putString(KEY_LAST_VOLUME_INPUT_UNIT, settings.lastVolumeInputUnit.name)
            .putString(KEY_RES_LENGTH_UNIT, settings.resultLengthUnit.name)
            .putString(KEY_RES_AREA_UNIT, settings.resultAreaUnit.name)
            .putString(KEY_RES_VOLUME_UNIT, settings.resultVolumeUnit.name)
            .apply()
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_THEME = "key_theme"
        private const val KEY_DYNAMIC_COLOR = "key_dynamic_color"
        private const val KEY_HAPTIC = "key_haptic"
        private const val KEY_ANIMATIONS = "key_animations"
        private const val KEY_DECIMALS = "key_decimals"
        private const val KEY_ROUNDING_MODE = "key_rounding_mode"
        private const val KEY_SHOW_FORMULA = "key_show_formula"
        private const val KEY_SHOW_STEPS = "key_show_steps"
        private const val KEY_SHOW_DIAGRAM = "key_show_diagram"
        private const val KEY_SHOW_MORE_DETAILS = "key_show_more_details"
        private const val KEY_CONFIRM_DELETE = "key_confirm_delete"
        private const val KEY_LAST_SOLVE_MODE = "key_last_solve_mode"
        private const val KEY_LAST_MISSING_DIM = "key_last_missing_dim"
        private const val KEY_LAST_LENGTH_INPUT = "key_last_length_input"
        private const val KEY_LAST_WIDTH_INPUT = "key_last_width_input"
        private const val KEY_LAST_HEIGHT_INPUT = "key_last_height_input"
        private const val KEY_LAST_VOLUME_INPUT = "key_last_volume_input"
        private const val KEY_LAST_LENGTH_UNIT = "key_last_length_unit"
        private const val KEY_LAST_WIDTH_UNIT = "key_last_width_unit"
        private const val KEY_LAST_HEIGHT_UNIT = "key_last_height_unit"
        private const val KEY_LAST_VOLUME_INPUT_UNIT = "key_last_volume_input_unit"
        private const val KEY_RES_LENGTH_UNIT = "key_res_length_unit"
        private const val KEY_RES_AREA_UNIT = "key_res_area_unit"
        private const val KEY_RES_VOLUME_UNIT = "key_res_volume_unit"
    }
}
