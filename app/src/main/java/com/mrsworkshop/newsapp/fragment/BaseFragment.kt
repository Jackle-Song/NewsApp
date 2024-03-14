package com.mrsworkshop.newsapp.fragment

import android.content.Context
import android.view.inputmethod.InputMethodManager
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

    fun dismissKeyBoard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}