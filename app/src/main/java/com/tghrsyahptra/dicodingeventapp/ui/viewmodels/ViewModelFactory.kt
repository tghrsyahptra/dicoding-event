package com.tghrsyahptra.dicodingeventapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tghrsyahptra.dicodingeventapp.data.remote.EventRepository
import com.tghrsyahptra.dicodingeventapp.di.Injection
import com.tghrsyahptra.dicodingeventapp.ui.SettingPreferences
import com.tghrsyahptra.dicodingeventapp.ui.dataStore

class ViewModelFactory(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Memeriksa apakah modelClass adalah MainViewModel
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(eventRepository, settingPreferences) as T
        }
        throw IllegalArgumentException("Kelas ViewModel tidak dikenal: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        // Mengambil instance ViewModelFactory
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val repository = Injection.provideRepository(context)
                val preferences = SettingPreferences.getInstance(context.dataStore)
                instance ?: ViewModelFactory(repository, preferences)
            }.also { instance = it }
    }
}