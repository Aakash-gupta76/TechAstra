package com.example.data.admob

import android.content.Context
import com.example.R

/**
 * Configuration and Ad Unit IDs for Google AdMob Monetization in TechAstra.
 */
object AdMobConfig {

    /**
     * Master switch to enable or disable ads globally in the app.
     */
    var isAdsEnabled: Boolean = true

    /**
     * Your Actual AdMob App ID (registered with AdMob for TechAstra):
     * ca-app-pub-3637965377534592~3770229709
     * Referenced in AndroidManifest.xml via @string/admob_app_id
     */
    const val REAL_APP_ID = "ca-app-pub-3637965377534592~3770229709"

    /**
     * Your Actual Production Banner Ad Unit ID:
     * ca-app-pub-3637965377534592/8745178611
     * Switch useTestAdUnitId to false when generating final release for Google Play Store.
     */
    const val PRODUCTION_BANNER_AD_UNIT_ID = "ca-app-pub-3637965377534592/8745178611"

    /**
     * Official Google Test Banner Ad Unit ID:
     * ca-app-pub-3940256099942544/6300978111
     * Used during development and testing to prevent invalid click/traffic penalties.
     */
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    /**
     * Official Google Test Interstitial Ad Unit ID:
     * ca-app-pub-3940256099942544/1033173712
     */
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    /**
     * Controls whether test or production banner ad unit ID is used:
     * Set to false for Production so your Real Banner ID (ca-app-pub-3637965377534592/8745178611) is active.
     */
    var useTestAdUnitId: Boolean = false

    /**
     * Resolves the active Banner Ad Unit ID based on the current mode:
     */
    fun getBannerAdUnitId(context: Context): String {
        return if (useTestAdUnitId) {
            try {
                val id = context.getString(R.string.admob_banner_ad_unit_id)
                if (id.isNotBlank()) id else TEST_BANNER_AD_UNIT_ID
            } catch (_: Exception) {
                TEST_BANNER_AD_UNIT_ID
            }
        } else {
            try {
                val id = context.getString(R.string.admob_production_banner_ad_unit_id)
                if (id.isNotBlank()) id else PRODUCTION_BANNER_AD_UNIT_ID
            } catch (_: Exception) {
                PRODUCTION_BANNER_AD_UNIT_ID
            }
        }
    }

    /**
     * Resolves the Interstitial Ad Unit ID:
     */
    fun getInterstitialAdUnitId(context: Context): String {
        return try {
            val id = context.getString(R.string.admob_interstitial_ad_unit_id)
            if (id.isNotBlank()) id else TEST_INTERSTITIAL_AD_UNIT_ID
        } catch (_: Exception) {
            TEST_INTERSTITIAL_AD_UNIT_ID
        }
    }
}
