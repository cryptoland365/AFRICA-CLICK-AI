package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * AFRICA CLICK AI Red & White eBook Brand Logo
 */
@Composable
fun AfricaClickLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 48.dp,
    showTagline: Boolean = true,
    isLargeHeader: Boolean = false
) {
    val vibrantRed = Color(0xFFFF1744)
    val deepCrimson = Color(0xFFD32F2F)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.testTag("africa_click_logo")
    ) {
        // Red & White eBook Logo Badge
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(vibrantRed, deepCrimson, Color(0xFF880E4F))
                    )
                )
                .border(
                    width = 2.dp,
                    color = PureWhite,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Crisp White Open Book Icon
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(iconSize * 0.65f)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // "AFRICA" in White
                Text(
                    text = "AFRICA ",
                    style = if (isLargeHeader) MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ) else MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    ),
                    color = PureWhite
                )
                // "CLICK" in Vibrant Red
                Text(
                    text = "CLICK",
                    style = if (isLargeHeader) MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ) else MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    ),
                    color = vibrantRed
                )

                Spacer(modifier = Modifier.width(6.dp))

                // "AI" Badge Chip
                Surface(
                    color = deepCrimson,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PureWhite)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "AI",
                            color = PureWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (showTagline) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "LIBRAIRIE NUMÉRIQUE D'ÉLITE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextSecondaryDark
                )
            }
        }
    }
}

