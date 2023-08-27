package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val searchHistoryKey = "search_history"
    val maxHistorySize = 10

    fun loadSearchHistory(): MutableList<Track> {
        val searchHistoryJson = sharedPreferences.getString(searchHistoryKey, null)
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(searchHistoryJson, type) ?: mutableListOf()
    }

    fun saveSearchHistory(searchHistory: MutableList<Track>) {
        val editor = sharedPreferences.edit()
        val limitedHistory = searchHistory.takeLast(maxHistorySize).toMutableList()
        val searchHistoryJson = Gson().toJson(limitedHistory)
        editor.putString(searchHistoryKey, searchHistoryJson)
        editor.apply()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit().remove(searchHistoryKey).apply()
    }

}
