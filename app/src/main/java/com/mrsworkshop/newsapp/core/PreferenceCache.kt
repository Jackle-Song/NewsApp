package com.mrsworkshop.newsapp.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

const val PREFERENCE_NAME = "SharedPreferenceNewsApp"
const val PREFERENCE_LANGUAGE = "Language"

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
}