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
REGISTRO DE CONFORMIDAD - DATALOTTO
================================================================================

ID DE REGISTRO:      $sigId
FECHA Y HORA:        $sigDate
ESTADO DE REGISTRO:  ACEPTADO Y FIRMADO DIGITALMENTE
ID DE DISPOSITIVO:   $anonId

CLÁUSULAS DE CONFORMIDAD Y EXENCIÓN DE RESPONSABILIDAD ACEPTADAS:
1. El usuario reconoce expresamente que DataLotto es una herramienta de análisis estadístico y entretenimiento.
2. El usuario comprende y acepta que los juegos de azar son impredecibles y que ninguna estrategia o análisis garantiza premios.
3. El usuario asume toda la responsabilidad sobre el uso de los datos y cualquier decisión económica o de juego.
4. El usuario declara ser mayor de edad legal para participar en juegos de azar en su jurisdicción.
5. El usuario se compromete a realizar un juego responsable y conoce los riesgos asociados a la ludopatía.

================================================================================
""".trimIndent()
    }
}
