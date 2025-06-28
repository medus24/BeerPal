package com.example.beerpal.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BeerPalViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeerPalViewModel::class.java)) {
            return BeerPalViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
