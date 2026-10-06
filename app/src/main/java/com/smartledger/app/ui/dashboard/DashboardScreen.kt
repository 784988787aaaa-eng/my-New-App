package com.smartledger.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.smartledger.app.R
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens

private data class QuickAction(
    val label: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SmartLedgerColors.Background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = SmartLedgerDimens.Screen,
                    top = SmartLedgerDimens.Screen,
                    end = SmartLedgerDimens.Screen,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.Section)
            ) {
                item {
                    DashboardHeader(onNavigate)
                }
                item {
                    BalanceOverview()
                }
                item {
                    SectionTitle(stringResource(R.string.quick_actions))
                    Spacer(Modifier.height(12.dp))
                    QuickActions()
                }
                item {
                    SectionTitle(stringResource(R.string.today_activity))
                    Spacer(Modifier.height(12.dp))
                    TodaySummary()
                }
                item {
                    SectionTitle(stringResource(R.string.recent_operations))
                    Spacer(Modifier.height(12.dp))
                    EmptyOperations()
                }
            }

            FloatingActionButton(
                onClick = { onNavigate("more") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = SmartLedgerDimens.Screen, bottom = 24.dp),
                containerColor = SmartLedgerColors.Navy900,
                contentColor = androidx.compose.ui.graphics.Color.White
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.new_operation)
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(onNavigate: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.dashboard_title),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = SmartLedgerColors.TextSecondary
            )
        }
        IconButton(onClick = { onNavigate("more") }) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = SmartLedgerColors.TextSecondary
            )
        }
    }
}

@Composable
private fun BalanceOverview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BalanceCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.total_receivable),
            amount = "0",
            suffix = stringResource(R.string.currency_yer),
            icon = Icons.Outlined.AccountBalanceWallet,
            tint = SmartLedgerColors.Success,
            container = SmartLedgerColors.SuccessContainer
        )
        BalanceCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.total_payable),
            amount = "0.00",
            suffix = stringResource(R.string.currency_placeholder),
            icon = Icons.Outlined.AccountBalanceWallet,
            tint = SmartLedgerColors.Danger,
            container = SmartLedgerColors.DangerContainer
        )
    }
}

@Composable
private fun BalanceCard(
    modifier: Modifier,
    title: String,
    amount: String,
    suffix: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    container: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(SmartLedgerDimens.Radius),
        colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SmartLedgerColors.Border)
    ) {
        Column(
            modifier = Modifier.padding(SmartLedgerDimens.Card),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(container),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = SmartLedgerColors.TextSecondary
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    amount,
                    style = MaterialTheme.typography.headlineSmall,
                    color = SmartLedgerColors.TextPrimary
                )
                Spacer(Modifier.size(5.dp))
                Text(
                    suffix,
                    style = MaterialTheme.typography.labelLarge,
                    color = SmartLedgerColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun QuickActions() {
    val actions = listOf(
        QuickAction(R.string.new_operation, Icons.Outlined.Add),
        QuickAction(R.string.new_sale, Icons.Outlined.PointOfSale),
        QuickAction(R.string.new_purchase, Icons.Outlined.ReceiptLong),
        QuickAction(R.string.add_person, Icons.Outlined.PeopleAlt),
        QuickAction(R.string.view_inventory, Icons.Outlined.Inventory2)
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(actions) { action ->
            Card(
                shape = RoundedCornerShape(SmartLedgerDimens.RadiusSmall),
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmartLedgerColors.Border)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 112.dp, height = 92.dp)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = stringResource(action.label),
                        tint = SmartLedgerColors.Blue600
                    )
                    Text(
                        text = stringResource(action.label),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun TodaySummary() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.ShowChart,
            value = "0",
            label = stringResource(R.string.new_sale)
        )
        SummaryItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.ReceiptLong,
            value = "0",
            label = stringResource(R.string.new_purchase)
        )
        SummaryItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.PeopleAlt,
            value = "0",
            label = stringResource(R.string.accounts)
        )
    }
}

@Composable
private fun SummaryItem(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
        shape = RoundedCornerShape(SmartLedgerDimens.RadiusSmall)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = label, tint = SmartLedgerColors.TextSecondary)
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = SmartLedgerColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyOperations() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
        shape = RoundedCornerShape(SmartLedgerDimens.Radius),
        border = androidx.compose.foundation.BorderStroke(1.dp, SmartLedgerColors.Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = SmartLedgerColors.TextSecondary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.no_operations),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.no_operations_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = SmartLedgerColors.TextSecondary
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
}
