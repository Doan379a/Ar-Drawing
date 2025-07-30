package com.ardrawing.sketch.anime.drawing.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardrawing.sketch.anime.drawing.base.BaseFragment
import com.ardrawing.sketch.anime.drawing.database.AppDatabase

import com.ardrawing.sketch.anime.drawing.databinding.FragmentFavoriteBinding
import androidx.lifecycle.Observer
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.database.FavoriteViewModel
import com.ardrawing.sketch.anime.drawing.database.FavoriteViewModelFactory
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefUtilsKotlin
import com.ardrawing.sketch.anime.drawing.ui.ar.ArCameraActivity
import com.ardrawing.sketch.anime.drawing.ui.tutorial.TutorialActivity

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {

    private lateinit var favoriteAdapter: FavoriteAdapter
    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory(AppDatabase.getInstance(requireActivity()))
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteAdapter = FavoriteAdapter(emptyList(), { item ->
            favoriteViewModel.deleteFavorite(item)
        }) { img ->
            if (SharePrefUtilsKotlin.isTutorial(requireActivity())) {
                Log.d("imgDraw", "imgDraw detail: ${img}")
                val intent = Intent(requireActivity(), TutorialActivity::class.java).apply {
                    putExtra("Img_Draw", img)
                }
                startActivity(intent)
            } else {
                Log.d("imgDraw", "imgDraw detail2: ${R.drawable.img_intro3}")
                Log.d("imgDraw", "imgDraw detail2: ${img}")
                val intent = Intent(requireActivity(), ArCameraActivity::class.java).apply {
                    putExtra("Img_Draw", img)
                }
                startActivity(intent)
            }
        }
        binding.rcvView.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rcvView.adapter = favoriteAdapter

        favoriteViewModel.favorites.observe(viewLifecycleOwner, Observer { favorites ->
            favoriteAdapter.updateList(favorites)
            if (favorites.isEmpty()) {
                binding.rcvView.visibility = View.GONE
                binding.imgEmpty.visibility = View.VISIBLE
            } else {
                binding.rcvView.visibility = View.VISIBLE
                binding.imgEmpty.visibility = View.GONE
            }
        })
    }
}