package com.github.florent37.application.provider

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
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

interface ActivityStoppedListener {
    fun onActivityStopped(activity: Activity)
}

interface ActivityStartedListener {
    fun onActivityStarted(activity: Activity)
}

enum class ActivityState {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY
}

data class ActivityAndState(val activity: Activity, val state: ActivityState)

object ActivityProvider {
    private val activityCreatedListeners = ConcurrentLinkedQueue<ActivityCreatedListener>()
    private val activityResumedListeners = ConcurrentLinkedQueue<ActivityResumedListener>()
    private val activityPausedListeners = ConcurrentLinkedQueue<ActivityPausedListener>()
    private val activityStoppedListeners = ConcurrentLinkedQueue<ActivityStoppedListener>()
    private val activityStartedListeners = ConcurrentLinkedQueue<ActivityStartedListener>()
    private val activityDestroyedListeners = ConcurrentLinkedQueue<ActivityDestroyedListener>()

    @JvmStatic
    fun addCreatedListener(listener: ActivityCreatedListener) {
        activityCreatedListeners.add(listener)
    }

    @JvmStatic
    fun removeCreatedListener(listener: ActivityCreatedListener) {
        activityCreatedListeners.remove(listener)
    }

    @JvmStatic
    fun addResumedListener(listener: ActivityResumedListener) {
        activityResumedListeners.add(listener)
    }

    @JvmStatic
    fun removeResumedListener(listener: ActivityResumedListener) {
        activityResumedListeners.remove(listener)
    }

    @JvmStatic
    fun addPausedListener(listener: ActivityPausedListener) {
        activityPausedListeners.add(listener)
    }

    @JvmStatic
    fun removePausedListener(listener: ActivityPausedListener) {
        activityPausedListeners.remove(listener)
    }

    @JvmStatic
    fun addDestroyedListener(listener: ActivityDestroyedListener) {
        activityDestroyedListeners.add(listener)
    }

    @JvmStatic
    fun removeDestroyedListener(listener: ActivityDestroyedListener) {
        activityDestroyedListeners.remove(listener)
    }

    @JvmStatic
    fun addStoppedListener(listener: ActivityStoppedListener) {
        activityStoppedListeners.add(listener)
    }

    @JvmStatic
    fun removeStoppedListener(listener: ActivityStoppedListener) {
        activityStoppedListeners.remove(listener)
    }

    @JvmStatic
    fun addStartedListener(listener: ActivityStartedListener) {
        activityStartedListeners.add(listener)
    }

    @JvmStatic
    fun removeStartedListener(listener: ActivityStartedListener) {
        activityStartedListeners.remove(listener)
    }

    internal fun pingResumedListeners(activity: Activity) {
        _activitiesState.offer(ActivityAndState(activity, ActivityState.RESUME))
        offerIfDiffer(activity)
        activityResumedListeners.forEach {
            it.onActivityResumed(activity)
        }
    }

    internal fun pingPausedListeners(activity: Activity) {
        _activitiesState.offer(ActivityAndState(activity, ActivityState.PAUSE))
        activityPausedListeners.forEach {
            it.onActivityPaused(activity)
        }
    }

    internal fun pingCreatedListeners(activity: Activity) {
        offerIfDiffer(activity)
        _activitiesState.offer(ActivityAndState(activity, ActivityState.CREATE))
        activityCreatedListeners.forEach {
            it.onActivityCreated(activity)
        }
    }

    internal fun pingDestroyedListeners(activity: Activity) {
        _activitiesState.offer(ActivityAndState(activity, ActivityState.DESTROY))
        activityDestroyedListeners.forEach {
            it.onActivityDestroyed(activity)
        }
    }

    internal fun pingStartedListeners(activity: Activity) {
        _activitiesState.offer(ActivityAndState(activity, ActivityState.START))
        activityStartedListeners.forEach {
            it.onActivityStarted(activity)
        }
    }

    internal fun pingStoppedListeners(activity: Activity) {
        _activitiesState.offer(ActivityAndState(activity, ActivityState.STOP))
        activityStoppedListeners.forEach {
            it.onActivityStopped(activity)
        }
    }

    private fun offerIfDiffer(newActivity: Activity) {
        val current = currentActivity
        if (current == null || current != newActivity) {
            _currentActivity.offer(WeakReference(newActivity))
        }
    }

    internal val _currentActivity = ConflatedBroadcastChannel<WeakReference<Activity>>()

    val listenCurrentActivity: Flow<Activity> = _currentActivity.asFlow().mapNotNull { it.get() }
    suspend fun activity(): Activity = listenCurrentActivity.first()

    @JvmStatic
    val currentActivity: Activity?
        get() {
            return _currentActivity.valueOrNull?.get()
        }


    internal val _activitiesState = ConflatedBroadcastChannel<ActivityAndState>()
    val listenActivitiesState: Flow<ActivityAndState> = _activitiesState.asFlow()

    fun listenCreated() = callbackFlow<Activity> {
        val listener = object : ActivityCreatedListener { // implementation of some callback interface
            override fun onActivityCreated(activity: Activity) {
                offer(activity)
            }
        }
        addCreatedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeCreatedListener(listener) }
    }

    fun listenStarted() = callbackFlow<Activity> {
        val listener = object : ActivityStartedListener { // implementation of some callback interface
            override fun onActivityStarted(activity: Activity) {
                offer(activity)
            }
        }
        addStartedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeStartedListener(listener) }
    }

    fun listenResumed() = callbackFlow<Activity> {
        val listener = object : ActivityResumedListener { // implementation of some callback interface
            override fun onActivityResumed(activity: Activity) {
                offer(activity)
            }
        }
        addResumedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeResumedListener(listener) }
    }

    fun listenDestroyed() = callbackFlow<Activity> {
        val listener = object : ActivityDestroyedListener { // implementation of some callback interface
            override fun onActivityDestroyed(activity: Activity) {
                offer(activity)
            }
        }
        addDestroyedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeDestroyedListener(listener) }
    }

    fun listenStopped() = callbackFlow<Activity> {
        val listener = object : ActivityStoppedListener { // implementation of some callback interface
            override fun onActivityStopped(activity: Activity) {
                offer(activity)
            }
        }
        addStoppedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeStoppedListener(listener) }
    }

    fun listenPaused() = callbackFlow<Activity> {
        val listener = object : ActivityPausedListener { // implementation of some callback interface
            override fun onActivityPaused(activity: Activity) {
                offer(activity)
            }
        }
        addPausedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removePausedListener(listener) }
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
                    activity?.let {
                        ActivityProvider.pingStartedListeners(activity)
                    }
                }

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                }

                override fun onActivityStopped(activity: Activity?) {
                    activity?.let {
                        ActivityProvider.pingStoppedListeners(activity)
                    }
                }

            })
        }
        return true
    }
}