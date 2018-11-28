package com.github.florent37.applicationprovider.stetho

import com.facebook.stetho.Stetho
import com.github.florent37.application.provider.ApplicationProvider

val InitializeStetho by lazy {
    ApplicationProvider.listen { application ->
        Stetho.initializeWithDefaults(application)
    }
}
