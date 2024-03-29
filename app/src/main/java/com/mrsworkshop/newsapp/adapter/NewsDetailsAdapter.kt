package com.mrsworkshop.newsapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mrsworkshop.newsapp.R
import com.mrsworkshop.newsapp.activity.BaseActivity
import com.mrsworkshop.newsapp.apidata.response.ArticlesDetails
import com.mrsworkshop.newsapp.fragment.BaseFragment

class NewsDetailsAdapter(
    private var mContext : Context,
    private var newsDetailsList : MutableList<ArticlesDetails>? = mutableListOf(),
    private var mListener : NewsDetailsInterface,
) : RecyclerView.Adapter<ViewHolder>() {

    interface NewsDetailsInterface {
        fun onViewNewsDetails(articlesDetails: ArticlesDetails)
    }

    class NewsDetailsViewHolder(itemView : View) : ViewHolder(itemView) {
        val cardViewNewsDetails : CardView = itemView.findViewById(R.id.cardViewNewsDetails)
        val imgNewsContent : ImageView = itemView.findViewById(R.id.imgNewsContent)
        val txtNewsTitle : TextView = itemView.findViewById(R.id.txtNewsTitle)
        val txtNewsAuthor : TextView = itemView.findViewById(R.id.txtNewsAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_details_recyclerview, parent, false)
        return NewsDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsDetailsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newsDetailsItem = newsDetailsList?.get(position)
        val newsDetailsViewHolder = holder as NewsDetailsViewHolder

        if (!newsDetailsItem?.urlToImage.isNullOrEmpty()) {
            Glide.with(mContext)
                .load(newsDetailsItem?.urlToImage)
                .placeholder(R.drawable.img_breaking_news)
                .into(newsDetailsViewHolder.imgNewsContent)
        }
        else {
            Glide.with(mContext)
                .load(R.drawable.img_breaking_news)
                .placeholder(R.drawable.img_breaking_news)
                .into(newsDetailsViewHolder.imgNewsContent)
        }

        if (!newsDetailsItem?.author.isNullOrEmpty()) {
            newsDetailsViewHolder.txtNewsAuthor.text = newsDetailsItem?.author
        }
        else {
            newsDetailsViewHolder.txtNewsAuthor.visibility = View.GONE
        }

        if (!newsDetailsItem?.title.isNullOrEmpty()) {
            newsDetailsViewHolder.txtNewsTitle.text = newsDetailsItem?.title
        }

        newsDetailsViewHolder.cardViewNewsDetails.setOnClickListener {
            if (newsDetailsItem != null) {
                mListener.onViewNewsDetails(newsDetailsItem)
            }
        }
    }

    fun updateSearchNewsDetailsList(updatedNewsList : MutableList<ArticlesDetails>?, startPosition : Int, newItemSize : Int) {
        newsDetailsList = updatedNewsList
        notifyItemRangeInserted(startPosition, newItemSize)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNewsDetailsList(updatedNewsList : MutableList<ArticlesDetails>?) {
        newsDetailsList = updatedNewsList
        notifyDataSetChanged()
    }
}