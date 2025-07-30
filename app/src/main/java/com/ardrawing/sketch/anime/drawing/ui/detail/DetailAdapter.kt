package com.ardrawing.sketch.anime.drawing.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.database.AppDatabase
import com.ardrawing.sketch.anime.drawing.database.FavoriteTable


import com.ardrawing.sketch.anime.drawing.databinding.ItemRcvDetailBinding
import com.ardrawing.sketch.anime.drawing.ui.home.DetailModel
import kotlinx.coroutines.launch


class DetailAdapter(
    private val list: List<DetailModel>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onclickItem: (Int) -> Unit
) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var db: AppDatabase

    inner class ViewHolder(val binding: ItemRcvDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DetailModel) {
            binding.img.setImageResource(item.img)
            db = AppDatabase.getInstance(context)

            lifecycleScope.launch {
                val isFavorite = db.favoriteItemDao().isFavorite(item.img)
                updateFavoriteIcon(isFavorite)
            }

            binding.root.setOnClickListener {
                onclickItem(item.img)
            }


            binding.btnFavorite.setOnClickListener {
                lifecycleScope.launch {
                    val isFavorite = db.favoriteItemDao().isFavorite(item.img)
                    if (isFavorite) {
                        db.favoriteItemDao().delete(FavoriteTable(item.img))
                    } else {
                        db.favoriteItemDao().insert(FavoriteTable(item.img))
                    }
                    updateFavoriteIcon(!isFavorite)
                }
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_selected else R.drawable.ic_favorite
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ItemRcvDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem)
    }
}