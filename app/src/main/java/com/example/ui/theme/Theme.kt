package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MasterNotesTheme(
    appStyleTheme: AppStyleTheme = AppStyleTheme.OXFORD_BLUE,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = appStyleTheme.toColorScheme(),
        typography = Typography,
        content = content
    )
}

