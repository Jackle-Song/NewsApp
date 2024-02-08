package com.mrsworkshop.newsapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.mrsworkshop.newsapp.R
import com.mrsworkshop.newsapp.apidata.response.NewsApiResponseDTO
import com.mrsworkshop.newsapp.viewModel.NewsApiData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var newsApiData : NewsApiData = NewsApiData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getNewsApi()
    }

    private fun getNewsApi() {
        val country = "my"
        val category = "business"

        newsApiData.getTopHeadlines(country, category)?.enqueue(object :
            Callback<NewsApiResponseDTO> {
            override fun onResponse(call: Call<NewsApiResponseDTO>, response: Response<NewsApiResponseDTO>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    println("newsResponse ${Gson().toJson(newsResponse)}")
                    // Handle the response data
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