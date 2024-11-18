package com.tghrsyahptra.dicodingeventapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tghrsyahptra.dicodingeventapp.data.local.entity.EventEntity
import com.tghrsyahptra.dicodingeventapp.data.remote.EventRepository
import com.tghrsyahptra.dicodingeventapp.ui.SettingPreferences
import kotlinx.coroutines.launch

class MainViewModel(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    // Mendapatkan event yang akan datang
    fun getUpcomingEvents() = eventRepository.getEvents(active = 1)
    // Mendapatkan event yang telah selesai
    fun getFinishedEvents() = eventRepository.getEvents(active = 0)
    // Mendapatkan event favorit
    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()
    // Menyimpan event sebagai favorit
    fun saveEvents(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvents(event, true)
        }
    }

    // Menghapus event dari favorit
    fun deleteEvents(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvents(event, false)
        }
    }

    // Fungsi pengaturan tema
    fun getThemeSettings() = settingPreferences.getThemeSetting().asLiveData()
    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            settingPreferences.saveThemeSetting(isDarkModeActive)
        }
    }

    // Fungsi pengaturan notifikasi
    fun getNotificationSettings() = settingPreferences.getNotificationSetting().asLiveData()
    fun saveNotificationSetting(isNotificationActive: Boolean) {
        viewModelScope.launch {
            settingPreferences.saveNotificationSetting(isNotificationActive)
        }
    }
}