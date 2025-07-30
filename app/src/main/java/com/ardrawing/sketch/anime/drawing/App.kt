package com.ardrawing.sketch.anime.drawing

import com.amazic.ads.util.AdsApplication
import com.amazic.ads.util.AppOpenManager
import com.ardrawing.sketch.anime.drawing.ui.splash.SplashActivity
import com.google.android.gms.ads.MobileAds



class App : AdsApplication() {
    override fun onCreate() {
      MobileAds.initialize(this) { }
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        super.onCreate()

    }

    override fun getAppTokenAdjust(): String {
        return ""
    }

    override fun getFacebookID(): String {
        return ""
    }

    override fun buildDebug(): Boolean? {
        return null
    }
}