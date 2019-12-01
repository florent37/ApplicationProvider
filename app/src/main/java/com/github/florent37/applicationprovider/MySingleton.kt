package com.github.florent37.applicationprovider

import com.github.florent37.application.provider.ActivityProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class MySingleton constructor() : CoroutineScope by MainScope() {
    init {
        launch {
            val currentActivity = ActivityProvider.activity()
            Timber.d("activity : $currentActivity")

            ActivityProvider.listenCurrentActivity.collect {  currentActivity ->
                Timber.d("current activity : $currentActivity")
            }
        }
    }
}