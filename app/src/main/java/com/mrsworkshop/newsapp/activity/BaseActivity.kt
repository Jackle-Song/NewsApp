package com.mrsworkshop.newsapp.activity

import androidx.appcompat.app.AppCompatActivity
import com.mrsworkshop.newsapp.component.LoadingDialog

open class BaseActivity : AppCompatActivity() {

    private lateinit var loadingViewDialog: LoadingDialog

    fun showLoadingViewDialog() {
        loadingViewDialog = LoadingDialog.show(supportFragmentManager)
    }

    fun dismissLoadingViewDialog() {
        loadingViewDialog.dismiss()
    }

}