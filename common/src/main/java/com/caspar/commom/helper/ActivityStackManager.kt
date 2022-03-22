package com.caspar.commom.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

object ActivityStackManager : Application.ActivityLifecycleCallbacks {
    private val mActivitySet = Stack<Activity>()
    /**
     * 获取 Application 对象
     */
    /**
     * 当前应用上下文对象
     */
    var application: Application? = null
        private set

    fun init(application: Application) {
        this.application = application
        application.registerActivityLifecycleCallbacks(this)
    }

    /**

     * 获取栈顶的 Activity

     */
    val topActivity: Activity
        get() = mActivitySet.lastElement()

    /**

     * 销毁所有的 Activity

     */
    fun finishAllActivity() {
        finishOtherActivity()
    }

    /**
     * 销毁所有的 Activity，除这些 Class 之外的 Activity
     */
    @SafeVarargs
    fun finishOtherActivity(vararg classArray: Class<out Activity>?) {
        mActivitySet.removeAll { set ->
            if (!set.isFinishing) {
                if (classArray.contains(set.javaClass)) {
                    true
                } else {
                    set.finish()
                    false
                }
            } else {
                true
            }
        }
    }

    /**
     * 销毁部分Activity
     */
    @SafeVarargs
    fun finishActivities(vararg classArray: Class<out Activity>) {
        mActivitySet.removeAll { set ->
            if (!set.isFinishing) {
                if (classArray.contains(set.javaClass)) {
                    set.finish()
                    true
                } else {
                    false
                }
            } else {
                true
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mActivitySet.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        mActivitySet.remove(activity)
    }

}
