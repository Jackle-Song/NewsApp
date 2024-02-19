package com.mrsworkshop.newsapp.activity

import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mrsworkshop.newsapp.component.LoadingDialog

open class BaseActivity : AppCompatActivity() {

    private lateinit var loadingViewDialog: LoadingDialog

    fun setUpStatusBarThemeColor(themColor: Int, isLightIcon : Boolean) {
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = applicationContext.let { ContextCompat.getColor(applicationContext, themColor) }

        if (isLightIcon) {
            // MARK : Status Bar Icon to WHITE
            window.decorView.systemUiVisibility = 0
        }
        else {
            // MARK : Status Bar Icon to BLACK
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun showLoadingViewDialog() {
        loadingViewDialog = LoadingDialog.show(supportFragmentManager)
    }

    fun dismissLoadingViewDialog() {
        loadingViewDialog.dismiss()
    }

}