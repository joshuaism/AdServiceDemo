package com.spacecitygames.adservices;

import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdMobService implements AdService {

    public static final String TEST_KEY = "ca-app-pub-3940256099942544/1033173712";

    private InterstitialAd mInterstitialAd;
    private AndroidApplication androidApplication;
    private String adMobKey;

    public AdMobService(AndroidApplication androidApplication, String adMobKey) {
        this.androidApplication = androidApplication;
        this.adMobKey = adMobKey;
        if(adMobKey == null || adMobKey.isEmpty()) {
            Log.i("ADS", "No AdMob App Key provided. Defaulting to test value.");
            this.adMobKey = AdMobService.TEST_KEY;
        }
        MobileAds.initialize(this.androidApplication, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadNewAd();
    }

    public void loadNewAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(androidApplication, adMobKey, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Gdx.app.log("ADS", "Interstitial Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Gdx.app.log("ADS", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    @Override
    public void showInterstitialAd() {

        Gdx.app.log("ADS", "Attempting to show interstitial ad from AdMob");
        try {
            androidApplication.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Gdx.app.log("ADS", "The ad was dismissed.");
                                loadNewAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Gdx.app.log("ADS", "The ad failed to show.");
                                loadNewAd();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Gdx.app.log("ADS", "The ad was shown.");
                            }
                        });
                        mInterstitialAd.show(androidApplication);
                    } else {
                        Gdx.app.log("ADS", "The interstitial ad wasn't ready yet.");
                        loadNewAd();
                    }
                }
            });
        } catch (Exception e) {
            Gdx.app.error("ADS", "Failed to show ad.", e);
        }
    }
}