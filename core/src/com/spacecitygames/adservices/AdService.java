package com.spacecitygames.adservices;

import com.badlogic.gdx.Gdx;

public interface AdService {

    default void showInterstitialAd() {
        Gdx.app.log("ADS", "Interstitial ad shown here");
    }
}
