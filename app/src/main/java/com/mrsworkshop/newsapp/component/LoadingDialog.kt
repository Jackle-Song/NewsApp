package com.mrsworkshop.newsapp.component

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mrsworkshop.newsapp.R

class LoadingDialog: DialogFragment() {

    companion object {
        private const val FRAGMENT_TAG = "loadingDialog"

        private fun newInstance() = LoadingDialog()

        fun show(supportFragmentManager: FragmentManager?): LoadingDialog {
            val dialog = newInstance()
            // prevent dismiss by user click
            dialog.isCancelable = false
            supportFragmentManager?.let { dialog.show(it, FRAGMENT_TAG) }
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // make white background transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return activity?.layoutInflater?.inflate(R.layout.layout_loading_view, container)
    }

}