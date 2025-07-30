package com.ardrawing.sketch.anime.drawing.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.amazic.ads.util.AppOpenManager
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.ads.InterAdHelper
import com.ardrawing.sketch.anime.drawing.base.BaseFragment
import com.ardrawing.sketch.anime.drawing.databinding.FragmentHomeBinding
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote
import com.ardrawing.sketch.anime.drawing.ui.ar.ArUploadInllustrationActivity
import com.ardrawing.sketch.anime.drawing.ui.detail.DetailActivity

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeAdapter: HomeAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        initView(binding)
        viewListener(binding)
        return binding

    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }

    fun initView(binding: FragmentHomeBinding) {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvView.layoutManager = layoutManager
        homeAdapter = HomeAdapter(requireActivity(), listHome) { homeId ->
            showLoading()
            InterAdHelper.showInterAd(
                requireActivity(), SharePrefRemote.get_config(
                    requireActivity(),
                    SharePrefRemote.inter_home
                ),
                getString(R.string.inter_home)
            ) {
                val intent = Intent(requireActivity(), DetailActivity::class.java).apply {
                    putExtra("HOME_ID", homeId)
                }
                startActivity(intent)
                hideLoading()
            }
        }
        binding.rcvView.adapter = homeAdapter
    }

    fun viewListener(binding: FragmentHomeBinding) {
        binding.layouUpload.setOnClickListener {
            startPhotoPicker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PICK_IMAGE -> if (resultCode == RESULT_OK) {
                data?.data?.let { uri ->
                    try {
                        val imgUri = data?.data
                        if (imgUri != null) {
                            val intent =
                                Intent(requireActivity(), ArUploadInllustrationActivity::class.java)
                            intent.putExtra("image_uri", imgUri.toString())
                            startActivity(intent)
                        } else {
                            Log.e("ArCameraActivity", "khng co link anh ")
                        }
                    } catch (e: Exception) {
                        Log.e("ArCameraActivity", "Error loading image: ${e.message}")
                    }
                }
            } else {

                Log.e("ArCameraActivity", "khong chon anh")
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startPhotoPicker() {
        AppOpenManager.getInstance().disableAppResumeWithActivity(requireActivity().javaClass)
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE)
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(requireActivity().javaClass)

    }
}