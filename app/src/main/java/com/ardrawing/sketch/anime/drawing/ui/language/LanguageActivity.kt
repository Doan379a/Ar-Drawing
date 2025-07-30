package com.ardrawing.sketch.anime.drawing.ui.language

import android.content.Intent
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.databinding.ActivityLanguageBinding
import com.ardrawing.sketch.anime.drawing.ui.main.MainActivity
import com.ardrawing.sketch.anime.drawing.utils.SystemUtil
import com.ardrawing.sketch.anime.drawing.widget.tap
class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {

    private lateinit var adapter: LanguageAdapter
    private lateinit var iCode: String

    override fun setViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    override fun initView() {
        iCode = SystemUtil.getPreLanguage(this)
        adapter = LanguageAdapter(this, onClick = {
            adapter.setCheck(it.code)
            iCode = it.code
        })
        binding.recyclerView.adapter = adapter
    }

    override fun viewListener() {
        binding.ivBack.tap { finish() }
        binding.ivDone.tap {
            SystemUtil.saveLocale(baseContext, iCode)
            back()
        }
    }

    override fun dataObservable() {
        val list = SystemUtil.listLanguage()
        list.forEach {
            if (it.code == SystemUtil.getPreLanguage(this)) {
                it.active = true
                return@forEach
            }
        }
        adapter.addList(list)
    }

    private fun back() {
        finishAffinity()
        val intent = Intent(this@LanguageActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
