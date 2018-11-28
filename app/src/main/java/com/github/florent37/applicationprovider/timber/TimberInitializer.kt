package com.github.florent37.applicationprovider.timber

import android.app.Application
import com.github.florent37.application.provider.ProviderInitializer
import timber.log.Timber

class TimberInitializer : ProviderInitializer() {
    override fun initialize(): (Application) -> Unit = {
        Timber.plant(Timber.DebugTree())
    }
}