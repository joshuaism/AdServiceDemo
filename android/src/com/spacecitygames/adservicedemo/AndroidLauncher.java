package com.spacecitygames.adservicedemo;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.spacecitygames.adservices.AdMobService;


public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String adMobKey = getAdMobKey();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new AdServiceDemo(new AdMobService(this, adMobKey)), config);
	}

	private String getAdMobKey() {
		if (BuildConfig.DEBUG) {
			Log.i("ADS", "Running in debug mode. Defaulting to test value.");
			return AdMobService.TEST_KEY;
		}
		try {
			String packageName = getPackageName();
			ApplicationInfo ai = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			return bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
		} catch (Exception e) {
			Log.e("ADS", "Failed to load com.google.android.gms.ads.APPLICATION_ID from AndroidManifest.xml", e);
		}
		return "";
	}
}
