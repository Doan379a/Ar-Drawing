package com.ardrawing.sketch.anime.drawing.ui.tutorial

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.databinding.ActivityTutorialBinding
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefUtilsKotlin
import com.ardrawing.sketch.anime.drawing.ui.ar.ArCameraActivity

class TutorialActivity : BaseActivity<ActivityTutorialBinding>() {
    private var ischeck = false
    override fun setViewBinding(): ActivityTutorialBinding {
        return ActivityTutorialBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
        val videoUri: Uri = Uri.parse("android.resource://${packageName}/raw/ar_video2")
        binding.view2.setVideoURI(videoUri)
        binding.view2.setOnPreparedListener { mediaPlayer: MediaPlayer ->
            mediaPlayer.isLooping = true
        }
        binding.view2.start()
    }

    override fun viewListener() {
        val imgDraw = intent.getIntExtra("Img_Draw",-1)
        Log.d("imgDraw", "imgDraw: $imgDraw")
        binding.tvGoit.setOnClickListener{
            if (!ischeck){
                SharePrefUtilsKotlin.forceTutorial(this,true)
                val intent = Intent(this, ArCameraActivity::class.java).apply {
                    putExtra("Img_Draw", imgDraw)
                }
                startActivity(intent)
                finish()
            }else{
                SharePrefUtilsKotlin.forceTutorial(this,false)
                val intent = Intent(this, ArCameraActivity::class.java).apply {
                    putExtra("Img_Draw", imgDraw)
                }
                startActivity(intent)
                finish()
            }

        }
        binding.btnBack.setOnClickListener{
            finish()
        }
        binding.tvDont.setOnClickListener {
            ischeck = !ischeck
            binding.tvDont.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (ischeck) R.drawable.icon_check_tutorial_selected else R.drawable.icon_check_tutorial,
                0,
                0,
                0
            )
        }
    }

    override fun dataObservable() {

    }
    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(this, SharePrefRemote.native_tutorial) &&
                AdsConsentManager.getConsentResult(this)
            ) {
                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    this,
                    binding.frAds,
                    R.layout.ads_native_small_bottom_shimer,
                    R.layout.layout_native_small_bottom
                )
                nativeBuilder.setListIdAd(listOf(getString(R.string.native_tutorial)))
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