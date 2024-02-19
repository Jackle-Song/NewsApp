package com.mrsworkshop.newsapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.mrsworkshop.newsapp.R
import com.mrsworkshop.newsapp.apidata.response.ArticlesDetails
import com.mrsworkshop.newsapp.core.PreferenceCache
import com.mrsworkshop.newsapp.databinding.ActivityNewsDetailsBinding
import com.mrsworkshop.newsapp.helper.ContextWrapper
import com.mrsworkshop.newsapp.utils.Constant

class NewsDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityNewsDetailsBinding
    private lateinit var articlesDetails: ArticlesDetails
    private lateinit var oldPrefLocaleCode : String

    override fun attachBaseContext(newBase: Context) {
        val lang = PreferenceCache(newBase).getSelectedLanguage()
        oldPrefLocaleCode = lang

        super.attachBaseContext(ContextWrapper.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articlesDetailsJson = intent.getStringExtra(Constant.INTENT_NEWS_DETAILS_JSON)
        articlesDetails = Gson().fromJson(articlesDetailsJson, ArticlesDetails::class.java)

        setUpStatusBarThemeColor(R.color.black, true)

        initUI()
        setupComponentListener()
    }

    /**
     * private function
     */

    private fun initUI() {
        if (articlesDetails.urlToImage != null) {
            Glide.with(this@NewsDetailsActivity)
                .load(articlesDetails.urlToImage)
                .placeholder(R.drawable.img_breaking_news)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgNewsDetails)
        }

        if (articlesDetails.author.isNullOrEmpty()) {
            binding.txtNewsDetailsAuthor.visibility = View.GONE
        }
        else {
            binding.txtNewsDetailsAuthor.visibility = View.VISIBLE
            binding.txtNewsDetailsAuthor.text = articlesDetails.author
        }

        if (articlesDetails.title.isNullOrEmpty()) {
            binding.txtNewsDetailsTitle.visibility = View.GONE
        }
        else {
            binding.txtNewsDetailsTitle.visibility = View.VISIBLE
            binding.txtNewsDetailsTitle.text = articlesDetails.title
        }

        if (articlesDetails.description.isNullOrEmpty()) {
            binding.txtNewsDetailsContent.visibility = View.GONE
        }
        else {
            binding.txtNewsDetailsContent.visibility = View.VISIBLE
            binding.txtNewsDetailsContent.text = articlesDetails.description
        }

        if (articlesDetails.url.isNullOrEmpty()) {
            binding.layoutReadMoreContent.visibility = View.GONE
        }
        else {
            binding.layoutReadMoreContent.visibility = View.VISIBLE
        }

    }

    private fun setupComponentListener() {
        binding.imgBackIcon.setOnClickListener {
            finish()
        }

        binding.layoutReadMoreContent.setOnClickListener {
            val intent = Intent(this@NewsDetailsActivity, WebViewActivity::class.java)
            if (!articlesDetails.url.isNullOrEmpty()) {
                intent.putExtra(Constant.INTENT_WEB_VIEW_URL, articlesDetails.url)
            }
            startActivity(intent)
        }
    }
}