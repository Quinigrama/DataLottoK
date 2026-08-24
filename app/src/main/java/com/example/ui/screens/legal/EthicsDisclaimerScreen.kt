package com.example.ui.screens.legal

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ContractPreferences
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandGradientEnd
import com.example.ui.theme.BrandGradientStart
import com.example.ui.theme.BrandIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EthicsDisclaimerScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showLogDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandGradientStart, BrandGradientEnd),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("disclaimer_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = BrandDark
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "⚠️ Disclaimer Ético",
                            fontWeight = FontWeight.Bold,
                            color = BrandDark
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.shadow(4.dp)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .testTag("ethics_disclaimer_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // IMPORTANTE section
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "IMPORTANTE:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandDanger
                            )
                            Text(
                                text = "Esta aplicación es una herramienta de entretenimiento y análisis estadístico. Los números de lotería son completamente aleatorios. Ninguna estrategia a largo plazo vence al azar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF334155),
                                lineHeight = 20.sp
                            )
                        }

                        // Recuerda section (9 points)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Recuerda:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandDark
                            )

                            val rememberPoints = listOf(
                                "No hay forma de predecir números ganadores",
                                "Juega solo lo que puedas permitirte perder",
                                "El juego puede crear adicción",
                                "Esta app no garantiza premios",
                                "Los algoritmos son para análisis, no predicción",
                                "Los datos históricos no predicen futuros resultados",
                                "Los filtros matemáticos solo organizan probabilidades",
                                "Las estrategias no aumentan las posibilidades reales de ganar",
                                "Ninguna estrategia a largo plazo vence al azar."
                            )

                            rememberPoints.forEachIndexed { index, point ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "•",
                                        color = if (index == 8) BrandDanger else Color(0xFF64748B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = point,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (index == 8) FontWeight.Bold else FontWeight.Normal,
                                        color = if (index == 8) BrandDanger else Color(0xFF475569),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        // Responsabilidad section
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Responsabilidad:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandDark
                            )
                            Text(
                                text = "El usuario es completamente responsable de sus decisiones de juego. Esta herramienta no fomenta el juego compulsivo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF334155),
                                lineHeight = 19.sp
                            )
                        }

                        // Ayuda section
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Ayuda:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandDark
                            )
                            Text(
                                text = "Si tienes problemas con el juego, busca ayuda profesional. En España: 900 200 225 (Línea de ayuda contra la ludopatía)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF334155),
                                lineHeight = 19.sp
                            )
                        }

                        // Caja de aviso (fondo rojo claro #fef2f2, borde izquierdo rojo)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEF2F2))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(72.dp)
                                    .background(BrandDanger)
                            )
                            Text(
                                text = "AVISO LEGAL: Esta aplicación es solo para entretenimiento. Los desarrolladores no se responsabilizan por pérdidas económicas derivadas del uso de esta herramienta. Recuerda que ninguna estrategia a largo plazo vence al azar.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF991B1B),
                                lineHeight = 17.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        // Botón Registro de Aceptación
                        Button(
                            onClick = { showLogDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("view_contract_log_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandIndigo
                            )
                        ) {
                            Text(
                                text = "📜 Registro de Aceptación",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Dialog for Signed Contract Log
        if (showLogDialog) {
            val logContent = remember { ContractPreferences.getLogText(context) }
            val dialogScrollState = rememberScrollState()

            AlertDialog(
                onDismissRequest = { showLogDialog = false },
                title = {
                    Text(
                        text = "📜 Registro de Aceptación",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandDark
                    )
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .verticalScroll(dialogScrollState)
                    ) {
                        Text(
                            text = logContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 15.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(logContent))
                            Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogDialog = false },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cerrar", color = Color(0xFF64748B))
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}
