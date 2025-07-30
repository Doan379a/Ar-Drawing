package com.ardrawing.sketch.anime.drawing.ui.favorite


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.database.FavoriteTable
import com.ardrawing.sketch.anime.drawing.databinding.ItemRcvDetailBinding


class FavoriteAdapter(
    private var favoritesList: List<FavoriteTable>,
    private val onDeleteClick: (FavoriteTable) -> Unit,
    private val onclickItem: (Int) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: ItemRcvDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FavoriteTable) {
            binding.img.setImageResource(item.imgResId)

            binding.btnFavorite.setImageResource(R.drawable.ic_heart_selected)
            binding.btnFavorite.setOnClickListener {
                onDeleteClick(item)
            }
            binding.root.setOnClickListener {
                onclickItem(item.imgResId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemRcvDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoritesList[position])
    }

    override fun getItemCount(): Int = favoritesList.size

    fun updateList(newList: List<FavoriteTable>) {
        favoritesList = newList
        notifyDataSetChanged()
    }
}
