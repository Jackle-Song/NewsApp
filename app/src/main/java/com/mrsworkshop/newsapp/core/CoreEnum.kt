package com.mrsworkshop.newsapp.core

object CoreEnum {

    enum class AppLanguageType(val languageType: String, val languageSpeech : String) {
        ENGLISH("en", "en-US"),
        CHINESE("zh", "zh-CN")
    }

    enum class CountryCategory(val country: String, val index : Int) {
        MALAYSIA("my", 0),
        CHINA("cn", 1)
    }

    enum class SubCategory(val category: String, val index : Int) {
        BUSINESS("business", 0),
        ENTERTAINMENT("entertainment", 1),
        GENERAL("general", 2),
        HEALTH("health", 3),
        SCIENCE("science", 4),
        SPORTS("sports", 5),
        TECHNOLOGY("technology", 6),
    }
}