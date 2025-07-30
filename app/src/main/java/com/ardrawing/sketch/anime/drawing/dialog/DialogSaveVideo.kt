package com.ardrawing.sketch.anime.drawing.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.ardrawing.sketch.anime.drawing.base.BaseDialog
import com.ardrawing.sketch.anime.drawing.databinding.DialogSaveVideoBinding

class DialogSaveVideo(
    activity1: Activity,
    private var onSave:()->Unit,
    private var onDiscard:()->Unit
) : BaseDialog<DialogSaveVideoBinding>(activity1,true) {
    override fun getContentView(): DialogSaveVideoBinding {
        return DialogSaveVideoBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {

    }

    override fun bindView() {
        binding.btnSave.setOnClickListener {
            onSave.invoke()
            dismiss()
        }
        binding.btnDiscard.setOnClickListener {
            onDiscard.invoke()
            dismiss()
        }
    }
}