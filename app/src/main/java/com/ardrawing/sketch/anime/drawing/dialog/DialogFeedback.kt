package com.ardrawing.sketch.anime.drawing.dialog

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.base.BaseDialog
import com.ardrawing.sketch.anime.drawing.databinding.DialogFeedbackBinding
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefUtils
import com.ardrawing.sketch.anime.drawing.utils.Default

import com.ardrawing.sketch.anime.drawing.widget.tap


class DialogFeedback(activity: Activity) :
    BaseDialog<DialogFeedbackBinding>(activity, true) {

    override fun getContentView(): DialogFeedbackBinding {
        return DialogFeedbackBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
    }

    override fun bindView() {
        binding.apply {
            tvSend.tap {
                if (binding.edtFeedback.text.toString().isEmpty()) {
                    Toast.makeText(activity, R.string.please_send_us, Toast.LENGTH_SHORT).show()
                    return@tap
                }
                dismiss()
                sendMail()
            }
            tvCancel.tap {
                dismiss()
            }
        }
    }

    private fun sendMail() {
        val uriText =
            "mailto:${Default.EMAIL}?subject=${Default.SUBJECT}&" +
                    "body=Content : \n${binding.edtFeedback.text.toString().trim()}".trimIndent()
        val uri = Uri.parse(uriText)
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = uri
        try {
            activity.startActivity(Intent.createChooser(sendIntent, "Send Email"))
            SharePrefUtils.forceRated(
                activity
            )
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                activity,
                R.string.There_is_no_email,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}