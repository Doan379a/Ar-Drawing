package com.ardrawing.sketch.anime.drawing.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.ardrawing.sketch.anime.drawing.base.BaseDialog
import com.ardrawing.sketch.anime.drawing.databinding.DialogPermissionBinding
import com.ardrawing.sketch.anime.drawing.widget.tap
class PermissionDialog(
    activity1: Activity,
    private var action: () -> Unit
) : BaseDialog<DialogPermissionBinding>(activity1, true) {


    override fun getContentView(): DialogPermissionBinding {
        return DialogPermissionBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
    }

    override fun bindView() {
        binding.apply {
            txtGo.tap {
                action.invoke()
                dismiss()
            }
        }
    }
}