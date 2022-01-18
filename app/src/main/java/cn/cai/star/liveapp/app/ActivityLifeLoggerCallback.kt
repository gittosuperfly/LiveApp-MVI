package cn.cai.star.liveapp.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import java.lang.ref.WeakReference

object ActivityLifeLoggerCallback : Application.ActivityLifecycleCallbacks {

    private const val TAG = "Life"

    var currentActivity: WeakReference<Activity>? = null
    var previousActivity: WeakReference<Activity>? = null

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        checkAndCreateActivityReference(activity)
        Log.i(TAG, "Activity [create] = ${activity.javaClass.name}")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        checkAndCreateActivityReference(activity)
        Log.i(TAG, "Activity [resume] = ${activity.javaClass.name}")
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivityPostDestroyed(activity: Activity) {
        Log.i(TAG, "Activity [destroyed] = ${activity.javaClass.name}")
    }

    private fun checkAndCreateActivityReference(activity: Activity) {
        if (currentActivity == null || currentActivity!!.get() != activity) {
            previousActivity = currentActivity
            currentActivity = WeakReference(activity)
        }
    }
}