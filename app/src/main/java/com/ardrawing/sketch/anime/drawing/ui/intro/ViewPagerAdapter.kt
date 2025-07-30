package com.ardrawing.sketch.anime.drawing.ui.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.base.BaseAdapter
import com.ardrawing.sketch.anime.drawing.databinding.ItemIntroBinding

import com.ardrawing.sketch.anime.drawing.model.IntroModel

class ViewPagerAdapter(val context: Context, list: MutableList<IntroModel>) :
    BaseAdapter<ItemIntroBinding, IntroModel>() {
    init {
        listData = list
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemIntroBinding = ItemIntroBinding.inflate(inflater, parent, false)

    override fun createVH(binding: ItemIntroBinding): RecyclerView.ViewHolder =
        IntroVH(binding)

    inner class IntroVH(binding: ItemIntroBinding) : BaseVH<IntroModel>(binding) {
        override fun bind(data: IntroModel) {
            super.bind(data)
            try {
                binding.ivIntro.setImageResource(data.image)
                binding.tvTitle.setText(data.title)
            } catch (_: Exception) {

            }
        }
    }
}