package com.github.florent37.applicationprovider.dagger

import com.github.florent37.applicationprovider.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}