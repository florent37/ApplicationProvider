package com.github.florent37.applicationprovider.dagger

import com.github.florent37.application.provider.ApplicationProvider

lateinit var appComponent : AppComponent

val InitializeDagger by lazy {
    ApplicationProvider.listen { application ->
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .build()
    }
}