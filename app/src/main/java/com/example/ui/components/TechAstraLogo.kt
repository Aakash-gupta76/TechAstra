package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechAstraGoldDark
import com.example.ui.theme.TechAstraGoldLight
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary

@Composable
fun TechAstraLogoBadge(
    size: Dp = 100.dp,
    showRingGlow: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .testTag("techastra_logo_badge"),
        contentAlignment = Alignment.Center
    ) {
        if (showRingGlow) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                TechAstraGold.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(size * 0.9f)
                .clip(CircleShape)
                .border(
                    width = 2.5.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            TechAstraGold,
                            TechAstraGoldDark,
                            TechAstraGoldLight,
                            TechAstraGold
                        )
                    ),
                    shape = CircleShape
                )
                .background(TechBackground),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.techastra_logo),
                contentDescription = "TechAstra Emblem",
                modifier = Modifier
                    .size(size * 0.85f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun TechAstraBrandHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 80.dp,
    titleSize: Float = 28f,
    subtitle: String = "Your B.Tech Learning Companion"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TechAstraLogoBadge(size = logoSize)

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tech",
                fontSize = titleSize.sp,
                fontWeight = FontWeight.Bold,
                color = TechTextPrimary
            )
            Text(
                text = "Astra",
                fontSize = titleSize.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TechAstraGold
            )
        }

        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TechTextMuted
            )
        }
    }
}
