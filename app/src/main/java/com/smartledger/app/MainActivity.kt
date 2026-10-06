package com.smartledger.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.auth.AuthScreen
import com.smartledger.app.auth.AuthState
import com.smartledger.app.auth.AuthViewModel
import com.smartledger.app.ui.navigation.SmartLedgerShell
import com.smartledger.app.ui.theme.SmartLedgerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SmartLedgerApp() }
    }
}

@Composable
private fun SmartLedgerApp(authViewModel: AuthViewModel = viewModel()) {
    val state by authViewModel.state.collectAsState()
    SmartLedgerTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            when (val current = state) {
                AuthState.Loading -> Unit
                is AuthState.SignIn -> AuthScreen(
                    current,
                    onLogin = authViewModel::login,
                    onBootstrap = authViewModel::bootstrapOwner
                )
                is AuthState.SignedIn -> SmartLedgerShell(current.session, authViewModel::logout)
            }
        }
    }
}
