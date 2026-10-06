package com.smartledger.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
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
    val Screen = 16.dp
    val Section = 18.dp
    val Card = 12.dp
    val Field = 12.dp
    val Row = 52.dp
    val TouchTarget = 48.dp
    val Icon = 24.dp
    val SmallIcon = 20.dp
    val Radius = 16.dp
    val RadiusSmall = 12.dp
    val FormGap = 9.dp
    val Border = 1.dp
}
private val SmartLedgerTypography = Typography(
    headlineLarge = Typography().headlineLarge.copy(fontSize = 25.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    headlineSmall = Typography().headlineSmall.copy(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
    titleLarge = Typography().titleLarge.copy(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold),
    titleMedium = Typography().titleMedium.copy(fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = Typography().bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = Typography().bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp),
    labelLarge = Typography().labelLarge.copy(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
)
private val LightColors = lightColorScheme(
    primary = SmartLedgerColors.Blue600, onPrimary = Color.White,
    primaryContainer = SmartLedgerColors.Blue100, onPrimaryContainer = SmartLedgerColors.Navy900,
    background = SmartLedgerColors.Background, onBackground = SmartLedgerColors.TextPrimary,
    surface = SmartLedgerColors.Surface, onSurface = SmartLedgerColors.TextPrimary,
    surfaceVariant = SmartLedgerColors.SurfaceMuted, onSurfaceVariant = SmartLedgerColors.TextSecondary,
    outline = SmartLedgerColors.Border, error = SmartLedgerColors.Danger, onError = Color.White,
    errorContainer = SmartLedgerColors.DangerContainer, onErrorContainer = SmartLedgerColors.Danger
)
@Composable
fun SmartLedgerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, typography = SmartLedgerTypography, shapes = Shapes(
        small = androidx.compose.foundation.shape.RoundedCornerShape(SmartLedgerDimens.RadiusSmall),
        medium = androidx.compose.foundation.shape.RoundedCornerShape(SmartLedgerDimens.Radius),
        large = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ), content = content)
}