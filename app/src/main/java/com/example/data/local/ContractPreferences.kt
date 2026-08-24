package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object ContractPreferences {
    private const val PREFS_NAME = "datalotto_contract_prefs"
    private const val KEY_ACCEPTED = "contract_accepted"
    private const val KEY_SIG_ID = "contract_sig_id"
    private const val KEY_SIG_DATE = "contract_sig_date"
    private const val KEY_ANON_ID = "contract_anon_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isAccepted(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ACCEPTED, false)
    }

    fun getOrCreateAnonId(context: Context): String {
        val prefs = getPrefs(context)
        val existingId = prefs.getString(KEY_ANON_ID, null)
        if (!existingId.isNullOrEmpty()) {
            return existingId
        }

        val chars = "0123456789abcdefghijklmnopqrstuvwxyz"
        val randomStr = (1..8)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
        val newAnonId = "usr_$randomStr"

        prefs.edit().putString(KEY_ANON_ID, newAnonId).apply()
        return newAnonId
    }

    fun getSigId(context: Context): String {
        return getPrefs(context).getString(KEY_SIG_ID, "") ?: ""
    }

    fun getSigDate(context: Context): String {
        return getPrefs(context).getString(KEY_SIG_DATE, "") ?: ""
    }

    fun acceptContract(context: Context) {
        val anonId = getOrCreateAnonId(context)

        val upperChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val random6 = (1..6)
            .map { upperChars[Random.nextInt(upperChars.length)] }
            .joinToString("")
        val last4Digits = (System.currentTimeMillis() % 10000).toString().padStart(4, '0')
        val sigId = "REG-$random6-$last4Digits"

        val dateFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale("es", "ES"))
        val sigDate = dateFormat.format(Date())

        getPrefs(context).edit()
            .putBoolean(KEY_ACCEPTED, true)
            .putString(KEY_SIG_ID, sigId)
            .putString(KEY_SIG_DATE, sigDate)
            .apply()
    }

    fun getLogText(context: Context): String {
        val sigId = getSigId(context).ifEmpty { "NO_REGISTRADO" }
        val sigDate = getSigDate(context).ifEmpty { "NO_REGISTRADO" }
        val anonId = getOrCreateAnonId(context)

        return """
================================================================================
REGISTRO DE ACEPTACIÓN DE CONDICIONES DE USO RESPONSABLE
DataLotto — Software de Análisis Estadístico y Probabilístico
================================================================================

ID de Firma:           $sigId
Fecha de Aceptación:   $sigDate
ID de Dispositivo:     $anonId
Estado:                ACEPTADO Y FIRMADO DIGITALMENTE
Versión del Acuerdo:   1.0.0

--------------------------------------------------------------------------------
TÉRMINOS ACEPTADOS:
--------------------------------------------------------------------------------
1. Objeto de la Aplicación:
   DataLotto es un software destinado exclusivamente al entretenimiento,
   estudio probabilístico, análisis estadístico y visualización de datos
   históricos de sorteos oficiales de lotería. Bajo ninguna circunstancia
   este programa constituye un método infalible para asegurar premios
   financieros o alterar los resultados del azar.

2. Advertencia sobre el Juego y Prevención de la Ludopatía:
   - Esta aplicación NO FOMENTA el juego ni la realización de apuestas reales.
   - No garantizamos ganancias ni rentabilidad económica alguna. Ninguna de
     las funciones de análisis estadístico (Optimización de Filtros, sistemas
     múltiples, sistemas reducidos, regresión lineal o análisis de frecuencia)
     incrementa la probabilidad matemática de ganar frente al azar del bombo real.
   - El juego compulsivo o patológico (ludopatía) es una enfermedad seria.
     Juega siempre con moderación, de forma recreativa, y solo con dinero
     que puedas permitirte perder íntegramente.

3. Exoneración de Responsabilidad Legal:
   El usuario asume el 100% de la responsabilidad derivada de las decisiones
   de compra o apuestas que realice de forma externa en administraciones
   oficiales de lotería. Los creadores y desarrolladores de DataLotto quedan
   eximidos por completo de cualquier reclamación legal o indemnización por
   pérdidas financieras directas o indirectas derivadas del uso de esta herramienta.

4. Declaración de Cumplimiento Normativo:
   Al aceptar este acuerdo, declaras ser mayor de edad en tu jurisdicción
   correspondiente y utilizar el software exclusivamente como una herramienta
   educativa y de entretenimiento.

================================================================================
Línea de ayuda contra la ludopatía (España): 900 200 225 (FEJAR / Gratuito)
================================================================================
""".trimIndent()
    }
}
