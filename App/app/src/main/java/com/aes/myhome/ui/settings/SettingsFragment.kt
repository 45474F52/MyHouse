package com.aes.myhome.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.aes.myhome.R
import com.aes.myhome.storage.json.JsonDataSerializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject lateinit var serializer: JsonDataSerializer

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findPreference<SwitchPreferenceCompat>("sync_theme")?.
        setOnPreferenceChangeListener { _, useCustomTheme ->
            if (useCustomTheme == true) {
                val isDarkTheme = findPreference<SwitchPreferenceCompat>("attachment_theme")?.isChecked

                if (isDarkTheme == true) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else if (isDarkTheme == false) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            } else if (useCustomTheme == false) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            true
        }

        findPreference<SwitchPreferenceCompat>("attachment_theme")?.
        setOnPreferenceChangeListener { _, isDarkTheme ->
            if (isDarkTheme == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else if (isDarkTheme == false) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }

        findPreference<Preference>("DEBUG_budget_drop")?.
        setOnPreferenceClickListener {
            serializer.writeData("", "", "finances.json")
            true
        }
    }
}