package com.ardrawing.sketch.anime.drawing.ui.detail

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.ads.InterAdHelper
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.database.AppDatabase

import com.ardrawing.sketch.anime.drawing.databinding.ActivityDetailBinding
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefUtilsKotlin
import com.ardrawing.sketch.anime.drawing.ui.ar.ArCameraActivity
import com.ardrawing.sketch.anime.drawing.ui.home.listHome
import com.ardrawing.sketch.anime.drawing.ui.tutorial.TutorialActivity

class DetailActivity : BaseActivity<ActivityDetailBinding>() {
    private lateinit var homedetailadapter: DetailAdapter


    override fun setViewBinding(): ActivityDetailBinding {
        return ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
        val homeId = intent.getIntExtra("HOME_ID", -1)
//        Toast.makeText(this, "ID detail: $homeId", Toast.LENGTH_SHORT).show()
        val selectedHomeModel = listHome.find { it.id == homeId }
        val detailList = selectedHomeModel?.list ?: emptyList()
        binding.txtTitle.setText(selectedHomeModel!!.title)
        homedetailadapter = DetailAdapter(detailList, lifecycleScope) { img ->
            if (SharePrefUtilsKotlin.isTutorial(this)) {
                Log.d("imgDraw", "imgDraw detail: $img")
                val intent = Intent(this, TutorialActivity::class.java).apply {
                    putExtra("Img_Draw", img)
                }
                startActivity(intent)
            } else {
                Log.d("imgDraw", "imgDraw detail2: $img")
                showLoading()
                InterAdHelper.showInterAd(
                    this, SharePrefRemote.get_config(
                        this,
                        SharePrefRemote.inter_category
                    ),
                    getString(R.string.inter_category)
                ) {
                    val intent = Intent(this, ArCameraActivity::class.java).apply {
                        putExtra("Img_Draw", img)
                    }
                    startActivity(intent)
                    hideLoading()
                }
            }

        }

        binding.rcvView.layoutManager = GridLayoutManager(this, 2)
        binding.rcvView.adapter = homedetailadapter
    }

    override fun viewListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun dataObservable() {}
    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(this, SharePrefRemote.native_category) &&
                AdsConsentManager.getConsentResult(this)
            ) {
                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    this,
                    binding.frAds,
                    R.layout.ads_native_small_bottom_shimer,
                    R.layout.layout_native_small_bottom
                )
                nativeBuilder.setListIdAd(listOf(getString(R.string.native_category)))
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
