package com.hukex.punpun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.material.appbar.MaterialToolbar;

public class Preference extends AppCompatActivity {

    private MaterialToolbar materialToolbar;
    public static boolean hasToRestart = false;
    public static String caller = "";
    private SharedPreferences sharedPref;

    @Override
    public void onBackPressed() {
        if (hasToRestart) {
            Intent intent = getIntentCaller(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        }
        super.onBackPressed();
    }

    private static Intent getIntentCaller(Context preference) {
        Intent intent = null;
        try {
            intent = new Intent(preference, Class.forName(caller));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.PreferenceTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            caller = getIntent().getStringExtra("caller");
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            if (!sharedPref.contains("CALLER") || (caller != null && !caller.equals("")))
                sharedPref.edit().putString("CALLER", caller).apply();
            else {
                caller = sharedPref.getString("CALLER", "com.hukex.punpun.AnimeActivity");
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
            materialToolbar = findViewById(R.id.topWallpaperBar);
            materialToolbar.setNavigationOnClickListener(v -> {
                Intent intent = getIntentCaller(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(intent, 0);
            });
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private SwitchPreference switchDarkMode, switchQualityImage;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.prefs, rootKey);
            switchDarkMode = (SwitchPreference) findPreference("DARKMODE");
            if (switchDarkMode != null) {
                switchDarkMode.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (switchDarkMode.isChecked()) {
                        switchDarkMode.setChecked(false);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        switchDarkMode.setChecked(true);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    restartApp();
                    return false;
                });
            }


            switchQualityImage = (SwitchPreference) findPreference("QUALITYIMAGE");
            if (switchQualityImage != null) {
                switchQualityImage.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (switchQualityImage.isChecked()) {
                        switchQualityImage.setChecked(false);
                    } else {
                        switchQualityImage.setChecked(true);
                    }
                    restartApp();
                    return false;
                });
            }
        }

        private void restartApp() {
            hasToRestart = true;
            Intent intent = new Intent(getContext(), Preference.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
            getActivity().finish();
        }
    }


}