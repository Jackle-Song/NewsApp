package com.mrsworkshop.newsapp.viewModel

import android.content.Context
import com.google.gson.GsonBuilder
import com.mrsworkshop.newsapp.apidata.response.NewsApiResponseDTO
import com.mrsworkshop.newsapp.network.ApiClient
import com.mrsworkshop.newsapp.network.ApiInterface
import com.mrsworkshop.newsapp.utils.Constant
import retrofit2.Call

class NewsApiData {
    private var apiInterface : ApiInterface? = null
    lateinit var mContext : Context
    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        apiInterface = ApiClient.retrofitService()
    }

    fun getTopHeadlines(country: String, category: String): Call<NewsApiResponseDTO>? {
        return apiInterface?.getTopHeadlines(country, category, Constant.API_KEY)
    }

    fun getRelevantNews(query : String, pageSize : Int, page : Int): Call<NewsApiResponseDTO>? {
        return apiInterface?.getRelevantNews(query, pageSize, page, Constant.API_KEY)
    }
}