package com.example.data.repository

import com.example.data.local.TicketDao
import com.example.data.model.SavedTicket
import kotlinx.coroutines.flow.Flow

class TicketRepository(private val ticketDao: TicketDao) {
    val allTickets: Flow<List<SavedTicket>> = ticketDao.getAllTickets()

    suspend fun saveTicket(ticket: SavedTicket): Long = ticketDao.insertTicket(ticket)

    suspend fun updateTicket(ticket: SavedTicket) = ticketDao.updateTicket(ticket)

    suspend fun deleteTicket(ticket: SavedTicket) = ticketDao.deleteTicket(ticket)

    suspend fun deleteTicketById(id: Long) = ticketDao.deleteTicketById(id)
}
