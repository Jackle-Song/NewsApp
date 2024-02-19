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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mrsworkshop.newsapp.R
import com.mrsworkshop.newsapp.adapter.NewsDetailsAdapter
import com.mrsworkshop.newsapp.apidata.response.ArticlesDetails
import com.mrsworkshop.newsapp.core.CoreEnum
import com.mrsworkshop.newsapp.core.PreferenceCache
import com.mrsworkshop.newsapp.databinding.ActivityHomeBinding
import com.mrsworkshop.newsapp.helper.ContextWrapper
import com.mrsworkshop.newsapp.utils.Constant
import com.mrsworkshop.newsapp.viewModel.NewsApiData
import com.mrsworkshop.newsapp.vo.NewsDetailsVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseActivity(), NewsDetailsAdapter.NewsDetailsInterface {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var newsDetailsAdapter: NewsDetailsAdapter
    private lateinit var oldPrefLocaleCode : String

    private var newsApiData : NewsApiData = NewsApiData()
    private var newsDetailsVO : NewsDetailsVO = NewsDetailsVO()
    private var newsApiDetailsList : MutableList<ArticlesDetails>? = mutableListOf()

    private var selectedCountry : String? = null
    private var selectedSubCategory : String? = null

    override fun attachBaseContext(newBase: Context) {
        val lang = PreferenceCache(newBase).getSelectedLanguage()
        oldPrefLocaleCode = lang

        super.attachBaseContext(ContextWrapper.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val savedSearch = PreferenceCache(this).getSavedSearch()
        if (savedSearch != null) {
            newsDetailsVO = Gson().fromJson(savedSearch, NewsDetailsVO::class.java)
            selectedCountry = newsDetailsVO.country
            selectedSubCategory = newsDetailsVO.category
        }

        initUI()
        setupComponentListener()
        getNewsApi()
    }

    override fun onViewNewsDetails(articlesDetails: ArticlesDetails) {
        val articlesDetailsJson = Gson().toJson(articlesDetails)
        val intent = Intent(this, NewsDetailsActivity::class.java)
        intent.putExtra(Constant.INTENT_NEWS_DETAILS_JSON, articlesDetailsJson)
        startActivity(intent)
    }

    /**
     * private function
     */

    private fun recreateActivity() {
        val currentLocaleCode = PreferenceCache(this).getSelectedLanguage()
        recreate()
        oldPrefLocaleCode = currentLocaleCode
    }

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

            if (selectedCountry == null) {
                if (index == CoreEnum.CountryCategory.MALAYSIA.index) {
                    cardViewCategory.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_41))
                    txtCardViewCategory.setTextColor(ContextCompat.getColor(this, R.color.grey_55))
                    selectedCountry = CoreEnum.CountryCategory.MALAYSIA.country
                }
            }
            else {
                when (val selectedCountryEnum = CoreEnum.CountryCategory.values().find { it.country == selectedCountry }) {
                    CoreEnum.CountryCategory.MALAYSIA,
                    CoreEnum.CountryCategory.CHINA -> {
                        if (index == selectedCountryEnum.index) {
                            cardViewCategory.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_41))
                            txtCardViewCategory.setTextColor(ContextCompat.getColor(this, R.color.grey_55))
                        }
                    }
                    else -> {
                        cardViewCategory.setCardBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
                        txtCardViewCategory.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                }
            }

            cardViewCategory.setOnClickListener {
                for (i in mainCategoryList.indices) {
                    val cardView = binding.layoutMainCategory.getChildAt(i).findViewById<CardView>(R.id.cardViewCategory)
                    val textView = binding.layoutMainCategory.getChildAt(i).findViewById<TextView>(R.id.txtCardViewCategory)
                    if (i == index) {
                        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_41))
                        textView.setTextColor(ContextCompat.getColor(this, R.color.grey_55))
                    }
                    else {
                        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
                        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }

                    when (index) {
                        CoreEnum.CountryCategory.MALAYSIA.index -> {
                            selectedCountry = CoreEnum.CountryCategory.MALAYSIA.country
                        }

                        CoreEnum.CountryCategory.CHINA.index -> {
                            selectedCountry = CoreEnum.CountryCategory.CHINA.country
                        }
                    }
                }
                getNewsApi()
            }

            binding.layoutMainCategory.addView(mainCategoryItemView)
        }

        val subCategoryList = resources.getStringArray(R.array.HomeActivitySubCategory).toMutableList()
        binding.layoutSubCategory.removeAllViews()
        for (index in subCategoryList.indices) {
            val subCategoryItem = subCategoryList[index]

            val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val subCategoryItemView : View = layoutInflater.inflate(R.layout.item_sub_category_button_design, null)

            val layoutSubCategory : LinearLayout = subCategoryItemView.findViewById(R.id.layoutSubCategory)
            val txtCardViewSubCategory : TextView = subCategoryItemView.findViewById(R.id.txtCardViewSubCategory)
            val viewSelectedCategory : View = subCategoryItemView.findViewById(R.id.viewSelectedCategory)

            if (selectedSubCategory == null || selectedSubCategory == CoreEnum.SubCategory.BUSINESS.category) {
                if (index == CoreEnum.SubCategory.BUSINESS.index) {
                    viewSelectedCategory.visibility = View.VISIBLE
                    selectedSubCategory = CoreEnum.SubCategory.BUSINESS.category
                }
                else {
                    viewSelectedCategory.visibility = View.INVISIBLE
                }
            }
            else {
                val selectedCategoryEnum = CoreEnum.SubCategory.values().find { it.category == selectedSubCategory }
                viewSelectedCategory.visibility = if (index == selectedCategoryEnum?.index) View.VISIBLE else View.INVISIBLE
            }

            txtCardViewSubCategory.text = subCategoryItem

            layoutSubCategory.setOnClickListener {
                for (i in subCategoryList.indices) {
                    val categoryView = binding.layoutSubCategory.getChildAt(i).findViewById<View>(R.id.viewSelectedCategory)
                    if (i == index) {
                        categoryView.visibility = View.VISIBLE
                    }
                    else {
                        categoryView.visibility = View.INVISIBLE
                    }

                    when (index) {
                        CoreEnum.SubCategory.BUSINESS.index -> {
                            selectedSubCategory = CoreEnum.SubCategory.BUSINESS.category
                        }

                        CoreEnum.SubCategory.ENTERTAINMENT.index -> {
                            selectedSubCategory = CoreEnum.SubCategory.ENTERTAINMENT.category
                        }

                        CoreEnum.SubCategory.GENERAL.index -> {
                            selectedSubCategory = CoreEnum.SubCategory.GENERAL.category
                        }

                        CoreEnum.SubCategory.HEALTH.index -> {
                            selectedSubCategory = CoreEnum.SubCategory.HEALTH.category
                        }

                        CoreEnum.SubCategory.SCIENCE.index -> {
                            selectedSubCategory = CoreEnum.SubCategory.SCIENCE.category
                        }

                        CoreEnum.SubCategory.SPORTS.index -> {
                            selectedSubCategory = CoreEnum.SubCategory.SPORTS.category
                        }

                        CoreEnum.SubCategory.TECHNOLOGY.index -> {
                            selectedSubCategory = CoreEnum.SubCategory.TECHNOLOGY.category
                        }
                    }
                }
                getNewsApi()
            }

            binding.layoutSubCategory.addView(subCategoryItemView)
        }

        newsDetailsAdapter = NewsDetailsAdapter(this@HomeActivity, newsApiDetailsList, this@HomeActivity)
        binding.recyclerviewNewsList.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerviewNewsList.adapter = newsDetailsAdapter

    }

    private fun setupComponentListener() {
        binding.imgLanguageDialog.setOnClickListener {
            showLanguageBottomSheetDialog()
        }

        binding.cardViewSaveSearch.setOnClickListener {
            newsDetailsVO.country = selectedCountry
            newsDetailsVO.category = selectedSubCategory
            val savedCountryCategory = Gson().toJson(newsDetailsVO)
            PreferenceCache(this).savedSearch(savedCountryCategory)
            Toast.makeText(this@HomeActivity, getString(R.string.home_activity_save_successfully_text), Toast.LENGTH_LONG).show()
        }

        binding.horizontalScrollViewSubCategory.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val selectedCategoryEnum = CoreEnum.SubCategory.values().find { it.category == selectedSubCategory }
                val childView = binding.layoutSubCategory.getChildAt(selectedCategoryEnum?.index ?: 0)
                val childWidth = childView.width

                val scrollPosition = selectedCategoryEnum?.index?.times(childWidth)

                binding.horizontalScrollViewSubCategory.post {
                    if (scrollPosition != null) {
                        binding.horizontalScrollViewSubCategory.smoothScrollTo(scrollPosition, 0)
                    }
                }

                binding.horizontalScrollViewSubCategory.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
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

        if (PreferenceCache(this).getSelectedLanguage() == CoreEnum.AppLanguageType.ENGLISH.languageType) {
            txtEnglishLanguage?.setTextColor(ContextCompat.getColor(this, R.color.red_b6))
            imgEnglishSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.red_b6), PorterDuff.Mode.SRC_ATOP)
            imgEnglishSelectedLanguage?.visibility = View.VISIBLE

            txtChineseLanguage?.setTextColor(ContextCompat.getColor(this, R.color.black_32))
            imgChineseSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.black_32), PorterDuff.Mode.SRC_ATOP)
            imgChineseSelectedLanguage?.visibility = View.GONE
        }
        else {
            txtChineseLanguage?.setTextColor(ContextCompat.getColor(this, R.color.red_b6))
            imgChineseSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.red_b6), PorterDuff.Mode.SRC_ATOP)
            imgChineseSelectedLanguage?.visibility = View.VISIBLE

            txtEnglishLanguage?.setTextColor(ContextCompat.getColor(this, R.color.black_32))
            imgEnglishSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.black_32), PorterDuff.Mode.SRC_ATOP)
            imgEnglishSelectedLanguage?.visibility = View.GONE
        }

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

            PreferenceCache(this).setSelectedLanguage(CoreEnum.AppLanguageType.ENGLISH.languageType)
            languageBottomSheetDialog.dismiss()
            recreateActivity()
        }

        layoutChineseLanguage?.setOnClickListener {
            txtChineseLanguage?.setTextColor(ContextCompat.getColor(this, R.color.red_b6))
            imgChineseSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.red_b6), PorterDuff.Mode.SRC_ATOP)
            imgChineseSelectedLanguage?.visibility = View.VISIBLE

            txtEnglishLanguage?.setTextColor(ContextCompat.getColor(this, R.color.black_32))
            imgEnglishSelectedLanguage?.setColorFilter(ContextCompat.getColor(this, R.color.black_32), PorterDuff.Mode.SRC_ATOP)
            imgEnglishSelectedLanguage?.visibility = View.GONE

            PreferenceCache(this).setSelectedLanguage(CoreEnum.AppLanguageType.CHINESE.languageType)
            languageBottomSheetDialog.dismiss()
            recreateActivity()
        }

        languageBottomSheetDialog.show()
    }

    /**
     * api service
     */

    private fun getNewsApi() {
        showLoadingViewDialog()
        newsApiDetailsList?.clear()
        newsDetailsVO.country = selectedCountry
        newsDetailsVO.category = selectedSubCategory

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = newsApiData.getTopHeadlines(newsDetailsVO.country ?: "", newsDetailsVO.category ?: "")?.execute()
                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true) {
                        val newsResponse = response.body()
                        newsApiDetailsList = newsResponse?.articles
                        newsDetailsAdapter.updateNewsDetailsList(newsApiDetailsList)
                    }
                    else {
                        // Handle unsuccessful response
                    }
                }
            }
            catch (e: Exception) {
                // Handle network errors
            }
        }
    }
}