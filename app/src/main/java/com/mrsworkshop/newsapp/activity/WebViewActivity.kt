package com.mrsworkshop.newsapp.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import com.mrsworkshop.newsapp.databinding.ActivityWebviewBinding
import com.mrsworkshop.newsapp.utils.Constant

class WebViewActivity : BaseActivity() {
    private lateinit var binding: ActivityWebviewBinding

    private var webViewUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webViewUrl = intent.getStringExtra(Constant.INTENT_WEB_VIEW_URL)

        initUI()
        setupComponentListener()
    }

    /**
     * private function
     */

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUI() {
        binding.webViewNewsContent.apply {
            webViewClient = MyWebViewClient()
            webChromeClient = MyWebChromeClient()
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
        }
        if (!webViewUrl.isNullOrEmpty()) {
            binding.webViewNewsContent.loadUrl(webViewUrl ?: "")
        }
    }

    private fun setupComponentListener() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webViewNewsContent.canGoBack()) {
                    binding.webViewNewsContent.goBack()
                }
                else {
                    finish()
                }
            }
        })

        binding.layoutSwipeRefresh.setOnRefreshListener {
            binding.webViewNewsContent.reload()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.webViewNewsContent.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                binding.layoutSwipeRefresh.isEnabled = scrollY == 0
            }
        }
    }

    /**
     * private inner function
     */

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressBarWebView.visibility = ProgressBar.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressBarWebView.visibility = ProgressBar.GONE
            binding.layoutSwipeRefresh.isRefreshing = false
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.progressBarWebView.progress = newProgress
        }
    }
}