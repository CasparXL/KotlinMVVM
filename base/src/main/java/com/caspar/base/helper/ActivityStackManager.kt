package com.caspar.base.helper

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.collection.ArrayMap
import java.util.*

object ActivityStackManager : ActivityLifecycleCallbacks {
    private val mActivitySet = Stack<Activity>()
    /**
     * 获取 Application 对象
     */
    /**
     * 当前应用上下文对象
     */
    var application: Application? = null
        private set

    /**
     * 当前 Activity 对象标记
     */
    private var mCurrentTag: String? = null

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
    fun finishAllActivities() {
        finishAllActivities(null)
    }

    /**
     * 销毁所有的 Activity，除这些 Class 之外的 Activity
     */
    @SafeVarargs
    fun finishAllActivities(vararg classArray: Class<out Activity>?) {
        for (key in mActivitySet) {
            if (key != null && !key.isFinishing) {
                var whiteClazz = false
                for (clazz in classArray) {
                    if (key.javaClass == clazz) {
                        whiteClazz = true
                    }
                }
                // 如果不是白名单上面的 Activity 就销毁掉
                if (!whiteClazz) {
                    LogUtil.e("销毁掉界面${key.localClassName}")
                    key.finish()
                    mActivitySet.remove(key)
                }
            }
        }
    }

    /**
     * 销毁部分Activity
     */
    @SafeVarargs
    fun finishActivities(vararg classArray: Class<out Activity>?) {
        for (key in mActivitySet) {
            if (key != null && !key.isFinishing) {
                var whiteClazz = true
                for (clazz in classArray) {
                    if (key.javaClass == clazz) {
                        whiteClazz = false
                    }
                }
                // 如果不是白名单上面的 Activity 就销毁掉
                if (whiteClazz) {
                    LogUtil.e("销毁掉界面${key.localClassName}")
                    key.finish()
                    mActivitySet.remove(key)
                }
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