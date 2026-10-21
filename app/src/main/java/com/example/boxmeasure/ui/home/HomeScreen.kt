package com.example.boxmeasure.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.SolveMode
import com.example.boxmeasure.domain.model.VolumeUnit
import com.example.boxmeasure.ui.components.BoxDiagram
import com.example.boxmeasure.ui.components.CalculationStepsCard
import com.example.boxmeasure.ui.components.DimensionInputField
import com.example.boxmeasure.ui.components.FormulasCard
import com.example.boxmeasure.ui.components.MeasurementsCard
import com.example.boxmeasure.ui.components.MissingDimensionResultCard
import com.example.boxmeasure.ui.components.MoreBoxDetailsCard
import com.example.boxmeasure.ui.components.PrimaryResultCard
import com.example.boxmeasure.ui.components.ResultUnitsBar
import com.example.boxmeasure.ui.components.SurfaceAreaCard
import com.example.boxmeasure.ui.components.UnitSelectionBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BoxMeasureViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Unit selector bottom sheets state
    var unitSheetType by remember { mutableStateOf<String?>(null) }

    fun performHaptic(isError: Boolean = false) {
        if (!settings.hapticFeedback) return
        if (isError) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun copyToClipboard(text: String, message: String = "Copied to clipboard") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("BoxMeasure", text)
        clipboard.setPrimaryClip(clip)
        performHaptic(isError = false)
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun shareCalculation() {
        val shareText = viewModel.buildShareText()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Box Measurements")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        performHaptic(isError = false)
        context.startActivity(Intent.createChooser(intent, "Share box measurements"))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BoxMeasure",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Calculate box volume, surface area, and dimensions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            performHaptic(isError = false)
                            viewModel.resetToDefaults()
                            scope.launch { snackbarHostState.showSnackbar("Reset to default dimensions") }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset values")
                    }
                    IconButton(
                        onClick = { shareCalculation() }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share calculation")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Selector
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CALCULATOR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = uiState.solveMode == SolveMode.BOX_MEASUREMENTS,
                        onClick = {
                            performHaptic(isError = false)
                            viewModel.setSolveMode(SolveMode.BOX_MEASUREMENTS)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(
                            text = "Box Measurements",
                            fontWeight = if (uiState.solveMode == SolveMode.BOX_MEASUREMENTS) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    SegmentedButton(
                        selected = uiState.solveMode == SolveMode.FIND_MISSING_DIMENSION,
                        onClick = {
                            performHaptic(isError = false)
                            viewModel.setSolveMode(SolveMode.FIND_MISSING_DIMENSION)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(
                            text = "Missing Dimension",
                            fontWeight = if (uiState.solveMode == SolveMode.FIND_MISSING_DIMENSION) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Missing Dimension Selector Cards
            if (uiState.solveMode == SolveMode.FIND_MISSING_DIMENSION) {
                item {
                    Text(
                        text = "FIND MISSING DIMENSION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MissingDimensionChoiceCard(
                            label = "LENGTH",
                            subtitle = "W + H + Vol",
                            isSelected = uiState.missingDimension == DimensionType.LENGTH,
                            onClick = {
                                performHaptic(isError = false)
                                viewModel.setMissingDimension(DimensionType.LENGTH)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        MissingDimensionChoiceCard(
                            label = "WIDTH",
                            subtitle = "L + H + Vol",
                            isSelected = uiState.missingDimension == DimensionType.WIDTH,
                            onClick = {
                                performHaptic(isError = false)
                                viewModel.setMissingDimension(DimensionType.WIDTH)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        MissingDimensionChoiceCard(
                            label = "HEIGHT",
                            subtitle = "L + W + Vol",
                            isSelected = uiState.missingDimension == DimensionType.HEIGHT,
                            onClick = {
                                performHaptic(isError = false)
                                viewModel.setMissingDimension(DimensionType.HEIGHT)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Box Diagram
            if (settings.showBoxDiagram) {
                item {
                    BoxDiagram(
                        lengthStr = uiState.lengthInput,
                        widthStr = uiState.widthInput,
                        heightStr = uiState.heightInput,
                        lengthUnitSymbol = uiState.lengthUnit.symbol,
                        widthUnitSymbol = uiState.widthUnit.symbol,
                        heightUnitSymbol = uiState.heightUnit.symbol,
                        animated = settings.animations
                    )
                }
            }

            // Inputs section
            item {
                Text(
                    text = "DIMENSION INPUTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (uiState.solveMode == SolveMode.BOX_MEASUREMENTS) {
                        DimensionInputField(
                            label = "Length (L)",
                            value = uiState.lengthInput,
                            unitSymbol = uiState.lengthUnit.symbol,
                            onValueChange = { viewModel.setLengthInput(it) },
                            onUnitClick = { unitSheetType = "LENGTH_IN" },
                            contentDescriptionText = "Box length value."
                        )
                        DimensionInputField(
                            label = "Width (W)",
                            value = uiState.widthInput,
                            unitSymbol = uiState.widthUnit.symbol,
                            onValueChange = { viewModel.setWidthInput(it) },
                            onUnitClick = { unitSheetType = "WIDTH_IN" },
                            contentDescriptionText = "Box width value."
                        )
                        DimensionInputField(
                            label = "Height (H)",
                            value = uiState.heightInput,
                            unitSymbol = uiState.heightUnit.symbol,
                            onValueChange = { viewModel.setHeightInput(it) },
                            onUnitClick = { unitSheetType = "HEIGHT_IN" },
                            contentDescriptionText = "Box height value."
                        )
                    } else {
                        // Find Missing Dimension Mode
                        DimensionInputField(
                            label = "Known Volume (V)",
                            value = uiState.volumeInput,
                            unitSymbol = uiState.volumeInputUnit.symbol,
                            onValueChange = { viewModel.setVolumeInput(it) },
                            onUnitClick = { unitSheetType = "VOLUME_IN" },
                            contentDescriptionText = "Box volume value."
                        )

                        if (uiState.missingDimension != DimensionType.LENGTH) {
                            DimensionInputField(
                                label = "Known Length (L)",
                                value = uiState.lengthInput,
                                unitSymbol = uiState.lengthUnit.symbol,
                                onValueChange = { viewModel.setLengthInput(it) },
                                onUnitClick = { unitSheetType = "LENGTH_IN" },
                                contentDescriptionText = "Box length value."
                            )
                        }

                        if (uiState.missingDimension != DimensionType.WIDTH) {
                            DimensionInputField(
                                label = "Known Width (W)",
                                value = uiState.widthInput,
                                unitSymbol = uiState.widthUnit.symbol,
                                onValueChange = { viewModel.setWidthInput(it) },
                                onUnitClick = { unitSheetType = "WIDTH_IN" },
                                contentDescriptionText = "Box width value."
                            )
                        }

                        if (uiState.missingDimension != DimensionType.HEIGHT) {
                            DimensionInputField(
                                label = "Known Height (H)",
                                value = uiState.heightInput,
                                unitSymbol = uiState.heightUnit.symbol,
                                onValueChange = { viewModel.setHeightInput(it) },
                                onUnitClick = { unitSheetType = "HEIGHT_IN" },
                                contentDescriptionText = "Box height value."
                            )
                        }
                    }
                }
            }

            // Output Units Bar
            item {
                ResultUnitsBar(
                    selectedLengthUnit = uiState.resultLengthUnit,
                    selectedAreaUnit = uiState.resultAreaUnit,
                    selectedVolumeUnit = uiState.resultVolumeUnit,
                    onLengthUnitClick = { unitSheetType = "RES_LENGTH" },
                    onAreaUnitClick = { unitSheetType = "RES_AREA" },
                    onVolumeUnitClick = { unitSheetType = "RES_VOLUME" }
                )
            }

            // Calculate Button and Action Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.calculate(saveInputs = true)
                            if (uiState.errorMessage != null) {
                                performHaptic(isError = true)
                            } else {
                                performHaptic(isError = false)
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(text = "CALCULATE", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val saved = viewModel.saveCalculation()
                            if (saved) {
                                performHaptic(isError = false)
                                scope.launch { snackbarHostState.showSnackbar("Calculation saved to history") }
                            } else {
                                scope.launch { snackbarHostState.showSnackbar("Already saved in history") }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(text = "SAVE", fontWeight = FontWeight.SemiBold)
                    }

                    FilledTonalButton(
                        onClick = {
                            val fullText = viewModel.buildShareText()
                            copyToClipboard(fullText, "Full box calculation copied")
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy all"
                        )
                    }
                }
            }

            // Error banner if any
            if (uiState.errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            // Results Section
            if (uiState.solveMode == SolveMode.BOX_MEASUREMENTS) {
                val result = uiState.result
                if (result != null) {
                    item {
                        PrimaryResultCard(
                            result = result,
                            lengthUnit = uiState.resultLengthUnit,
                            areaUnit = uiState.resultAreaUnit,
                            volumeUnit = uiState.resultVolumeUnit,
                            decimals = settings.decimalPlaces,
                            roundingMode = settings.roundingMode,
                            onCopyValue = { copyToClipboard(it) }
                        )
                    }

                    item {
                        MeasurementsCard(
                            length = result.length,
                            width = result.width,
                            height = result.height,
                            lengthUnit = uiState.resultLengthUnit,
                            decimals = settings.decimalPlaces,
                            roundingMode = settings.roundingMode
                        )
                    }

                    item {
                        SurfaceAreaCard(
                            result = result,
                            areaUnit = uiState.resultAreaUnit,
                            decimals = settings.decimalPlaces,
                            roundingMode = settings.roundingMode
                        )
                    }

                    if (settings.showMoreDetails) {
                        item {
                            MoreBoxDetailsCard(
                                result = result,
                                lengthUnit = uiState.resultLengthUnit,
                                areaUnit = uiState.resultAreaUnit,
                                decimals = settings.decimalPlaces,
                                roundingMode = settings.roundingMode
                            )
                        }
                    }

                    if (settings.showFormula) {
                        item {
                            FormulasCard(
                                solveMode = uiState.solveMode,
                                missingDimension = uiState.missingDimension
                            )
                        }
                    }

                    if (settings.showCalculationSteps) {
                        item {
                            CalculationStepsCard(steps = result.calculationSteps)
                        }
                    }
                }
            } else {
                // Find Missing Dimension Result
                val missingResult = uiState.missingResult
                if (missingResult != null) {
                    item {
                        MissingDimensionResultCard(
                            result = missingResult,
                            lengthUnit = uiState.resultLengthUnit,
                            volumeUnit = uiState.resultVolumeUnit,
                            decimals = settings.decimalPlaces,
                            roundingMode = settings.roundingMode,
                            onCopyValue = { copyToClipboard(it) }
                        )
                    }

                    item {
                        MeasurementsCard(
                            length = missingResult.length,
                            width = missingResult.width,
                            height = missingResult.height,
                            lengthUnit = uiState.resultLengthUnit,
                            decimals = settings.decimalPlaces,
                            roundingMode = settings.roundingMode
                        )
                    }

                    if (settings.showFormula) {
                        item {
                            FormulasCard(
                                solveMode = uiState.solveMode,
                                missingDimension = uiState.missingDimension
                            )
                        }
                    }

                    if (settings.showCalculationSteps) {
                        item {
                            CalculationStepsCard(steps = missingResult.calculationSteps)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Bottom Sheets for Unit Selection
    when (unitSheetType) {
        "LENGTH_IN" -> UnitSelectionBottomSheet(
            title = "Select Length Unit",
            items = LengthUnit.entries,
            selectedItem = uiState.lengthUnit,
            itemLabel = { "${it.displayName} (${it.symbol})" },
            itemDescription = { "1 ${it.symbol} = ${it.metersFactor.stripTrailingZeros().toPlainString()} m" },
            onItemSelected = { viewModel.setLengthUnit(it) },
            onDismissRequest = { unitSheetType = null }
        )
        "WIDTH_IN" -> UnitSelectionBottomSheet(
            title = "Select Width Unit",
            items = LengthUnit.entries,
            selectedItem = uiState.widthUnit,
            itemLabel = { "${it.displayName} (${it.symbol})" },
            itemDescription = { "1 ${it.symbol} = ${it.metersFactor.stripTrailingZeros().toPlainString()} m" },
            onItemSelected = { viewModel.setWidthUnit(it) },
            onDismissRequest = { unitSheetType = null }
        )
        "HEIGHT_IN" -> UnitSelectionBottomSheet(
            title = "Select Height Unit",
            items = LengthUnit.entries,
            selectedItem = uiState.heightUnit,
            itemLabel = { "${it.displayName} (${it.symbol})" },
            itemDescription = { "1 ${it.symbol} = ${it.metersFactor.stripTrailingZeros().toPlainString()} m" },
            onItemSelected = { viewModel.setHeightUnit(it) },
            onDismissRequest = { unitSheetType = null }
        )
        "VOLUME_IN" -> UnitSelectionBottomSheet(
            title = "Select Volume Input Unit",
            items = VolumeUnit.entries,
            selectedItem = uiState.volumeInputUnit,
            itemLabel = { "${it.displayName} (${it.symbol})" },
            itemDescription = { "1 ${it.symbol} = ${it.cubicMetersFactor.stripTrailingZeros().toPlainString()} m³" },
            onItemSelected = { viewModel.setVolumeInputUnit(it) },
            onDismissRequest = { unitSheetType = null }
        )
        "RES_LENGTH" -> UnitSelectionBottomSheet(
            title = "Select Output Length Unit",
            items = LengthUnit.entries,
            selectedItem = uiState.resultLengthUnit,
            itemLabel = { "${it.displayName} (${it.symbol})" },
            itemDescription = { "Display dimensions, perimeter, diagonal in ${it.symbol}" },
            onItemSelected = { viewModel.setResultLengthUnit(it) },
            onDismissRequest = { unitSheetType = null }
        )
        "RES_AREA" -> UnitSelectionBottomSheet(
            title = "Select Output Area Unit",
            items = AreaUnit.entries,
            selectedItem = uiState.resultAreaUnit,
            itemLabel = { "${it.displayName} (${it.symbol})" },
            itemDescription = { "Display surface & face areas in ${it.symbol}" },
            onItemSelected = { viewModel.setResultAreaUnit(it) },
            onDismissRequest = { unitSheetType = null }
        )
        "RES_VOLUME" -> UnitSelectionBottomSheet(
            title = "Select Output Volume Unit",
            items = VolumeUnit.entries,
            selectedItem = uiState.resultVolumeUnit,
            itemLabel = { "${it.displayName} (${it.symbol})" },
            itemDescription = { "Display box volume in ${it.symbol}" },
            onItemSelected = { viewModel.setResultVolumeUnit(it) },
            onDismissRequest = { unitSheetType = null }
        )
    }
}

@Composable
private fun MissingDimensionChoiceCard(
    label: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}
