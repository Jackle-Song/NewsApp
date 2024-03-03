package com.mrsworkshop.newsapp.activity

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mrsworkshop.newsapp.R
import com.mrsworkshop.newsapp.adapter.NewsDetailsAdapter
import com.mrsworkshop.newsapp.apidata.response.ArticlesDetails
import com.mrsworkshop.newsapp.core.CoreEnum
import com.mrsworkshop.newsapp.core.PreferenceCache
import com.mrsworkshop.newsapp.databinding.ActivityHomeBinding
import com.mrsworkshop.newsapp.fragment.TopHeadlinesFragment
import com.mrsworkshop.newsapp.helper.ContextWrapper
import com.mrsworkshop.newsapp.utils.Constant
import com.mrsworkshop.newsapp.viewModel.NewsApiData
import com.mrsworkshop.newsapp.vo.NewsDetailsVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseActivity(), TopHeadlinesFragment.TopHeadlinesInterface {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var oldPrefLocaleCode : String


    override fun attachBaseContext(newBase: Context) {
        val lang = PreferenceCache(newBase).getSelectedLanguage()
        oldPrefLocaleCode = lang

        super.attachBaseContext(ContextWrapper.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(TopHeadlinesFragment())
        setupComponentListener()
    }

    override fun mainRecreateActivity() {
        recreateActivity()
    }

    /**
     * private function
     */

    private fun setupComponentListener() {
        binding.bottomNavigationHome.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.top_headlines -> {
                    loadFragment(TopHeadlinesFragment())
                }
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutHome, fragment)
            .commit()
    }

    private fun recreateActivity() {
        val currentLocaleCode = PreferenceCache(this).getSelectedLanguage()
        recreate()
        oldPrefLocaleCode = currentLocaleCode
    }
}