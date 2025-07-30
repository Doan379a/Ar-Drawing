package com.ardrawing.sketch.anime.drawing.ui.permisson

import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.AppOpenManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.databinding.ActivityPermissonBinding
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote
import com.ardrawing.sketch.anime.drawing.ui.main.MainActivity

class PermissonActivity : BaseActivity<ActivityPermissonBinding>() {
    private var nextActivityClass: Class<*>? = null
    private val CAMERA_AND_MICROPHONE_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    )

    private var allPermissionsGranted = false

    override fun setViewBinding(): ActivityPermissonBinding {
        return ActivityPermissonBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
    }

    override fun viewListener() {
        binding.btncontinue.setOnClickListener {
            if (allPermissionsGranted) {
                if (checkPermissions()){
                    nextActivityClass = MainActivity::class.java
                    showActivity(nextActivityClass!!)
                    finish()
                }else{
                    showDialogPermission(CAMERA_AND_MICROPHONE_PERMISSIONS)
                }
            } else {
                showDialogPermission(CAMERA_AND_MICROPHONE_PERMISSIONS)
            }
        }
    }
    private fun checkPermissions(): Boolean {
        return CAMERA_AND_MICROPHONE_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    override fun onPermissionGranted() {
        allPermissionsGranted = true

    }

    override fun onPermissionDenied() {
        super.onPermissionDenied()

    }

    override fun onResume() {
        AppOpenManager.getInstance().enableAppResumeWithActivity(this.javaClass)
        super.onResume()

    }

    override fun dataObservable() {

    }
    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(this, SharePrefRemote.native_permission) &&
                AdsConsentManager.getConsentResult(this)
            ) {
                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    this,
                    binding.frAds,
                    R.layout.ads_native_small_bottom_shimer,
                    R.layout.layout_native_small_bottom
                )
                nativeBuilder.setListIdAd(listOf(getString(R.string.native_permission)))
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
