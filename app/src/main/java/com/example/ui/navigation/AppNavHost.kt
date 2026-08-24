package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.generator.GeneratorScreen
import com.example.ui.screens.tickets.SavedTicketsScreen
import com.example.ui.viewmodel.LotteryViewModel

@Composable
fun AppNavHost(
    viewModel: LotteryViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.GENERATOR,
        modifier = modifier
    ) {
        composable(NavRoutes.GENERATOR) {
            GeneratorScreen(
                viewModel = viewModel,
                onNavigateToSavedTickets = {
                    navController.navigate(NavRoutes.SAVED_TICKETS)
                }
            )
        }

        composable(NavRoutes.SAVED_TICKETS) {
            SavedTicketsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
