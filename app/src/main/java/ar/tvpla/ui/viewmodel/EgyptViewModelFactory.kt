package ar.tvpla.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.tvpla.ui.model.UrlDatabase

class EgyptViewModelFactory(private val application: Application, private val db: UrlDatabase) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EgyptViewModel(application, db) as T
    }
}