package com.github.florent37.applicationprovider.timber

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.facebook.stetho.Stetho
import com.github.florent37.application.provider.ApplicationProvider
import com.github.florent37.application.provider.ProviderInitializer
import timber.log.Timber

class TimberInitializer : ProviderInitializer() {
    override fun initialize(): (Application) -> Unit = {
        Timber.plant(Timber.DebugTree())
    }
}

class StethoInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        Stetho.initializeWithDefaults(context)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        throw Exception("unimplemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        throw Exception("unimplemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw Exception("unimplemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw Exception("unimplemented")
    }

    override fun getType(uri: Uri): String {
        throw Exception("unimplemented")
    }
}
