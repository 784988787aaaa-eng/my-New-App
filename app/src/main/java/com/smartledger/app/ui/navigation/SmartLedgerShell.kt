package com.smartledger.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.smartledger.app.ui.dashboard.DashboardScreen
import com.smartledger.app.ui.inventory.InventoryScreen
import com.smartledger.app.ui.more.MoreScreen
import com.smartledger.app.ui.people.PeopleScreen
import com.smartledger.app.ui.reports.ReportsScreen
import com.smartledger.app.ui.operations.OperationScreen

@Composable
fun SmartLedgerShell(navController: NavHostController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                primaryRoutes.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(SmartLedgerRoute.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(stringResource(item.labelRes)) }
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = SmartLedgerRoute.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(SmartLedgerRoute.Home.route) { DashboardScreen { route -> navController.navigate(route) { launchSingleTop = true } } }
            composable(SmartLedgerRoute.People.route) { PeopleScreen() }
            composable(SmartLedgerRoute.Inventory.route) { InventoryScreen() }
            composable(SmartLedgerRoute.Reports.route) { ReportsScreen() }
            composable(SmartLedgerRoute.More.route) { MoreScreen() }
            composable(SmartLedgerRoute.Operation.route) { OperationScreen(onSaved = { navController.popBackStack() }) }
        }
    }
}
