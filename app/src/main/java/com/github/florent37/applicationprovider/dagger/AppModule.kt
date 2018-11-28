package com.github.florent37.applicationprovider.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {

    @Provides
    fun provideApplication(): Application = application

    @Provides
    fun provideContext(): Context = application

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

}