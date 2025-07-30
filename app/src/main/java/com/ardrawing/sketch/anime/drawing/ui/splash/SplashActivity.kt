package com.ardrawing.sketch.anime.drawing.ui.splash

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import com.amazic.ads.callback.AdCallback
import com.amazic.ads.callback.InterCallback
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.AppOpenManager
import com.applovin.sdk.AppLovinPrivacySettings
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.ads.AdsSplash
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.databinding.ActivitySplashBinding
import com.ardrawing.sketch.anime.drawing.sharePreferent.Common
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote
import com.ardrawing.sketch.anime.drawing.ui.language_start.LanguageStartActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.ironsource.mediationsdk.IronSource
import com.mbridge.msdk.MBridgeConstans
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.unity3d.ads.metadata.MetaData
import com.vungle.ads.VunglePrivacySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default);
    private var adsSplash: AdsSplash? = null
    private var adCallback: AdCallback? = null
    private var interCallback: InterCallback? = null
    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        Admob.getInstance().setOpenShowAllAds(false)
        Common.initRemoteConfig { task ->
            if (task.isSuccessful) {
                val updated = task.result as Boolean
                if (updated) {
                    setupConfigRemote()
                }
                checkUMP()
            } else {
                checkUMP()
            }
        }
    }

    private fun checkUMP() {
        val adsConsentManager = AdsConsentManager(this)
        adsConsentManager.requestUMP { b ->
            if (b) {
                AppOpenManager.getInstance().initApi(application);
                AppOpenManager.getInstance()
                    .disableAppResumeWithActivity(SplashActivity::class.java)
            }
            handleOpenApp()
            initMediation(b)
        }

    }

    private fun handleOpenApp() {
        Admob.getInstance()
            .setOpenShowAllAds(SharePrefRemote.get_config(this, SharePrefRemote.ads_visibility))
        Admob.getInstance().setTimeInterval(
            SharePrefRemote.get_config_long(
                this,
                SharePrefRemote.interval_between_interstitial
            ) * 1000
        )
        if (!SharePrefRemote.get_config(this, SharePrefRemote.appopen_resume)) {
            AppOpenManager.getInstance().disableAppResume()
            AppOpenManager.getInstance().setAppResumeAdId("")
        }

        api()
    }

    private fun setupConfigRemote() {
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.ads_visibility,
            Common.getRemoteConfigBoolean("ads_visibility")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.inter_splash,
            Common.getRemoteConfigBoolean("inter_splash")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_language,
            Common.getRemoteConfigBoolean("native_language")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_intro,
            Common.getRemoteConfigBoolean("native_intro")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.inter_intro,
            Common.getRemoteConfigBoolean("inter_intro")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.banner_home,
            Common.getRemoteConfigBoolean("banner_home")
        )
        SharePrefRemote.set_config_long(
            this,
            SharePrefRemote.interval_between_interstitial,
            Common.getRemoteConfigLong("interval_between_interstitial")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.appopen_resume,
            Common.getRemoteConfigBoolean("appopen_resume")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_permission,
            Common.getRemoteConfigBoolean("native_permission")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_tutorial,
            Common.getRemoteConfigBoolean("native_tutorial")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_category,
            Common.getRemoteConfigBoolean("native_category")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.inter_category,
            Common.getRemoteConfigBoolean("inter_category")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.banner_draw,
            Common.getRemoteConfigBoolean("banner_draw")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.inter_draw,
            Common.getRemoteConfigBoolean("inter_draw")
        )

    }

    override fun viewListener() {
    }

    private fun api() {
        adCallback = object : AdCallback() {
            override fun onNextAction() {
                super.onNextAction()
                startIntro()
            }

            override fun onAdFailedToLoad(i: LoadAdError?) {
                super.onAdFailedToLoad(i)
                startIntro()
            }

            override fun onAdFailedToShow(adError: AdError?) {
                super.onAdFailedToShow(adError)
                startIntro()
            }
        }

        interCallback = object : InterCallback() {
            override fun onNextAction() {
                super.onNextAction()
                showActivity(LanguageStartActivity::class.java)
            }
        }
        if (AdsConsentManager.getConsentResult(this@SplashActivity)) {
            if (SharePrefRemote.get_config(
                    this@SplashActivity,
                    SharePrefRemote.appopen_resume
                )
            ) {
                AppOpenManager.getInstance()
                    .init(application, getString(R.string.appopen_resume))
            }
            adsSplash = AdsSplash.init(
                SharePrefRemote.get_config(
                    this@SplashActivity,
                    SharePrefRemote.inter_splash
                ), false,
                SharePrefRemote.get_config_string(
                    this@SplashActivity,
                    "100_0"
                )
            )
            adsSplash?.showAdsSplashApi(
                this@SplashActivity,
                adCallback,
                interCallback
            )
        } else {
            coroutineScope.launch {
                delay(3000)
                startIntro()
            }
        }
    }

    override fun dataObservable() {
    }

    private fun startIntro() {
        showActivity(LanguageStartActivity::class.java)
        finish()
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        if (adsSplash != null && AdsConsentManager.getConsentResult(this@SplashActivity)) {
            adsSplash?.onCheckShowSplashWhenFail(this, adCallback, interCallback)
        }
    }

    private fun initMediation(canRequestAds: Boolean) {
        VunglePrivacySettings.setGDPRStatus(canRequestAds, null)
        AppLovinPrivacySettings.setHasUserConsent(canRequestAds, this)
        IronSource.setConsent(canRequestAds)
        MBridgeSDKFactory.getMBridgeSDK().setConsentStatus(
            this,
            if (canRequestAds) MBridgeConstans.IS_SWITCH_ON else MBridgeConstans.IS_SWITCH_OFF
        )
        val gdprMetaData = MetaData(this)
        gdprMetaData["gdpr.consent"] = canRequestAds
        gdprMetaData.commit()
    }
}