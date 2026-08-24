package com.example.ui.screens.legal

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ContractPreferences
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandIndigo

@Composable
fun ResponsibleUseContractScreen(
    onAccepted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .testTag("responsible_use_contract_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 36.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "CONDICIONES DE USO RESPONSABLE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Para acceder a DataLotto, debes aceptar los siguientes términos de exención de responsabilidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }

                // Cita destacada (fondo gris claro, borde izquierdo rojo)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(68.dp)
                            .background(BrandDanger)
                    )
                    Text(
                        text = "Ninguna estrategia a largo plazo vence al azar. Las loterías y apuestas son juegos de probabilidad pura donde cada sorteo es independiente de los anteriores.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155),
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Section 1
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "1. Objeto de la Aplicación",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandDark
                    )
                    Text(
                        text = "DataLotto es un software destinado exclusivamente al entretenimiento, estudio probabilístico, análisis estadístico y visualización de datos históricos de sorteos oficiales de lotería. Bajo ninguna circunstancia este programa constituye un método infalible para asegurar premios financieros o alterar los resultados del azar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                        lineHeight = 17.sp
                    )
                }

                // Section 2
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "2. Advertencia sobre el Juego y Prevención de la Ludopatía",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandDark
                    )
                    BulletPoint(text = "Esta aplicación NO FOMENTA el juego ni la realización de apuestas reales.")
                    BulletPoint(text = "No garantizamos ganancias ni rentabilidad económica alguna. Ninguna de las funciones de análisis estadístico (Optimización de Filtros, sistemas múltiples, sistemas reducidos, regresión lineal o análisis de frecuencia) incrementa la probabilidad matemática de ganar frente al azar del bombo real.")
                    BulletPoint(text = "El juego compulsivo o patológico (ludopatía) es una enfermedad seria. Juega siempre con moderación, de forma recreativa, y solo con dinero que puedas permitirte perder íntegramente.")
                }

                // Section 3
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "3. Exoneración de Responsabilidad Legal",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandDark
                    )
                    Text(
                        text = "El usuario asume el 100% de la responsabilidad derivada de las decisiones de compra o apuestas que realice de forma externa en administraciones oficiales de lotería. Los creadores y desarrolladores de DataLotto quedan eximidos por completo de cualquier reclamación legal o indemnización por pérdidas financieras directas o indirectas derivadas del uso de esta herramienta.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                        lineHeight = 17.sp
                    )
                }

                // Section 4
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "4. Declaración de Cumplimiento Normativo",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandDark
                    )
                    Text(
                        text = "Al aceptar este acuerdo, declaras ser mayor de edad en tu jurisdicción correspondiente y utilizar el software exclusivamente como una herramienta educativa y de entretenimiento.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isChecked) Color(0xFFF1F5F9) else Color(0xFFF8FAFC))
                        .clickable { isChecked = !isChecked }
                        .padding(8.dp)
                        .testTag("contract_checkbox"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = BrandIndigo,
                            uncheckedColor = Color(0xFF94A3B8)
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "He leído, comprendo y acepto en su totalidad las condiciones de uso responsable, la advertencia de ludopatía y declaro entender que ninguna estrategia a largo plazo vence al azar y que esta app es para mero entretenimiento.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = BrandDark,
                        lineHeight = 16.sp
                    )
                }

                // Accept Button
                Button(
                    onClick = {
                        ContractPreferences.acceptContract(context)
                        onAccepted()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("contract_accept_button"),
                    enabled = isChecked,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandIndigo,
                        disabledContainerColor = Color(0xFFE2E8F0)
                    )
                ) {
                    Text(
                        text = "Aceptar y Continuar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isChecked) Color.White else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = BrandDanger,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF475569),
            lineHeight = 17.sp
        )
    }
}
