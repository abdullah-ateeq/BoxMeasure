package com.example.boxmeasure.domain.model

import java.math.BigDecimal

enum class LengthUnit(
    val symbol: String,
    val displayName: String,
    val metersFactor: BigDecimal
) {
    MILLIMETER("mm", "Millimeter", BigDecimal("0.001")),
    CENTIMETER("cm", "Centimeter", BigDecimal("0.01")),
    METER("m", "Meter", BigDecimal("1")),
    KILOMETER("km", "Kilometer", BigDecimal("1000")),
    INCH("in", "Inch", BigDecimal("0.0254")),
    FOOT("ft", "Foot", BigDecimal("0.3048")),
    YARD("yd", "Yard", BigDecimal("0.9144"));

    override fun toString(): String = symbol
}

enum class AreaUnit(
    val symbol: String,
    val displayName: String,
    val squareMetersFactor: BigDecimal
) {
    SQUARE_MILLIMETER("mm²", "Square Millimeter", BigDecimal("0.000001")),
    SQUARE_CENTIMETER("cm²", "Square Centimeter", BigDecimal("0.0001")),
    SQUARE_METER("m²", "Square Meter", BigDecimal("1")),
    SQUARE_KILOMETER("km²", "Square Kilometer", BigDecimal("1000000")),
    SQUARE_INCH("in²", "Square Inch", BigDecimal("0.00064516")),
    SQUARE_FOOT("ft²", "Square Foot", BigDecimal("0.09290304")),
    SQUARE_YARD("yd²", "Square Yard", BigDecimal("0.83612736"));

    override fun toString(): String = symbol
}

enum class VolumeUnit(
    val symbol: String,
    val displayName: String,
    val cubicMetersFactor: BigDecimal
) {
    CUBIC_MILLIMETER("mm³", "Cubic Millimeter", BigDecimal("0.000000001")),
    CUBIC_CENTIMETER("cm³", "Cubic Centimeter", BigDecimal("0.000001")),
    CUBIC_METER("m³", "Cubic Meter", BigDecimal("1")),
    CUBIC_KILOMETER("km³", "Cubic Kilometer", BigDecimal("1000000000")),
    CUBIC_INCH("in³", "Cubic Inch", BigDecimal("0.000016387064")),
    CUBIC_FOOT("ft³", "Cubic Foot", BigDecimal("0.028316846592")),
    CUBIC_YARD("yd³", "Cubic Yard", BigDecimal("0.764554857984"));

    override fun toString(): String = symbol
}
