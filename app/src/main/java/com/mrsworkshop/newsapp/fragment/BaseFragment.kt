package com.mrsworkshop.newsapp.fragment

import androidx.fragment.app.Fragment
import com.mrsworkshop.newsapp.component.LoadingDialog

open class BaseFragment : Fragment() {
    private lateinit var loadingViewDialog: LoadingDialog

    fun showLoadingViewDialog() {
        loadingViewDialog = LoadingDialog.show(parentFragmentManager)
    }

    fun dismissLoadingViewDialog() {
        loadingViewDialog.dismiss()
    }
}