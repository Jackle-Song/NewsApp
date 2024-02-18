package com.mrsworkshop.newsapp.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mrsworkshop.newsapp.R
import com.mrsworkshop.newsapp.adapter.NewsDetailsAdapter
import com.mrsworkshop.newsapp.apidata.response.ArticlesDetails
import com.mrsworkshop.newsapp.apidata.response.NewsApiResponseDTO
import com.mrsworkshop.newsapp.databinding.ActivityHomeBinding
import com.mrsworkshop.newsapp.viewModel.NewsApiData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var newsDetailsAdapter: NewsDetailsAdapter

    private var newsApiData : NewsApiData = NewsApiData()
    private var newsApiDetailsList : MutableList<ArticlesDetails>? = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setupComponentListener()
        getNewsApi()
    }

    /**
     * private function
     */

    private fun initUI() {
        val mainCategoryList = resources.getStringArray(R.array.HomeActivityMainCategory).toMutableList()
        binding.layoutMainCategory.removeAllViews()
        for (index in mainCategoryList.indices) {
            val mainCategoryItem = mainCategoryList[index]

            val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val mainCategoryItemView : View = layoutInflater.inflate(R.layout.item_category_button_design, null)

            val cardViewCategory : CardView = mainCategoryItemView.findViewById(R.id.cardViewCategory)
            val txtCardViewCategory : TextView = mainCategoryItemView.findViewById(R.id.txtCardViewCategory)

            txtCardViewCategory.text = mainCategoryItem

            binding.layoutMainCategory.addView(mainCategoryItemView)
        }

        val subCategoryList = resources.getStringArray(R.array.HomeActivitySubCategory).toMutableList()
        binding.layoutSubCategory.removeAllViews()
        for (index in subCategoryList.indices) {
            val subCategoryItem = subCategoryList[index]

            val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val subCategoryItemView : View = layoutInflater.inflate(R.layout.item_sub_category_button_design, null)

            val txtCardViewSubCategory : TextView = subCategoryItemView.findViewById(R.id.txtCardViewSubCategory)

            txtCardViewSubCategory.text = subCategoryItem

            binding.layoutSubCategory.addView(subCategoryItemView)
        }

        newsDetailsAdapter = NewsDetailsAdapter(this@HomeActivity, newsApiDetailsList)
        binding.recyclerviewNewsList.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerviewNewsList.adapter = newsDetailsAdapter

    }

    private fun setupComponentListener() {
        binding.imgLanguageDialog.setOnClickListener {
            showLanguageBottomSheetDialog()
        }
    }

    private fun showLanguageBottomSheetDialog() {
        val languageBottomSheetDialog = BottomSheetDialog(this)
        languageBottomSheetDialog.setContentView(R.layout.layout_select_language_bottom_sheet)

        val toolbarBottomSheet = languageBottomSheetDialog.findViewById<LinearLayout>(R.id.toolbarLanguageBottomSheet)
        val toolbarTitle = toolbarBottomSheet?.findViewById<TextView>(R.id.toolbarTitle)
        val imgCloseBtn = toolbarBottomSheet?.findViewById<ImageView>(R.id.imgCloseBtn)

        val layoutEnglishLanguage = languageBottomSheetDialog.findViewById<RelativeLayout>(R.id.layoutEnglishLanguage)
        val txtEnglishLanguage = languageBottomSheetDialog.findViewById<TextView>(R.id.txtEnglishLanguage)
        val imgEnglishSelectedLanguage = languageBottomSheetDialog.findViewById<ImageView>(R.id.imgEnglishSelectedLanguage)

        val layoutChineseLanguage = languageBottomSheetDialog.findViewById<RelativeLayout>(R.id.layoutChineseLanguage)
        val txtChineseLanguage = languageBottomSheetDialog.findViewById<TextView>(R.id.txtChineseLanguage)
        val imgChineseSelectedLanguage = languageBottomSheetDialog.findViewById<ImageView>(R.id.imgChineseSelectedLanguage)

        toolbarTitle?.text = getString(R.string.language_dialog_select_language_title)

        imgCloseBtn?.setOnClickListener {
            languageBottomSheetDialog.dismiss()
        }

        layoutEnglishLanguage?.setOnClickListener {
            txtEnglishLanguage?.setTextColor(ContextCompat.getColor(this, R.color.red_b6))
            imgEnglishSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.red_b6), PorterDuff.Mode.SRC_ATOP)
            imgEnglishSelectedLanguage?.visibility = View.VISIBLE

            txtChineseLanguage?.setTextColor(ContextCompat.getColor(this, R.color.black_32))
            imgChineseSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.black_32), PorterDuff.Mode.SRC_ATOP)
            imgChineseSelectedLanguage?.visibility = View.GONE
        }

        layoutChineseLanguage?.setOnClickListener {
            txtChineseLanguage?.setTextColor(ContextCompat.getColor(this, R.color.red_b6))
            imgChineseSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.red_b6), PorterDuff.Mode.SRC_ATOP)
            imgChineseSelectedLanguage?.visibility = View.VISIBLE

            txtEnglishLanguage?.setTextColor(ContextCompat.getColor(this, R.color.black_32))
            imgEnglishSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.black_32), PorterDuff.Mode.SRC_ATOP)
            imgEnglishSelectedLanguage?.visibility = View.GONE
        }

        languageBottomSheetDialog.show()
    }

    /**
     * api service
     */

    private fun getNewsApi() {
        val country = "my"
        val category = "business"

        newsApiData.getTopHeadlines(country, category)?.enqueue(object :
            Callback<NewsApiResponseDTO> {
            override fun onResponse(call: Call<NewsApiResponseDTO>, response: Response<NewsApiResponseDTO>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    newsApiDetailsList = newsResponse?.articles
                    newsDetailsAdapter.updateNewsDetailsList(newsApiDetailsList)
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<NewsApiResponseDTO>, t: Throwable) {
                // Handle network errors
            }
        })
    }
}