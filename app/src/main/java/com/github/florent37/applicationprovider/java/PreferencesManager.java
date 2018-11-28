package com.github.florent37.applicationprovider.java;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.florent37.application.provider.ApplicationProvider;

public class PreferencesManager {

    private static PreferencesManager INSTANCE;

    private SharedPreferences sharedPreferences;

    private PreferencesManager() {
        final Application application = ApplicationProvider.getApplication();
        this.sharedPreferences = application.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static PreferencesManager getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new PreferencesManager();
        }
        return INSTANCE;
    }

}
