package com.github.florent37.applicationprovider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.application.provider.ActivityProvider
import com.github.florent37.applicationprovider.dagger.InitializeDagger
import com.github.florent37.applicationprovider.java.PreferencesManager
import com.github.florent37.applicationprovider.stetho.InitializeStetho
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

class SecondActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.text = "second activity"
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
