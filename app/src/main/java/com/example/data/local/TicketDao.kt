package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SavedTicket
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Query("SELECT * FROM saved_tickets ORDER BY timestamp DESC")
    fun getAllTickets(): Flow<List<SavedTicket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SavedTicket): Long

    @androidx.room.Update
    suspend fun updateTicket(ticket: SavedTicket)

    @Delete
    suspend fun deleteTicket(ticket: SavedTicket)

    @Query("DELETE FROM saved_tickets WHERE id = :ticketId")
    suspend fun deleteTicketById(ticketId: Long)
}
