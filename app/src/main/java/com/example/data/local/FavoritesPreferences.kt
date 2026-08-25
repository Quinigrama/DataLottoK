package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

object FavoritesPreferences {
    private const val PREFS_NAME = "favorites_prefs"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getFavorites(context: Context, gameId: String, isStar: Boolean = false): Set<Int> {
        val key = "fav_${gameId}_${if (isStar) "star" else "main"}"
        val stringSet = getPrefs(context).getStringSet(key, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun toggleFavorite(context: Context, gameId: String, number: Int, isStar: Boolean = false): Boolean {
        val current = getFavorites(context, gameId, isStar).toMutableSet()
        val key = "fav_${gameId}_${if (isStar) "star" else "main"}"
        if (current.contains(number)) {
            current.remove(number)
        } else {
            if (current.size >= 10) return false // Límite de 10
            current.add(number)
        }
        val stringSet = current.map { it.toString() }.toSet()
        getPrefs(context).edit().putStringSet(key, stringSet).apply()
        return true
    }
}
