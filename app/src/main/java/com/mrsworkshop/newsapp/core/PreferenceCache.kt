package com.mrsworkshop.newsapp.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrsworkshop.newsapp.apidata.response.ArticlesDetails

const val PREFERENCE_NAME = "SharedPreferenceNewsApp"
const val PREFERENCE_LANGUAGE = "Language"
const val PREFERENCE_COUNTRY_CATEGORY = "CountryCategory"
const val PREFERENCE_SAVED_HISTORY = "SaveHistory"

class PreferenceCache(context : Context) {

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFERENCE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val preference = getEncryptedSharedPreferences(context)

    fun getSelectedLanguage() : String {
        return preference.getString(PREFERENCE_LANGUAGE, CoreEnum.AppLanguageType.ENGLISH.languageType).toString()
    }

    fun setSelectedLanguage(language:String) {
        val editor = preference.edit()
        editor.putString(PREFERENCE_LANGUAGE, language)
        editor.apply()
    }

    fun getSavedSearch() : String? {
        return preference.getString(PREFERENCE_COUNTRY_CATEGORY, null)
    }

    fun savedSearch(savedCountryCategory : String) {
        val editor = preference.edit()
        editor.putString(PREFERENCE_COUNTRY_CATEGORY, savedCountryCategory)
        editor.apply()
    }
    fun getSearchHistory(): MutableList<String>? {
        val json = preference.getString(PREFERENCE_SAVED_HISTORY, null)
        return Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
    }

    fun savedSearchHistory(savedHistory : String) {
        val editor = preference.edit()
        editor.putString(PREFERENCE_SAVED_HISTORY, savedHistory)
        editor.apply()
    }
}