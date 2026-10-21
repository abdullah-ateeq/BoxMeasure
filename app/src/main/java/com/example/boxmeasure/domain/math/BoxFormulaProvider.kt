package com.example.boxmeasure.domain.math

import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.SolveMode

data class FormulaItem(
    val title: String,
    val formula: String,
    val description: String? = null
)

object BoxFormulaProvider {

    fun getFormulas(solveMode: SolveMode, missingDimension: DimensionType): List<FormulaItem> {
        return when (solveMode) {
            SolveMode.BOX_MEASUREMENTS -> listOf(
                FormulaItem("Volume", "V = L × W × H", "Inside capacity / 3D space occupied"),
                FormulaItem("Total Surface Area", "SA = 2(LW + LH + WH)", "Sum of all 6 faces"),
                FormulaItem("Lateral Surface Area", "LSA = 2H(L + W)", "Sum of 4 side faces (excluding top & bottom)"),
                FormulaItem("Face Areas", "LW (Top/Bottom), LH (Front/Back), WH (Left/Right)"),
                FormulaItem("Space Diagonal", "d = √(L² + W² + H²)", "Corner to opposite corner inside the box"),
                FormulaItem("Base Perimeter", "P = 2(L + W)", "Perimeter of bottom rectangle"),
                FormulaItem("Total Edge Length", "E = 4(L + W + H)", "Total length of all 12 edges")
            )
            SolveMode.FIND_MISSING_DIMENSION -> when (missingDimension) {
                DimensionType.LENGTH -> listOf(
                    FormulaItem("Find Length", "L = V / (W × H)", "Derived from V = L × W × H with known Width, Height, and Volume"),
                    FormulaItem("Base Area", "Area = W × H", "Cross-sectional area of known dimensions")
                )
                DimensionType.WIDTH -> listOf(
                    FormulaItem("Find Width", "W = V / (L × H)", "Derived from V = L × W × H with known Length, Height, and Volume"),
                    FormulaItem("Base Area", "Area = L × H", "Cross-sectional area of known dimensions")
                )
                DimensionType.HEIGHT -> listOf(
                    FormulaItem("Find Height", "H = V / (L × W)", "Derived from V = L × W × H with known Length, Width, and Volume"),
                    FormulaItem("Base Area", "Area = L × W", "Cross-sectional area of known dimensions")
                )
            }
        }
    }
}
