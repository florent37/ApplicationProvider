package com.github.florent37.applicationprovider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.application.provider.ActivityProvider
import com.github.florent37.applicationprovider.dagger.InitializeDagger
import com.github.florent37.applicationprovider.java.PreferencesManager
import com.github.florent37.applicationprovider.stetho.InitializeStetho

class MainActivity : AppCompatActivity() {

    init {
        InitializeStetho
        InitializeDagger
    }

    private var preferencesManager = PreferencesManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentActivity = ActivityProvider.currentActivity
    }
}
