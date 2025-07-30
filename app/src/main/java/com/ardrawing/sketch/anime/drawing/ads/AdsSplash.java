package com.ardrawing.sketch.anime.drawing.ads;


import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.amazic.ads.callback.AdCallback;
import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admob;
import com.amazic.ads.util.AppOpenManager;
import com.ardrawing.sketch.anime.drawing.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdsSplash {
    private static final String TAG = "AdsSplash";
    private STATE state;

    public AdsSplash() {
        this.state = STATE.NO_ADS;
    }

    public static AdsSplash init(boolean showInter, boolean showOpen, String rate) {
        AdsSplash adsSplash = new AdsSplash();
        Log.d("AdsSplash", "init: ");
        if (!Admob.isShowAllAds) {
            adsSplash.setState(STATE.NO_ADS);
        } else if (showInter && showOpen) {
            adsSplash.checkShowInterOpenSplash(rate);
        } else if (showInter) {
            adsSplash.setState(STATE.INTER);
        } else if (showOpen) {
            adsSplash.setState(STATE.OPEN);
        } else {
            adsSplash.setState(STATE.NO_ADS);
        }

        return adsSplash;
    }

    private void checkShowInterOpenSplash(String rate) {
        int rateInter;
        int rateOpen;
        try {
            rateInter = Integer.parseInt(rate.trim().split("_")[1].trim());
            rateOpen = Integer.parseInt(rate.trim().split("_")[0].trim());
        } catch (Exception var5) {
            Log.d("AdsSplash", "checkShowInterOpenSplash: ");
            rateInter = 0;
            rateOpen = 0;
        }

        Log.d("AdsSplash", "rateInter: " + rateInter + " - rateOpen: " + rateOpen);
        Log.d("AdsSplash", "rateInter: " + rateInter + " - rateOpen: " + rateOpen);
        if (rateInter >= 0 && rateOpen >= 0 && rateInter + rateOpen == 100) {
            boolean isShowOpenSplash = (new Random()).nextInt(100) + 1 < rateOpen;
            this.setState(isShowOpenSplash ? STATE.OPEN : STATE.INTER);
        } else {
            this.setState(STATE.NO_ADS);
        }

    }

    public void setState(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return this.state;
    }

    public void showAdsSplashApi(AppCompatActivity activity, AdCallback openCallback, InterCallback interCallback) {
        Log.d("AdsSplash", "state show: " + this.getState());
        if (this.getState() == STATE.OPEN) {
            loadOpenAppAdSplashFloor(activity, openCallback);
        } else if (this.getState() == STATE.INTER) {
            loadInterAdSplashFloor(activity, 3000, 20000, interCallback, true);
        } else {
            interCallback.onNextAction();
        }

    }

    public void onCheckShowSplashWhenFail(AppCompatActivity activity, AdCallback openCallback, InterCallback interCallback) {
        if (this.getState() == STATE.OPEN) {
            AppOpenManager.getInstance().onCheckShowSplashWhenFailNew(activity, openCallback, 1000);
        } else if (this.getState() == STATE.INTER) {
            Admob.getInstance().onCheckShowSplashWhenFail(activity, interCallback, 1000);
        }

    }

    static enum STATE {
        INTER,
        OPEN,
        NO_ADS;

        private STATE() {
        }
    }

    public void loadOpenAppAdSplashFloor(Activity activity, AdCallback adCallback) {
        List<String> list = new ArrayList<>();
        list.add(activity.getString(R.string.open_splash));
        AppOpenManager.getInstance().loadOpenAppAdSplashFloor(activity, list, true, adCallback);
    }

    public void loadInterAdSplashFloor(Activity activity, int timeDelay, int timeOut, InterCallback callback, boolean isNextActionWhenFailedInter) {
        List<String> list = new ArrayList<>();
        list.add(activity.getString(R.string.inter_splash));
        Admob.getInstance().loadSplashInterAds3(activity, list, timeDelay, timeOut, callback, isNextActionWhenFailedInter);
    }
}