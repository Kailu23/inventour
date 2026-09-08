package com.kailu.inventour.ui.theme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kailu.inventour.R
import com.kailu.inventour.model.ProgressType
import com.kailu.inventour.model.WarehouseStat
import com.kailu.inventour.ui.theme.*



@Composable
fun WarehouseTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
                Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 32.dp, height = 40.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
        ) {
            Text(
                text = "W",
                color = TextOnDark,
                fontFamily = SyneFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = "Smart Warehouse",
                style = AppTypography.headlineMedium
            )
            Text(
                text = "Enterprise SaaS",
                style = AppTypography.bodySmall,
                color = TextSecondary
            )
        }
    }
}



@Composable
fun HeroSection(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
                Text(
            text = "Warehouse Management System",
            style = AppTypography.bodySmall,
            color = TextSecondary
        )

        Spacer(Modifier.height(12.dp))

                Text(
            text = "Pametno\nupravljanje\nskladištem",
            style = AppTypography.displayLarge,
            color = TextPrimary
        )

        Spacer(Modifier.height(24.dp))

                Text(
            text = "Pratite kapacitet, temperaturu, vlažnost i stanje polica u realnom vremenu — sve na jednom mjestu.",
            style = AppTypography.bodyMedium
        )

        Spacer(Modifier.height(32.dp))

                Row {
            LoginButton(onClick = onLoginClick)
            Spacer(Modifier.width(16.dp))
            RegisterButton(onClick = onRegisterClick)
        }
    }
}


@Composable
fun LoginButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceDark,
            contentColor = TextOnDark
        ),
        modifier = modifier.height(54.dp)
    ) {
        Text(
            text = "Prijava",
            style = AppTypography.labelLarge
        )
    }
}


@Composable
fun RegisterButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        border = BorderStroke(1.dp, SurfaceDark),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary
        ),
        modifier = modifier.height(54.dp)
    ) {
        Text(
            text = "Registracija",
            style = AppTypography.labelLarge
        )
    }
}



@Composable
fun DashboardCard(
    locationUsedPercent: String,
    stats: List<WarehouseStat>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(SurfaceLight)
            .padding(28.dp)
    ) {
                LocationUsedCard(percent = locationUsedPercent)

        Spacer(Modifier.height(16.dp))

                StatsGrid(stats = stats)
    }
}

@Composable
fun LocationUsedCard(percent: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0x66E53333))
                .align(Alignment.TopStart)
        ) {
            Image(
                painter = painterResource(R.drawable.v3_22),
                contentDescription = "Location map",
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(top = 70.dp)
        ) {
            Text(
                text = "Location Used",
                style = AppTypography.labelSmall,
                color = TextOnDarkMuted
            )
            Text(
                text = percent,
                style = AppTypography.headlineLarge
            )
        }
    }
}

@Composable
fun StatsGrid(stats: List<WarehouseStat>, modifier: Modifier = Modifier) {
        val rows = stats.chunked(2)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { rowStats ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowStats.forEach { stat ->
                    StatTile(stat = stat, modifier = Modifier.weight(1f))
                }
                                if (rowStats.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}


@Composable
fun StatTile(stat: WarehouseStat, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .padding(16.dp)
    ) {
        Text(
            text = stat.label,
            style = AppTypography.labelSmall
        )

        Spacer(Modifier.height(8.dp))

        when {
                        stat.progress != null -> {
                WarehouseProgressBar(
                    progress = stat.progress,
                    colorType = stat.progressColorType
                )
            }
                        stat.icon != null -> {
                Text(
                    text = stat.icon,
                    fontSize = 22.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = stat.value,
            style = AppTypography.titleLarge
        )
    }
}



@Composable
fun WarehouseProgressBar(
    progress: Float,
    colorType: ProgressType,
    modifier: Modifier = Modifier
) {
    val fillColor = when (colorType) {
        ProgressType.FULL    -> ProgressBarFull
        ProgressType.HALF    -> ProgressBarHalf
        ProgressType.EXPIRED -> ProgressBarExpired
        ProgressType.NONE    -> Color.Transparent
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(ProgressBarBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(fillColor)
        )
    }
}



@Composable
fun WarehouseFooter(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "© 2025 Smart Warehouse",
            style = AppTypography.bodySmall
        )
        Text(
            text = "Enterprise SaaS",
            style = AppTypography.bodySmall
        )
    }
}
