package com.ardrawing.sketch.anime.drawing.ui.setting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ardrawing.sketch.anime.drawing.base.BaseFragment
import com.ardrawing.sketch.anime.drawing.databinding.FragmentSettingBinding
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefUtils
import com.ardrawing.sketch.anime.drawing.ui.language.LanguageActivity
import com.ardrawing.sketch.anime.drawing.utils.HelperMenu
import com.ardrawing.sketch.anime.drawing.widget.gone
import com.ardrawing.sketch.anime.drawing.widget.tap


class SettingFragment : BaseFragment<FragmentSettingBinding>(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var helperMenu: HelperMenu? = null
    private lateinit var prefs: SharedPreferences


    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        val binding = FragmentSettingBinding.inflate(inflater, container, false)
        initView(binding)
        viewListener(binding)
        return binding
    }

    private fun initView(binding: FragmentSettingBinding) {
        if (SharePrefUtils.isRated(requireActivity()))
            binding.tvRate.gone()
        helperMenu = HelperMenu(requireActivity())

         prefs = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(this)

        val packageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
        val appVersion = "Version ${packageInfo.versionName}"
        binding.tvVersion.text = appVersion
    }

    private fun viewListener(binding: FragmentSettingBinding) {
        binding.apply {
            tvRate.tap { helperMenu?.showDialogRate(false) }
//            tvFeedback.tap { helperMenu?.showDialogFeedback() }
            tvShare.tap { helperMenu?.showShareApp() }
            tvPolicy.tap { helperMenu?.showPolicy() }
            tvLanguage.tap {
                val intent = Intent(requireActivity(), LanguageActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == null)
            return

        if (SharePrefUtils.isRated(requireActivity()))
            binding.tvRate.gone()
    }
    override fun onDestroyView() {
        super.onDestroyView()

        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }
}