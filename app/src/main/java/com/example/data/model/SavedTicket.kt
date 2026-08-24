package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_tickets")
data class SavedTicket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: String,
    val gameName: String,
    val numbers: List<Int>,
    val secondaryNumbers: List<Int> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val strategy: String = "simple",
    val isValidated: Boolean = false,
    val validatedHits: Int? = null,
    val validatedStarHits: Int? = null,
    val prizeLabel: String? = null
)
