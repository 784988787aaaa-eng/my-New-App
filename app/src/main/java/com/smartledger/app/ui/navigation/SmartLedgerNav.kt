package com.smartledger.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartledger.app.R

sealed class SmartLedgerRoute(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
) {
    data object Home : SmartLedgerRoute("home", R.string.dashboard_title, Icons.Outlined.Home)
    data object People : SmartLedgerRoute("people", R.string.accounts, Icons.Outlined.PeopleAlt)
    data object Inventory : SmartLedgerRoute("inventory", R.string.view_inventory, Icons.Outlined.Inventory2)
    data object Reports : SmartLedgerRoute("reports", R.string.reports, Icons.Outlined.Assessment)
    data object More : SmartLedgerRoute("more", R.string.more, Icons.Outlined.MoreHoriz)
    data object Operation : SmartLedgerRoute("operation", R.string.new_operation, Icons.Outlined.AddCircle)
    data object Sale : SmartLedgerRoute("sale", R.string.new_sale, Icons.Outlined.ShoppingCart)
    data object Purchase : SmartLedgerRoute("purchase", R.string.new_purchase, Icons.Outlined.ShoppingBag)\n    data object BusinessManagement : SmartLedgerRoute("business_management", R.string.business_management, Icons.Outlined.Business)
}

val primaryRoutes = listOf(
    SmartLedgerRoute.Home,
    SmartLedgerRoute.People,
    SmartLedgerRoute.Inventory,
    SmartLedgerRoute.Reports,
    SmartLedgerRoute.More
)
