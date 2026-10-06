package com.smartledger.app.ui.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smartledger.app.R
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens

@Composable
fun MoreScreen() {
    val settings = listOf(
        R.string.business_identity,
        R.string.backup_restore,
        R.string.security_privacy,
        R.string.user_permissions
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.more), style = MaterialTheme.typography.headlineLarge)
        }
        items(settings) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = BorderStroke(1.dp, SmartLedgerColors.Border)
            ) {
                Column(Modifier.padding(SmartLedgerDimens.Card)) {
                    Icon(
                        imageVector = when (item) {
                            R.string.security_privacy -> Icons.Outlined.Security
                            R.string.backup_restore -> Icons.Outlined.Storage
                            else -> Icons.Outlined.Settings
                        },
                        contentDescription = stringResource(item),
                        tint = SmartLedgerColors.Blue600
                    )
                    Text(stringResource(item), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
