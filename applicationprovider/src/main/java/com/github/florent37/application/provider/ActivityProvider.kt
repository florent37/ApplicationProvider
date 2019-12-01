package com.github.florent37.application.provider

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedQueue


interface ActivityCreatedListener {
    fun onActivityCreated(activity: Activity)
}

interface ActivityResumedListener {
    fun onActivityResumed(activity: Activity)
}

interface ActivityPausedListener {
    fun onActivityPaused(activity: Activity)
}

interface ActivityDestroyedListener {
    fun onActivityDestroyed(activity: Activity)
}

object ActivityProvider {
    private val activityCreatedListeners = ConcurrentLinkedQueue<ActivityCreatedListener>()
    private val activityResumedListeners = ConcurrentLinkedQueue<ActivityResumedListener>()
    private val activityPausedListeners = ConcurrentLinkedQueue<ActivityPausedListener>()
    private val activityDestroyedListeners = ConcurrentLinkedQueue<ActivityDestroyedListener>()

    @JvmStatic
    fun addListen(listener: ActivityCreatedListener) {
        activityCreatedListeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: ActivityCreatedListener) {
        activityCreatedListeners.remove(listener)
    }

    @JvmStatic
    fun addListen(listener: ActivityResumedListener) {
        activityResumedListeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: ActivityResumedListener) {
        activityResumedListeners.remove(listener)
    }

    @JvmStatic
    fun addListen(listener: ActivityPausedListener) {
        activityPausedListeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: ActivityPausedListener) {
        activityPausedListeners.remove(listener)
    }

    @JvmStatic
    fun addListen(listener: ActivityDestroyedListener) {
        activityDestroyedListeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: ActivityDestroyedListener) {
        activityDestroyedListeners.remove(listener)
    }

    internal fun pingResumedListeners(activity: Activity) {
        offerIfDiffer(activity)
        activityResumedListeners.forEach {
            it.onActivityResumed(activity)
        }
    }

    internal fun pingPausedListeners(activity: Activity) {
        activityPausedListeners.forEach {
            it.onActivityPaused(activity)
        }
    }

    internal fun pingCreatedListeners(activity: Activity) {
        offerIfDiffer(activity)
        activityCreatedListeners.forEach {
            it.onActivityCreated(activity)
        }
    }

    internal fun pingDestroyedListeners(activity: Activity) {
        activityDestroyedListeners.forEach {
            it.onActivityDestroyed(activity)
        }
    }

    private fun offerIfDiffer(newActivity: Activity){
        val current = currentActivity
        if(current == null || current != newActivity){
            _currentActivity.offer(WeakReference(newActivity))
        }
    }

    internal var _currentActivity = ConflatedBroadcastChannel<WeakReference<Activity>>()

    val listenCurrentActivity : Flow<Activity> = _currentActivity.asFlow().mapNotNull { it.get() }
    suspend fun activity() : Activity = listenCurrentActivity.first()

    @JvmStatic
    val currentActivity: Activity?
        get() {
            return _currentActivity.valueOrNull?.get()
        }
}

class LastActivityProvider : EmptyProvider() {
    override fun onCreate(): Boolean {
        ApplicationProvider.listen { application ->
            application.registerActivityLifecycleCallbacks(object :
                Application.ActivityLifecycleCallbacks {

                override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                    activity?.let {
                        ActivityProvider.pingCreatedListeners(activity)
                    }
                }

                override fun onActivityResumed(activity: Activity?) {
                    activity?.let {
                        ActivityProvider.pingResumedListeners(activity)
                    }
                }

                override fun onActivityPaused(activity: Activity?) {
                    activity?.let {
                        ActivityProvider.pingPausedListeners(activity)
                    }
                }

                override fun onActivityDestroyed(activity: Activity?) {
                    activity?.let {
                        ActivityProvider.pingDestroyedListeners(activity)
                    }
                }


                override fun onActivityStarted(activity: Activity?) {
                }

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                }

                override fun onActivityStopped(activity: Activity?) {
                }

            })
        }
        return true
    }
}