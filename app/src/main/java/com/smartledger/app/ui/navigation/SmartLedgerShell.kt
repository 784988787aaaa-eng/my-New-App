package com.smartledger.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import com.smartledger.app.ui.dashboard.DashboardScreen
import com.smartledger.app.ui.inventory.InventoryScreen
import com.smartledger.app.ui.more.MoreScreen
import com.smartledger.app.ui.people.PeopleScreen
import com.smartledger.app.ui.reports.ReportsScreen
import com.smartledger.app.ui.operations.OperationScreen
import com.smartledger.app.ui.commerce.CommerceEntryScreen
import com.smartledger.app.ui.commerce.CommerceMode
import com.smartledger.app.ui.more.BusinessManagementScreen
import com.smartledger.core.domain.Permission
import com.smartledger.core.domain.UserSession

@Composable
fun SmartLedgerShell(session: UserSession, onLogout: () -> Unit, navController: NavHostController = rememberNavController()) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    fun allowed(route: String): Boolean = when (route) {
        SmartLedgerRoute.Home.route -> session.can(Permission.VIEW_DASHBOARD)
        SmartLedgerRoute.People.route -> session.can(Permission.MANAGE_PEOPLE)
        SmartLedgerRoute.Inventory.route -> session.can(Permission.MANAGE_INVENTORY)
        SmartLedgerRoute.Reports.route -> session.can(Permission.VIEW_REPORTS)
        SmartLedgerRoute.More.route -> session.can(Permission.MANAGE_SETTINGS)
        SmartLedgerRoute.Operation.route -> session.can(Permission.MANAGE_OPERATIONS)
        SmartLedgerRoute.Sale.route -> session.can(Permission.MANAGE_SALES)
        SmartLedgerRoute.Purchase.route -> session.can(Permission.MANAGE_PURCHASES)
        SmartLedgerRoute.BusinessManagement.route -> session.can(Permission.MANAGE_EXPENSES)
        SmartLedgerRoute.InvoiceHistory.route -> session.can(Permission.MANAGE_SALES) || session.can(Permission.MANAGE_PURCHASES)
        else -> false
    }
    fun go(route: String) {
        if (allowed(route)) navController.navigate(route) { launchSingleTop = true }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                primaryRoutes.filter { allowed(it.route) }.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = { go(item.route) },
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(stringResource(item.labelRes)) }
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = if (allowed(SmartLedgerRoute.Home.route)) SmartLedgerRoute.Home.route else SmartLedgerRoute.More.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(SmartLedgerRoute.Home.route) {
                if (allowed(SmartLedgerRoute.Home.route)) DashboardScreen(onNavigate = ::go)
            }
            composable(SmartLedgerRoute.People.route) { if (allowed(SmartLedgerRoute.People.route)) PeopleScreen() }
            composable(SmartLedgerRoute.Inventory.route) { if (allowed(SmartLedgerRoute.Inventory.route)) InventoryScreen() }
            composable(SmartLedgerRoute.Reports.route) { if (allowed(SmartLedgerRoute.Reports.route)) ReportsScreen() }
            composable(SmartLedgerRoute.More.route) {
                if (allowed(SmartLedgerRoute.More.route)) MoreScreen(
                    onOpenBusinessManagement = { go(SmartLedgerRoute.BusinessManagement.route) },
                    onLogout = onLogout
                )
            }
            composable(SmartLedgerRoute.Operation.route) { if (allowed(SmartLedgerRoute.Operation.route)) OperationScreen(onSaved = { navController.popBackStack() }) }
            composable(SmartLedgerRoute.Sale.route) { if (allowed(SmartLedgerRoute.Sale.route)) CommerceEntryScreen(CommerceMode.SALE, onSaved = { navController.popBackStack() }) }
            composable(SmartLedgerRoute.Purchase.route) { if (allowed(SmartLedgerRoute.Purchase.route)) CommerceEntryScreen(CommerceMode.PURCHASE, onSaved = { navController.popBackStack() }) }
            composable(SmartLedgerRoute.BusinessManagement.route) { if (allowed(SmartLedgerRoute.BusinessManagement.route)) BusinessManagementScreen() }
            composable(SmartLedgerRoute.InvoiceHistory.route) { if (allowed(SmartLedgerRoute.InvoiceHistory.route)) com.smartledger.app.ui.commerce.InvoiceHistoryScreen() }
        }
    }
}
