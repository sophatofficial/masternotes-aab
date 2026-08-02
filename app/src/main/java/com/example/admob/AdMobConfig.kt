package com.example.admob

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * AdMob Configuration file.
 * All AdMob IDs and ad initialization logic are centralized here.
 *
 * IMPORTANT ADMOB SAFETY NOTICE:
 * - The IDs configured below are the provided development testing IDs.
 * - Do NOT use real AdMob IDs during development or testing to prevent policy violations.
 * - When ready to publish to Google Play, replace these test IDs with your production AdMob IDs.
 */
object AdMobConfig {
    private const val TAG = "AdMobConfig"

    // Official provided AdMob IDs
    const val APP_ID = "ca-app-pub-2615392705372264~1774030489"
    const val BANNER_AD_UNIT_ID = "ca-app-pub-2615392705372264/9460948819"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2615392705372264/9211722712"

    // Standard Google Test Ad Unit IDs for safe local fallback if needed
    // Banner Test ID: ca-app-pub-3940256099942544/6300978111
    // Interstitial Test ID: ca-app-pub-3940256099942544/1033173712

    private var interstitialAd: InterstitialAd? = null

    /**
     * Initializes the Google Mobile Ads SDK.
     * Call this once during app startup (e.g. in MainActivity onCreate).
     */
    fun initialize(context: Context) {
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob MobileAds initialized successfully: $initializationStatus")
            loadInterstitialAd(context)
        }
    }

    /**
     * Preloads an Interstitial Ad to be shown at natural transition points.
     */
    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(TAG, "Interstitial ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    /**
     * Displays the loaded Interstitial Ad if available.
     */
    fun showInterstitialAd(activity: android.app.Activity, onAdDismissed: () -> Unit = {}) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "Interstitial ad was not ready yet")
            loadInterstitialAd(activity)
            onAdDismissed()
        }
    }
}
