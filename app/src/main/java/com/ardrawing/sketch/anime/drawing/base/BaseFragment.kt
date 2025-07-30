package com.ardrawing.sketch.anime.drawing.base
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ardrawing.sketch.anime.drawing.R

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding: T
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): T
    private var backgroundView: FrameLayout? = null
    private var loadingLayout: View? = null
    protected open fun showLoading() {
        if (loadingLayout == null) {
            val li = LayoutInflater.from(requireActivity())
            loadingLayout = li.inflate(R.layout.layout_loading_progress, null, false)
            backgroundView = loadingLayout!!.findViewById(R.id.root)
            val rootView = requireActivity().findViewById<ViewGroup>(android.R.id.content)
            rootView.addView(
                loadingLayout,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            backgroundView!!.isClickable = true
        }
    }

    protected open fun hideLoading() {
        if (loadingLayout != null) {
            val rootView = requireActivity().findViewById<ViewGroup>(android.R.id.content)
            rootView.removeView(loadingLayout)
            if (backgroundView != null) backgroundView!!.isClickable = false
            loadingLayout = null
        }
    }

}
