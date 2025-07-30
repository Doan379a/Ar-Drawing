package com.ardrawing.sketch.anime.drawing.ui.language_start

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.base.BaseAdapter
import com.ardrawing.sketch.anime.drawing.databinding.ItemRcvLanguageBinding
import com.ardrawing.sketch.anime.drawing.model.LanguageModel

class LanguageStartAdapter(
    val context: Context,
    val onClick: (lang: LanguageModel) -> Unit
) : BaseAdapter<ItemRcvLanguageBinding, LanguageModel>() {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemRcvLanguageBinding {
        return ItemRcvLanguageBinding.inflate(inflater, parent, false)
    }

    override fun createVH(binding: ItemRcvLanguageBinding): RecyclerView.ViewHolder =
        LanguageVH(binding)


    inner class LanguageVH(binding: ItemRcvLanguageBinding) : BaseVH<LanguageModel>(binding) {
        override fun onItemClickListener(data: LanguageModel) {
            super.onItemClickListener(data)
            onClick.invoke(data)
        }

        override fun bind(data: LanguageModel) {
            super.bind(data)
            binding.txtName.text = data.name
            if (data.active) {
                binding.layoutItem.setBackgroundResource(R.drawable.bg_item_language_active)
                binding.txtName.setTextColor(context.resources.getColor(R.color.white))
            } else {
                binding.txtName.setTextColor(context.resources.getColor(R.color.color_1E232E))
                binding.layoutItem.setBackgroundResource(R.drawable.brg_item_language)
            }
            when (data.code) {
                "en" -> binding.icLang.setImageResource(R.drawable.flag_en)
                "de" -> binding.icLang.setImageResource(R.drawable.flag_ger)
                "es" -> binding.icLang.setImageResource(R.drawable.flag_span)
                "fr" -> binding.icLang.setImageResource(R.drawable.flag_fra)
                "hi" -> binding.icLang.setImageResource(R.drawable.flag_hindi)
                "in" -> binding.icLang.setImageResource(R.drawable.flag_indonesia)
                "pt" -> binding.icLang.setImageResource(R.drawable.flag_port)
                "vi" -> binding.icLang.setImageResource(R.drawable.flag_vi)
                "ja" -> binding.icLang.setImageResource(R.drawable.flag_jp)
            }
        }
    }


    fun setCheck(code: String) {
        for (item in listData) {
            item.active = item.code == code
        }
        notifyDataSetChanged()
    }
}