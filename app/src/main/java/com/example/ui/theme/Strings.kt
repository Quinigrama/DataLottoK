package com.example.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun tr(es: String, en: String): String {
    return if (LocalAppLocale.current == "en") en else es
}
