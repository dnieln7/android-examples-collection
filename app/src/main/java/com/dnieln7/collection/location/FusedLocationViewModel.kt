package com.dnieln7.collection.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FusedLocationViewModel(val tracker: LocationTracker) : ViewModel() {



    @Suppress("UNCHECKED_CAST")
    class Factory(private val tracker: LocationTracker) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FusedLocationViewModel::class.java)) {
                return FusedLocationViewModel(tracker) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}