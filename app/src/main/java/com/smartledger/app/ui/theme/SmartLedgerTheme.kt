package com.smartledger.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object SmartLedgerColors {
    val Navy900 = Color(0xFF10233F)
    val Navy800 = Color(0xFF17345D)
    val Blue600 = Color(0xFF2F67C7)
    val Blue100 = Color(0xFFE8F0FF)
    val Background = Color(0xFFF5F7FA)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFF0F3F7)
    val TextPrimary = Color(0xFF182230)
    val TextSecondary = Color(0xFF687386)
    val Border = Color(0xFFE1E6ED)
    val Success = Color(0xFF238B57)
    val SuccessContainer = Color(0xFFE8F6EF)
    val Danger = Color(0xFFC63C4B)
    val DangerContainer = Color(0xFFFCEBED)
    val Warning = Color(0xFFB86B00)
    val WarningContainer = Color(0xFFFFF3DF)
    val Info = Color(0xFF376DAD)
}

object SmartLedgerDimens {
    val Screen = 20.dp
    val Section = 24.dp
    val Card = 16.dp
    val Field = 14.dp
    val Row = 56.dp
    val TouchTarget = 48.dp
    val Icon = 24.dp
    val SmallIcon = 20.dp
    val Radius = 16.dp
    val RadiusSmall = 12.dp
    val FormGap = 12.dp
    val Border = 1.dp
}

private val SmartLedgerTypography = Typography(
    headlineLarge = androidx.compose.material3.Typography().headlineLarge.copy(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = androidx.compose.material3.Typography().headlineSmall.copy(
        fontSize = 21.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = androidx.compose.material3.Typography().titleMedium.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = androidx.compose.material3.Typography().bodyLarge.copy(
        fontSize = 15.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = androidx.compose.material3.Typography().bodyMedium.copy(
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    labelLarge = androidx.compose.material3.Typography().labelLarge.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold
    )
)

private val LightColors = lightColorScheme(
    primary = SmartLedgerColors.Blue600,
    onPrimary = Color.White,
    primaryContainer = SmartLedgerColors.Blue100,
    onPrimaryContainer = SmartLedgerColors.Navy900,
    background = SmartLedgerColors.Background,
    onBackground = SmartLedgerColors.TextPrimary,
    surface = SmartLedgerColors.Surface,
    onSurface = SmartLedgerColors.TextPrimary,
    surfaceVariant = SmartLedgerColors.SurfaceMuted,
    onSurfaceVariant = SmartLedgerColors.TextSecondary,
    outline = SmartLedgerColors.Border,
    error = SmartLedgerColors.Danger,
    onError = Color.White,
    errorContainer = SmartLedgerColors.DangerContainer,
    onErrorContainer = SmartLedgerColors.Danger
)

@Composable
fun SmartLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = SmartLedgerTypography,
        content = content
    )
}
