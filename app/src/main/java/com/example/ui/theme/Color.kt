package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Professional Polish Blue Palette
val DeepDarkBlue = Color(0xFF0B132B) // Deepest Dark Blue
val Navy900 = Color(0xFF1C2541)    // Dark Navy 900
val Navy800 = Color(0xFF1E3A8A)    // Dark Blue 800
val Navy700 = Color(0xFF2563EB)    // Medium Navy 700
val PrimaryBlue = Color(0xFF1D4ED8) // Royal Tech Blue 700
val TechBlue = Color(0xFF1D4ED8)    // Tech Blue alias
val TechBlueLight = Color(0xFF3B82F6) // Bright Accent Blue 500
val ElectricCyan = Color(0xFF00E5FF)  // Glowing Neon Cyan
val BlueSelection = Color(0xFFD1E4FF) // Soft Selection Blue

// Pure Black & White Standards
val PureBlack = Color(0xFF000000)
val DarkBackground = Color(0xFF0A0F1D)
val BackgroundDark = Color(0xFF0A0F1D)
val DarkSurface = Color(0xFF151C2E)
val PureWhite = Color(0xFFFFFFFF)

// Accent Palette (Gold / Amber Highlights)
val Gold400 = Color(0xFFFACC15) // Best-Seller badge yellow
val Gold500 = Color(0xFFEAB308)
val Gold600 = Color(0xFFCA8A04)

// Light Theme Surface & Input Colors
val SurfaceLight = Color(0xFFFDFBFF) // Off-white clean background
val InputSurfaceLight = Color(0xFFF3F3FA) // Light gray-blue search/card background
val ChipBackgroundLight = Color(0xFFE1E2EC)
val CardLight = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF1C1B1F)
val TextSecondaryLight = Color(0xFF44474F)

val SurfaceDark = Color(0xFF0A0F1D)
val CardDark = Color(0xFF151C2E)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)

// Status colors
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)
val ErrorRed = Color(0xFFEF4444)
val InfoBlue = Color(0xFF2563EB)

// Gradient Brushes for Professional Polish Theme
val PrimaryHeaderGradient = Brush.verticalGradient(
    colors = listOf(DeepDarkBlue, Navy900, Navy800)
)

val DarkBlueCardGradient = Brush.linearGradient(
    colors = listOf(Navy900, PrimaryBlue)
)

val BlackNavyGradient = Brush.linearGradient(
    colors = listOf(PureBlack, DeepDarkBlue, Navy900)
)

val AccentHeroGradient = Brush.horizontalGradient(
    colors = listOf(DeepDarkBlue, PrimaryBlue, Gold500)
)



