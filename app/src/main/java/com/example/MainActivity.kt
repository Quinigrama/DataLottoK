package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.local.AppDatabase
import com.example.data.repository.TicketRepository
import com.example.ui.navigation.AppNavHost
import com.example.ui.theme.DataLottoTheme
import com.example.ui.viewmodel.LotteryViewModel
import com.example.ui.viewmodel.LotteryViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: LotteryViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TicketRepository(database.ticketDao())
        LotteryViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataLottoTheme {
                AppNavHost(viewModel = viewModel)
            }
        }
    }
}

