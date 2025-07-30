package com.ardrawing.sketch.anime.drawing.ui.language_start

import android.content.Intent
import android.view.View
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.databinding.ActivityLanguageStartBinding
import com.ardrawing.sketch.anime.drawing.model.LanguageModel
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote
import com.ardrawing.sketch.anime.drawing.ui.intro.IntroActivity
import com.ardrawing.sketch.anime.drawing.utils.SystemUtil
import java.util.Locale
import com.ardrawing.sketch.anime.drawing.widget.visible
import com.ardrawing.sketch.anime.drawing.widget.tap
class LanguageStartActivity : BaseActivity<ActivityLanguageStartBinding>() {

    private lateinit var adapter: LanguageStartAdapter
    private var listLanguage: ArrayList<LanguageModel> = ArrayList()
    private var codeLang = ""

    override fun setViewBinding(): ActivityLanguageStartBinding {
        return ActivityLanguageStartBinding.inflate(layoutInflater)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun initView() {
        loadNative()
        adapter = LanguageStartAdapter(this, onClick = {
            codeLang = it.code
            adapter.setCheck(it.code)
            binding.ivDone.visible()
        })
        binding.recyclerView.adapter = adapter

    }

    override fun viewListener() {
        binding.ivDone.tap {
            SystemUtil.saveLocale(baseContext, codeLang)
            startActivity(Intent(this@LanguageStartActivity, IntroActivity::class.java))
            finish()
        }
    }

    override fun dataObservable() {
        setCodeLanguage()
        initData()
    }

    private fun setCodeLanguage() {
        //language
        val codeLangDefault = Locale.getDefault().language
        val langDefault = arrayOf("fr", "pt", "es", "de", "in", "en", "hi", "vi", "ja") //"hi" ấn độ
        codeLang =
            if (SystemUtil.getPreLanguage(this).equals(""))
                if (!mutableListOf(*langDefault)
                        .contains(codeLangDefault)
                ) {
                    "en"
                } else {
                    codeLangDefault
                } else {
                SystemUtil.getPreLanguage(this)
            }
    }

    private fun initData() {
        var pos = 0
        listLanguage.clear()
        listLanguage.addAll(SystemUtil.listLanguage())
//        listLanguage.forEachIndexed { index, languageModel ->
//            if (languageModel.code == codeLang) {
//                pos = index
//                return@forEachIndexed
//            }
//        }
//        val temp = listLanguage[pos]
//        temp.active = true
//        listLanguage.removeAt(pos)
//        listLanguage.add(0, temp)
        adapter.addList(listLanguage)
    }
    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(this, SharePrefRemote.native_language) &&
                AdsConsentManager.getConsentResult(this)
            ) {
                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    this,
                    binding.frAds,
                    R.layout.ads_native_large_bottom_shimer,
                    R.layout.layout_native_large_bottom
                )
                nativeBuilder.setListIdAd(listOf(getString(R.string.native_language)))
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