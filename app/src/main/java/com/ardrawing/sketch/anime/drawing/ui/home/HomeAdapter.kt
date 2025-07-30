package com.ardrawing.sketch.anime.drawing.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ardrawing.sketch.anime.drawing.databinding.ItemRcvHomeBinding


class HomeAdapter(
    private val context: Context,
    private val list: List<HomeModel>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<HomeAdapter.ViewHoler>() {

    inner class ViewHoler(val binding: ItemRcvHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeModel) {
            binding.img.setImageResource(item.img)
            binding.txtName.setText(item.title)
            binding.root.setOnClickListener {
                onClick(item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoler {
        val binding = ItemRcvHomeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHoler(binding)
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ViewHoler, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem)
    }
}