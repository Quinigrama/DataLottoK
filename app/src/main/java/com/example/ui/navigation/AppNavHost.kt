package com.example.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.generator.GeneratorScreen
import com.example.ui.screens.legal.EthicsDisclaimerScreen
import com.example.ui.screens.stats.StatisticsScreen
import com.example.ui.screens.tickets.SavedTicketsScreen
import com.example.ui.viewmodel.LotteryViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    viewModel: LotteryViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentGame by viewModel.currentGame.collectAsStateWithLifecycle()
    val savedTickets by viewModel.savedTickets.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppSidebar(
                currentGame = currentGame,
                currentRoute = currentRoute,
                onSelectGame = { game ->
                    viewModel.selectGame(game)
                    if (currentRoute != NavRoutes.GENERATOR) {
                        navController.navigate(NavRoutes.GENERATOR) {
                            popUpTo(NavRoutes.GENERATOR) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToSavedTickets = {
                    if (currentRoute != NavRoutes.SAVED_TICKETS) {
                        navController.navigate(NavRoutes.SAVED_TICKETS)
                    }
                },
                onNavigateToStatistics = {
                    if (currentRoute != NavRoutes.STATISTICS) {
                        navController.navigate(NavRoutes.STATISTICS)
                    }
                },
                savedTicketsCount = savedTickets.size,
                onCloseDrawer = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        modifier = modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.GENERATOR
        ) {
            composable(NavRoutes.GENERATOR) {
                GeneratorScreen(
                    viewModel = viewModel,
                    onNavigateToSavedTickets = {
                        navController.navigate(NavRoutes.SAVED_TICKETS)
                    },
                    onOpenDrawer = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    onNavigateToEthicsDisclaimer = {
                        navController.navigate(NavRoutes.ETHICS_DISCLAIMER)
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

            composable(NavRoutes.STATISTICS) {
                StatisticsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(NavRoutes.ETHICS_DISCLAIMER) {
                EthicsDisclaimerScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
