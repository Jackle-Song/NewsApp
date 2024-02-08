package com.mrsworkshop.newsapp.apidata.response

abstract class DTO (
    var responseCode: Int? = null,
    var responseMessage: String? = null,

    var errorKey: String? = null,
    var title: String? = null,
    var statusCode: Int? = null,
    var httpDetails: String? = null
)