package com.example.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.SearchInteractor

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(private val interactor: SearchInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(interactor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
