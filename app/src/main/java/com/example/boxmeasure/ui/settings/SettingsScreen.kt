package com.example.boxmeasure.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.boxmeasure.data.repository.AppTheme
import com.example.boxmeasure.ui.home.BoxMeasureViewModel
import kotlinx.coroutines.launch
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: BoxMeasureViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }
    var showDecimalPicker by remember { mutableStateOf(false) }
    var showRoundingPicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
            // Appearance
            item {
                SectionHeader("APPEARANCE")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingClickableRow(
                            title = "Theme",
                            value = when (settings.theme) {
                                AppTheme.SYSTEM -> "System default"
                                AppTheme.LIGHT -> "Light"
                                AppTheme.DARK -> "Dark"
                            },
                            onClick = { showThemePicker = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        SettingSwitchRow(
                            title = "Dynamic Color",
                            subtitle = "Use Material You dynamic accent colors",
                            checked = settings.dynamicColor,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(dynamicColor = it)) }
                        )
                    }
                }
            }

            // Behavior
            item {
                SectionHeader("BEHAVIOR")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingSwitchRow(
                            title = "Haptic Feedback",
                            subtitle = "Vibrate on calculation and mode actions",
                            checked = settings.hapticFeedback,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(hapticFeedback = it)) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        SettingSwitchRow(
                            title = "Animations",
                            subtitle = "Smooth 3D box and transition animations",
                            checked = settings.animations,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(animations = it)) }
                        )
                    }
                }
            }

            // Number Display
            item {
                SectionHeader("NUMBER DISPLAY")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingClickableRow(
                            title = "Decimal Places",
                            value = "${settings.decimalPlaces} places (default: 6)",
                            onClick = { showDecimalPicker = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        SettingClickableRow(
                            title = "Rounding Mode",
                            value = settings.roundingMode.name.replace("_", " "),
                            onClick = { showRoundingPicker = true }
                        )
                    }
                }
            }

            // Display Details
            item {
                SectionHeader("DISPLAY")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingSwitchRow(
                            title = "Show Box Diagram",
                            subtitle = "Visual vector cuboid with proportional scaling",
                            checked = settings.showBoxDiagram,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(showBoxDiagram = it)) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        SettingSwitchRow(
                            title = "Show Formulas",
                            subtitle = "Geometric formulas relevant to active mode",
                            checked = settings.showFormula,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(showFormula = it)) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        SettingSwitchRow(
                            title = "Show Calculation Steps",
                            subtitle = "Step-by-step substitution and solution details",
                            checked = settings.showCalculationSteps,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(showCalculationSteps = it)) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        SettingSwitchRow(
                            title = "Show More Details",
                            subtitle = "Lateral surface area, base perimeter, edge length",
                            checked = settings.showMoreDetails,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(showMoreDetails = it)) }
                        )
                    }
                }
            }

            // History & Data Management
            item {
                SectionHeader("HISTORY & STORAGE")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingSwitchRow(
                            title = "Confirm Delete",
                            subtitle = "Ask before deleting saved calculations",
                            checked = settings.confirmDelete,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(confirmDelete = it)) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClearHistoryDialog = true }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Clear History",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showResetSettingsDialog = true }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reset Settings",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Theme Dialog
    if (showThemePicker) {
        AlertDialog(
            onDismissRequest = { showThemePicker = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    AppTheme.entries.forEach { themeOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateSettings(settings.copy(theme = themeOption))
                                    showThemePicker = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (themeOption) {
                                    AppTheme.SYSTEM -> "System default"
                                    AppTheme.LIGHT -> "Light"
                                    AppTheme.DARK -> "Dark"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (settings.theme == themeOption) FontWeight.Bold else FontWeight.Normal,
                                color = if (settings.theme == themeOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showThemePicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Decimal Picker Dialog
    if (showDecimalPicker) {
        val decimalsOptions = listOf(0, 1, 2, 3, 4, 6, 8, 10, 12)
        AlertDialog(
            onDismissRequest = { showDecimalPicker = false },
            title = { Text("Decimal Places") },
            text = {
                Column {
                    decimalsOptions.forEach { decimals ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateSettings(settings.copy(decimalPlaces = decimals))
                                    showDecimalPicker = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$decimals decimal places${if (decimals == 6) " (default)" else ""}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (settings.decimalPlaces == decimals) FontWeight.Bold else FontWeight.Normal,
                                color = if (settings.decimalPlaces == decimals) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDecimalPicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Rounding Mode Dialog
    if (showRoundingPicker) {
        val roundingModes = listOf(
            RoundingMode.HALF_UP,
            RoundingMode.HALF_DOWN,
            RoundingMode.HALF_EVEN,
            RoundingMode.UP,
            RoundingMode.DOWN,
            RoundingMode.CEILING,
            RoundingMode.FLOOR
        )
        AlertDialog(
            onDismissRequest = { showRoundingPicker = false },
            title = { Text("Rounding Mode") },
            text = {
                Column {
                    roundingModes.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateSettings(settings.copy(roundingMode = mode))
                                    showRoundingPicker = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mode.name.replace("_", " ") + if (mode == RoundingMode.HALF_UP) " (default)" else "",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (settings.roundingMode == mode) FontWeight.Bold else FontWeight.Normal,
                                color = if (settings.roundingMode == mode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRoundingPicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Clear History Confirmation
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Clear all saved box calculations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearHistoryDialog = false
                        scope.launch { snackbarHostState.showSnackbar("All history cleared") }
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Settings Confirmation
    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Reset all preferences to default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetSettingsDialog = false
                        scope.launch { snackbarHostState.showSnackbar("Settings reset to defaults") }
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingClickableRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}
