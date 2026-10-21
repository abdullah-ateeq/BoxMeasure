package com.example.boxmeasure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.boxmeasure.data.repository.AppTheme
import com.example.boxmeasure.ui.home.BoxMeasureViewModel
import com.example.boxmeasure.ui.navigation.BoxMeasureApp
import com.example.boxmeasure.ui.theme.BoxMeasureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: BoxMeasureViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()

            val isDarkTheme = when (settings.theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            BoxMeasureTheme(
                darkTheme = isDarkTheme,
                dynamicColor = settings.dynamicColor
            ) {
                BoxMeasureApp(viewModel = viewModel)
            }
        }
    }
}
