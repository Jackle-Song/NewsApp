package com.mrsworkshop.newsapp.apidata.response

data class NewsApiResponseDTO (
    var status : String? = null,
    var totalResults : Int? = null,
    var articles : MutableList<ArticlesDetails>? = mutableListOf()
) : DTO()

data class ArticlesDetails(
    var source : SourceDetails? = null,
    var author : String? = null,
    var title : String? = null,
    var description : String? = null,
    var url : String? = null,
    var urlToImage : String? = null,
    var publishedAt : String? = null,
    var content : String? = null,
)

data class SourceDetails(
    var id : String? = null,
    var name : String? = null
)