package com.example.data.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Manages Google Mobile Ads (AdMob) SDK initialization,
 * User Messaging Platform (UMP) consent flows, and test device configurations.
 */
object AdMobManager {

    private const val TAG = "AdMobManager"
    private val isMobileAdsInitialized = AtomicBoolean(false)

    /**
     * Initializes Google Mobile Ads SDK with UMP consent checks and emulator test configuration.
     */
    fun initialize(activity: Activity, onComplete: () -> Unit = {}) {
        if (!AdMobConfig.isAdsEnabled) {
            Log.d(TAG, "AdMob is disabled via AdMobConfig.isAdsEnabled")
            onComplete()
            return
        }

        try {
            // Configure Emulator as Test Device so test ads always serve reliably
            val testDeviceIds = listOf(AdRequest.DEVICE_ID_EMULATOR)
            val configuration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
            MobileAds.setRequestConfiguration(configuration)

            val params = ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .build()

            val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

            consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                        if (formError != null) {
                            Log.w(TAG, "UMP consent form error: ${formError.errorCode} - ${formError.message}")
                        }

                        if (consentInformation.canRequestAds()) {
                            initializeMobileAds(activity, onComplete)
                        } else {
                            Log.d(TAG, "Cannot request ads based on current consent status")
                            onComplete()
                        }
                    }
                },
                { requestConsentError ->
                    Log.w(TAG, "UMP requestConsentInfoUpdate failed: ${requestConsentError.errorCode} - ${requestConsentError.message}")
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAds(activity, onComplete)
                    } else {
                        onComplete()
                    }
                }
            )

            // If consent already obtained or not required, initialize right away
            if (consentInformation.canRequestAds()) {
                initializeMobileAds(activity)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AdMob / UMP: ${e.message}", e)
            onComplete()
        }
    }

    private fun initializeMobileAds(context: Context, onComplete: () -> Unit = {}) {
        if (isMobileAdsInitialized.getAndSet(true)) {
            onComplete()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                MobileAds.initialize(context) { initializationStatus ->
                    Log.d(TAG, "Google Mobile Ads SDK Initialized: $initializationStatus")
                    onComplete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "MobileAds.initialize exception: ${e.message}", e)
                onComplete()
            }
        }
    }

    fun isPrivacyOptionsRequired(context: Context): Boolean {
        return try {
            val consentInformation = UserMessagingPlatform.getConsentInformation(context)
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        } catch (_: Exception) {
            false
        }
    }

    fun showPrivacyOptionsForm(activity: Activity, onDismissed: () -> Unit = {}) {
        try {
            UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
                if (error != null) {
                    Log.w(TAG, "Failed to show privacy options form: ${error.message}")
                }
                onDismissed()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying privacy options form: ${e.message}", e)
            onDismissed()
        }
    }

    fun loadInterstitialAd(
        context: Context,
        adUnitId: String? = null,
        onLoaded: (InterstitialAd?) -> Unit
    ) {
        if (!AdMobConfig.isAdsEnabled) {
            onLoaded(null)
            return
        }

        val targetAdUnitId = adUnitId ?: AdMobConfig.getInterstitialAdUnitId(context)
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            targetAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    onLoaded(interstitialAd)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    onLoaded(null)
                }
            }
        )
    }

    fun showInterstitialAd(
        activity: Activity,
        interstitialAd: InterstitialAd?,
        onDismissed: () -> Unit
    ) {
        if (interstitialAd == null) {
            onDismissed()
            return
        }

        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed")
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "Interstitial ad failed to show: ${adError.message}")
                onDismissed()
            }
        }

        interstitialAd.show(activity)
    }
}
