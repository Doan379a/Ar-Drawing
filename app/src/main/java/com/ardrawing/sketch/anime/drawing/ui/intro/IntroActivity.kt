package com.ardrawing.sketch.anime.drawing.ui.intro

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.amazic.ads.event.AdmobEvent
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.ads.InterAdHelper
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.databinding.ActivityIntroBinding
import com.ardrawing.sketch.anime.drawing.model.IntroModel
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote
import com.ardrawing.sketch.anime.drawing.ui.main.MainActivity
import com.ardrawing.sketch.anime.drawing.ui.permisson.PermissonActivity
import com.ardrawing.sketch.anime.drawing.widget.gone
import com.ardrawing.sketch.anime.drawing.widget.visible

class IntroActivity : BaseActivity<ActivityIntroBinding>() {

    var isFirst = true
    private lateinit var dots: Array<ImageView?>
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val myPageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (isFirst) {
                    isFirst = false
                    return
                }
                addBottomDots(position)
                when (position) {
                    0 -> {
                        binding.frAds.visible()
                        AdmobEvent.logEvent(this@IntroActivity, "intro1_view", Bundle())

                    }

                    1 -> {
                        binding.frAds.gone()
                        AdmobEvent.logEvent(this@IntroActivity, "intro2_view", Bundle())
                    }

                    2 -> {
                        binding.frAds.visible()
                        AdmobEvent.logEvent(this@IntroActivity, "intro2_view", Bundle())
                    }

                    else -> {
                        binding.frAds.gone()
                        AdmobEvent.logEvent(this@IntroActivity, "intro3_view", Bundle())
                    }
                }
            }
        }
    private val CAMERA_AND_MICROPHONE_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
    )
    private val countPageIntro = 3

    override fun setViewBinding(): ActivityIntroBinding {
        return ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        loadNative()
        val data = mutableListOf(
            IntroModel(
                R.drawable.img_intro1,
                R.string.content_intro1,
            ),
            IntroModel(
                R.drawable.img_intro2,
                R.string.content_intro2,
            ),
            IntroModel(
                R.drawable.img_intro3,
                R.string.content_intro3,
            )
        )
        viewPagerAdapter = ViewPagerAdapter(this, data)
        binding.viewPager2.apply {
            adapter = viewPagerAdapter
            registerOnPageChangeCallback(myPageChangeCallback)
        }
        addBottomDots(0)
    }

    override fun viewListener() {
        binding.btnNextTutorial.setOnClickListener {
            if (binding.viewPager2.currentItem == countPageIntro - 1) {
                it.isEnabled = false
                startNextScreen()
            } else
                binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1
        }
    }

    private fun startNextScreen() {
        showLoading()
        InterAdHelper.showInterAd(
            this@IntroActivity, SharePrefRemote.get_config(
                this@IntroActivity,
                SharePrefRemote.inter_intro
            ),
            getString(R.string.inter_intro)
        ) {
            if (checkPermissions())
                showActivity(MainActivity::class.java)
            else
                showActivity(PermissonActivity::class.java)
            finishAffinity()
            hideLoading()
        }
    }

    private fun checkPermissions(): Boolean {
        return CAMERA_AND_MICROPHONE_PERMISSIONS.all { permission ->
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun dataObservable() {
        addBottomDots(0)
    }


    private fun addBottomDots(currentPage: Int) {
        binding.linearDots.removeAllViews()
        dots = arrayOfNulls(countPageIntro)
        for (i in 0 until countPageIntro) {
            dots[i] = ImageView(this)
            if (i == currentPage)
                dots[i]!!.setImageResource(R.drawable.ic_intro_selected)
            else
                dots[i]!!.setImageResource(R.drawable.ic_intro_no_select)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.linearDots.addView(dots[i], params)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(this, SharePrefRemote.native_intro) &&
                AdsConsentManager.getConsentResult(this)
            ) {
                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    this,
                    binding.frAds,
                    R.layout.ads_native_small_bottom_shimer,
                    R.layout.layout_native_small_bottom
                )
                nativeBuilder.setListIdAd(listOf(getString(R.string.native_intro)))
                val nativeManager = NativeManager(this, this, nativeBuilder)
            } else {
                binding.frAds.visibility = View.GONE
                binding.frAds.removeAllViews()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            binding.frAds.visibility = View.GONE
            binding.frAds.removeAllViews()
        }
    }
}