package com.example.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.admob.AdMobConfig
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechTextMuted
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * A Jetpack Compose Banner Ad component using Google AdMob Test IDs:
 * - Marked with "Ad / Test Mode" label.
 * - Safely collapses if load fails.
 * - Kept isolated from navigation controls and action buttons.
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String? = null
) {
    if (!AdMobConfig.isAdsEnabled) return

    val context = LocalContext.current
    val targetAdUnitId = remember(adUnitId) {
        adUnitId ?: AdMobConfig.getBannerAdUnitId(context)
    }

    var isAdLoaded by remember { mutableStateOf(false) }
    var isAdFailed by remember { mutableStateOf(false) }
    var adViewInstance by remember { mutableStateOf<AdView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                adViewInstance?.destroy()
            } catch (e: Exception) {
                Log.w("BannerAdView", "Error destroying adView: ${e.message}")
            }
        }
    }

    if (isAdFailed) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("banner_ad_container"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(TechSurface)
                .border(1.dp, TechCardBorder, RoundedCornerShape(14.dp))
                .padding(vertical = 8.dp, horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isTestMode = AdMobConfig.useTestAdUnitId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isTestMode) "TEST AD" else "AD",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTestMode) Color(0xFFFBBF24) else TechTextMuted
                        )
                    }

                    Text(
                        text = if (isTestMode) "Google AdMob Test Mode" else "Sponsored Content",
                        fontSize = 10.sp,
                        color = TechTextMuted
                    )
                }

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admob_adview"),
                    factory = { ctx ->
                        AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            this.adUnitId = targetAdUnitId
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    super.onAdLoaded()
                                    isAdLoaded = true
                                    isAdFailed = false
                                    Log.d("BannerAdView", "Ad loaded successfully with test ID")
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    super.onAdFailedToLoad(loadAdError)
                                    isAdLoaded = false
                                    isAdFailed = true
                                    Log.w("BannerAdView", "Ad failed to load: ${loadAdError.code} - ${loadAdError.message}")
                                }
                            }
                            try {
                                loadAd(AdRequest.Builder().build())
                            } catch (e: Exception) {
                                Log.w("BannerAdView", "Exception during loadAd: ${e.message}")
                                isAdFailed = true
                            }
                            adViewInstance = this
                        }
                    }
                )
            }
        }
    }
}
