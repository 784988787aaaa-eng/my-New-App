package com.smartledger.app.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(state: AuthState, onLogin: (String, CharArray) -> Unit, onBootstrap: (String, String, CharArray) -> Unit) {
    var username by remember { mutableStateOf("admin") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    val firstRun = (state as? AuthState.SignIn)?.firstRun == true
    val error = (state as? AuthState.SignIn)?.error
    Column(
        Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.Lock, null, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(18.dp))
        Text(if (firstRun) "إعداد الدفتر الذكي" else "تسجيل الدخول", style = MaterialTheme.typography.headlineMedium)
        Text(
            if (firstRun) "أنشئ حساب المالك الأول. كلمة المرور تُخزّن كقيمة مشتقة ومحمية، ولا تُحفظ كنص صريح."
            else "أدخل بيانات المستخدم للوصول إلى بيانات النشاط.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(username, { username = it }, label = { Text("اسم المستخدم") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        if (firstRun) {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(displayName, { displayName = it }, label = { Text("الاسم الظاهر") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(password, { password = it }, label = { Text("كلمة المرور") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        if (firstRun) {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(confirmation, { confirmation = it }, label = { Text("تأكيد كلمة المرور") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        }
        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = {
                if (firstRun) onBootstrap(username, displayName, password.toCharArray())
                else onLogin(username, password.toCharArray())
                password = ""; confirmation = ""
            },
            enabled = username.isNotBlank() && password.length >= 8 && (!firstRun || (displayName.isNotBlank() && confirmation == password)),
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (firstRun) "إنشاء حساب المالك" else "دخول") }
    }
}
