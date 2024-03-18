package com.mrsworkshop.newsapp.network

import com.mrsworkshop.newsapp.apidata.response.NewsApiResponseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String?,
        @Query("category") category: String?,
        @Query("apiKey") apiKey: String?
    ): Call<NewsApiResponseDTO>?

    @GET("everything")
    fun getRelevantNews(
        @Query("q") query: String?,
        @Query("apiKey") apiKey: String?
    ): Call<NewsApiResponseDTO>?
}