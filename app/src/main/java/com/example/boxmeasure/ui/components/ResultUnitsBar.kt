package com.example.boxmeasure.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.VolumeUnit

@Composable
fun ResultUnitsBar(
    selectedLengthUnit: LengthUnit,
    selectedAreaUnit: AreaUnit,
    selectedVolumeUnit: VolumeUnit,
    onLengthUnitClick: () -> Unit,
    onAreaUnitClick: () -> Unit,
    onVolumeUnitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "OUTPUT UNITS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = onLengthUnitClick,
                label = { Text("Length: ${selectedLengthUnit.symbol}") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Change length unit"
                    )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            )

            AssistChip(
                onClick = onAreaUnitClick,
                label = { Text("Area: ${selectedAreaUnit.symbol}") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Change area unit"
                    )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            )

            AssistChip(
                onClick = onVolumeUnitClick,
                label = { Text("Vol: ${selectedVolumeUnit.symbol}") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Change volume unit"
                    )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
