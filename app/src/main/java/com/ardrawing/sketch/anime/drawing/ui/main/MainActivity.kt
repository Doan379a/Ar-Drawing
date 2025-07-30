package com.ardrawing.sketch.anime.drawing.ui.main

import android.os.Bundle
import android.view.View
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.BannerGravity
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.databinding.ActivityMainBinding
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote


class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun setViewBinding(): ActivityMainBinding {
       return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        if (SharePrefRemote.get_config(
                this,
                SharePrefRemote.banner_home
            ) && AdsConsentManager.getConsentResult(this)
        ) {
            Admob.getInstance().loadCollapsibleBanner(
                this,
                getString(R.string.banner_home),
                BannerGravity.bottom
            )
            binding.include.visibility = View.VISIBLE
        } else {
            binding.include.visibility = View.GONE
        }
        val pagerAdapter = MainViewPageAdapter(supportFragmentManager)
        binding.viewPage.adapter = pagerAdapter
        setColorTab(0)
    }

    override fun viewListener() {
        binding.tvHome.setOnClickListener {
            binding.tvTitleHome.visibility= View.VISIBLE
            binding.tvTitle.visibility= View.GONE
            binding.viewPage.currentItem = 0
            setColorTab(0)
        }
        binding.tvFavorite.setOnClickListener {
            binding.tvTitleHome.visibility= View.GONE
            binding.tvTitle.visibility= View.VISIBLE
            binding.tvTitle.setText(R.string.favorite)
            binding.viewPage.currentItem = 1
            setColorTab(1)
        }
        binding.tvSetting.setOnClickListener {
            binding.tvTitleHome.visibility= View.GONE
            binding.tvTitle.visibility= View.VISIBLE
            binding.tvTitle.setText(R.string.setting)
            binding.viewPage.currentItem = 2
            setColorTab(2)
        }
    }

    override fun dataObservable() {

    }
    private fun setColorTab(i: Int) {
        when (i) {
            0 -> {
                binding.tvHome.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_selected, 0, 0)
                binding.tvHome.setTextColor(getColor(R.color.color_FF6F89))
                binding.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_heart,0,0)
                binding.tvFavorite.setTextColor(getColor(R.color.color_8F9DAA))
                binding.tvSetting.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_setting,0,0)
                binding.tvSetting.setTextColor(getColor(R.color.color_8F9DAA))
            }

            1 -> {
                binding.tvHome.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home, 0, 0)
                binding.tvHome.setTextColor(getColor(R.color.color_8F9DAA))
                binding.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_heart_selected,0,0)
                binding.tvFavorite.setTextColor(getColor(R.color.color_FF6F89))
                binding.tvSetting.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_setting,0,0)
                binding.tvSetting.setTextColor(getColor(R.color.color_8F9DAA))
            }
            2 -> {
                binding.tvHome.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home, 0, 0)
                binding.tvHome.setTextColor(getColor(R.color.color_8F9DAA))
                binding.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_heart,0,0)
                binding.tvFavorite.setTextColor(getColor(R.color.color_8F9DAA))
                binding.tvSetting.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_setting_selected,0,0)
                binding.tvSetting.setTextColor(getColor(R.color.color_FF6F89))
            }
        }
    }

}

