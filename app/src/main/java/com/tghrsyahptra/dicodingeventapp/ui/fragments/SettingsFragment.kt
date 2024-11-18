package com.tghrsyahptra.dicodingeventapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.*
import com.tghrsyahptra.dicodingeventapp.R
import com.tghrsyahptra.dicodingeventapp.ui.MyWorker
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.MainViewModel
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.ViewModelFactory
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeSwitch = findPreference<SwitchPreference>("theme")
        val notificationSwitch = findPreference<SwitchPreference>("notification")

        // Pantau pengaturan tema dan perbarui state switch
        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeEnabled ->
            themeSwitch?.isChecked = isDarkModeEnabled
        }

        // Pantau pengaturan pemberitahuan dan perbarui state switch
        viewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationEnabled ->
            notificationSwitch?.isChecked = isNotificationEnabled
        }

        // Kelola perubahan tema
        themeSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkModeEnabled = newValue as Boolean
            viewModel.saveThemeSetting(isDarkModeEnabled)
            setDarkMode(isDarkModeEnabled)
            true
        }

        // Kelola perubahan pemberitahuan
        notificationSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isDailyReminderEnabled = newValue as Boolean
            viewModel.saveNotificationSetting(isDailyReminderEnabled)
            manageDailyReminder(isDailyReminderEnabled)
            true
        }
    }

    private fun setDarkMode(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun manageDailyReminder(isDailyReminder: Boolean) {
        if (isDailyReminder) {
            initiatePeriodicTask()
        } else {
            workManager.cancelUniqueWork("notification")
        }
    }

    private fun initiatePeriodicTask() {
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_EVENT, "haloo")
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 24, TimeUnit.HOURS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "notification",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        workManager.getWorkInfosForUniqueWorkLiveData("notification")
            .observe(requireActivity()) { workInfo ->
                workInfo.forEach { info ->
                    Log.d("WorkInfo", info.state.toString())
                }
            }
    }
}