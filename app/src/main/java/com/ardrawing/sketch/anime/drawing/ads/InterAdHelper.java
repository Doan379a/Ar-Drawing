
package com.ardrawing.sketch.anime.drawing.ads;

import android.content.Context;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admob;
import com.amazic.ads.util.AdsConsentManager;
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;

public class InterAdHelper {
    private static long lastTimeShowed = 0;


    public static void showInterAd(Context context, boolean config, String id, Runnable onHandle) {
        if (config && canShowNextAd(context) && AdsConsentManager.getConsentResult(context)) {

            Admob.getInstance().loadInterAds(
                    context,
                    id,
                    new InterCallback() {
                        @Override
                        public void onAdLoadSuccess(InterstitialAd interstitialAd) {
                            super.onAdLoadSuccess(interstitialAd);
                            Admob.getInstance().showInterAds(context, interstitialAd, new InterCallback() {
                                @Override
                                public void onNextAction() {
                                    super.onNextAction();
                                    onHandle.run();
                                }

                                @Override
                                public void onAdFailedToShow(AdError adError) {
                                    super.onAdFailedToShow(adError);
                                    onHandle.run();
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError i) {
                            super.onAdFailedToLoad(i);
                            onHandle.run();
                        }
                    });
        } else {
            onHandle.run();
        }
    }

    private static boolean canShowNextAd(Context context) {
        if ((System.currentTimeMillis() - lastTimeShowed) >= SharePrefRemote.get_config_long(context, SharePrefRemote.interval_between_interstitial) * 1000) {
            lastTimeShowed = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

}