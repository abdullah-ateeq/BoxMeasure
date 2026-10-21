package com.example.boxmeasure.domain.math

import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.VolumeUnit
import java.math.BigDecimal
import java.math.MathContext

object LengthUnitConverter {
    fun toMeters(value: BigDecimal, fromUnit: LengthUnit): BigDecimal {
        return value.multiply(fromUnit.metersFactor)
    }

    fun fromMeters(valueInMeters: BigDecimal, toUnit: LengthUnit, mathContext: MathContext): BigDecimal {
        if (toUnit.metersFactor.compareTo(BigDecimal.ONE) == 0) {
            return valueInMeters
        }
        return valueInMeters.divide(toUnit.metersFactor, mathContext)
    }

    fun convert(
        value: BigDecimal,
        from: LengthUnit,
        to: LengthUnit,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        if (from == to) return value
        val inMeters = toMeters(value, from)
        return fromMeters(inMeters, to, mathContext)
    }
}

object AreaUnitConverter {
    fun toSquareMeters(value: BigDecimal, fromUnit: AreaUnit): BigDecimal {
        return value.multiply(fromUnit.squareMetersFactor)
    }

    fun fromSquareMeters(valueInSquareMeters: BigDecimal, toUnit: AreaUnit, mathContext: MathContext): BigDecimal {
        if (toUnit.squareMetersFactor.compareTo(BigDecimal.ONE) == 0) {
            return valueInSquareMeters
        }
        return valueInSquareMeters.divide(toUnit.squareMetersFactor, mathContext)
    }

    fun convert(
        value: BigDecimal,
        from: AreaUnit,
        to: AreaUnit,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        if (from == to) return value
        val inSquareMeters = toSquareMeters(value, from)
        return fromSquareMeters(inSquareMeters, to, mathContext)
    }
}

object VolumeUnitConverter {
    fun toCubicMeters(value: BigDecimal, fromUnit: VolumeUnit): BigDecimal {
        return value.multiply(fromUnit.cubicMetersFactor)
    }

    fun fromCubicMeters(valueInCubicMeters: BigDecimal, toUnit: VolumeUnit, mathContext: MathContext): BigDecimal {
        if (toUnit.cubicMetersFactor.compareTo(BigDecimal.ONE) == 0) {
            return valueInCubicMeters
        }
        return valueInCubicMeters.divide(toUnit.cubicMetersFactor, mathContext)
    }

    fun convert(
        value: BigDecimal,
        from: VolumeUnit,
        to: VolumeUnit,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        if (from == to) return value
        val inCubicMeters = toCubicMeters(value, from)
        return fromCubicMeters(inCubicMeters, to, mathContext)
    }
}
