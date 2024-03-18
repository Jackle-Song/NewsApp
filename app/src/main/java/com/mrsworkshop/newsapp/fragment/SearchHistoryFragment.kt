package com.mrsworkshop.newsapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.mrsworkshop.newsapp.R
import com.mrsworkshop.newsapp.activity.NewsDetailsActivity
import com.mrsworkshop.newsapp.adapter.NewsDetailsAdapter
import com.mrsworkshop.newsapp.apidata.response.ArticlesDetails
import com.mrsworkshop.newsapp.core.PreferenceCache
import com.mrsworkshop.newsapp.databinding.FragmentSearchNewsBinding
import com.mrsworkshop.newsapp.utils.Constant
import com.mrsworkshop.newsapp.viewModel.NewsApiData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchHistoryFragment : BaseFragment(), NewsDetailsAdapter.NewsDetailsInterface {
    private lateinit var binding : FragmentSearchNewsBinding
    private lateinit var newsDetailsAdapter: NewsDetailsAdapter

    private var newsApiData : NewsApiData = NewsApiData()
    private var searchHistoryList : MutableList<String>? = mutableListOf()
    private var newsApiDetailsList : MutableList<ArticlesDetails>? = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)

        initUI()
        setupComponentListener()

        return binding.root
    }

    override fun onViewNewsDetails(articlesDetails: ArticlesDetails) {
        val articlesDetailsJson = Gson().toJson(articlesDetails)
        val intent = Intent(requireContext(), NewsDetailsActivity::class.java)
        intent.putExtra(Constant.INTENT_NEWS_DETAILS_JSON, articlesDetailsJson)
        startActivity(intent)
    }

    /**
     * private function
     */

    private fun initUI() {
        loadSearchHistory()

        newsDetailsAdapter = NewsDetailsAdapter(requireContext(), newsApiDetailsList, this@SearchHistoryFragment)
        binding.recyclerviewSearchResults.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerviewSearchResults.adapter = newsDetailsAdapter
    }

    private fun setupComponentListener() {
        binding.layoutSearchFragment.setOnClickListener {
            binding.etSearchEditText.clearFocus()
            dismissKeyBoard()
        }

        binding.etSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.txtSearchText.visibility = View.VISIBLE
                }
                else {
                    binding.txtSearchText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }
        })

        binding.etSearchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.layoutSearchHistory.visibility = View.VISIBLE
                binding.layoutSearchHistoryWrapper.visibility = View.VISIBLE
                binding.recyclerviewSearchResults.visibility = View.GONE
            }
            else {
                binding.txtSearchText.visibility = View.GONE
                binding.layoutSearchHistory.visibility = View.GONE
                binding.layoutSearchHistoryWrapper.visibility = View.GONE
                binding.recyclerviewSearchResults.visibility = View.VISIBLE
            }
        }

        binding.txtSearchText.setOnClickListener {
            binding.etSearchEditText.clearFocus()
            dismissKeyBoard()

            val searchText = binding.etSearchEditText.text.toString().trim()
            if (searchText.isNotEmpty()) {
                if ((searchHistoryList?.size ?: 0) >= 10) {
                    searchHistoryList?.removeAt(0)
                }
                searchHistoryList?.add(searchText)
                PreferenceCache(requireContext()).savedSearchHistory(Gson().toJson(searchHistoryList))
                loadSearchHistory()

                getRelevantNews(searchText)
            }
        }

        binding.txtClearHistoryTitle.setOnClickListener {
            searchHistoryList?.clear()
            PreferenceCache(requireContext()).savedSearchHistory(Gson().toJson(searchHistoryList))
            loadSearchHistory()
        }
    }

    private fun loadSearchHistory() {
        searchHistoryList?.clear()
        PreferenceCache(requireContext()).getSearchHistory()?.let { searchHistoryList?.addAll(it) }

        val sortedSearchHistoryList = searchHistoryList?.reversed()

        binding.layoutSearchHistory.removeAllViews()
        if (sortedSearchHistoryList?.isNotEmpty() == true) {
            for (index in 0 until (sortedSearchHistoryList.size)) {
                val searchHistoryItem = sortedSearchHistoryList[index]

                val layoutInflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val searchHistoryLayoutView : View = layoutInflater.inflate(R.layout.item_category_button_design, null)

                val cardViewCategory : CardView = searchHistoryLayoutView.findViewById(R.id.cardViewCategory)
                val txtCardViewCategory : TextView = searchHistoryLayoutView.findViewById(R.id.txtCardViewCategory)

                txtCardViewCategory.text = searchHistoryItem

                cardViewCategory.setOnClickListener {
                    binding.etSearchEditText.setText(searchHistoryItem)
                    binding.etSearchEditText.clearFocus()
                    dismissKeyBoard()

                    getRelevantNews(searchHistoryItem)
                }

                binding.layoutSearchHistory.addView(searchHistoryLayoutView)
            }
        }
    }

    /**
     * api service
     */

    private fun getRelevantNews(query : String) {
        showLoadingViewDialog()
        newsApiDetailsList?.clear()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = newsApiData.getRelevantNews(query)?.execute()
                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true) {
                        val newsResponse = response.body()
                        newsApiDetailsList = newsResponse?.articles
                        newsDetailsAdapter.updateNewsDetailsList(newsApiDetailsList)
                        dismissLoadingViewDialog()
                    }
                    else {
                        // Handle unsuccessful response
                        dismissLoadingViewDialog()
                    }
                }
            }
            catch (e: Exception) {
                // Handle network errors
                dismissLoadingViewDialog()
            }
        }
    }

}