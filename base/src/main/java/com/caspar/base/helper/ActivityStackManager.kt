package com.caspar.base.helper

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.os.Bundle
import com.caspar.base.utils.log.eLog
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

    /**
     * 初始化activity堆栈管理类
     */
    fun init(application: Application) {
        this.application = application
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 获取栈顶的 Activity
     */
    val topActivity: Activity?
        get() = mActivitySet.lastOrNull()

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
                    try {
                        set.finish()
                    } catch (e: ActivityNotFoundException) {
                        e.eLog()
                    }
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
                    try {
                        set.finish()
                    } catch (e: ActivityNotFoundException) {
                        e.eLog()
                    }
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
        if (mActivitySet.contains(activity)){
            mActivitySet.remove(activity)
        }
    }

}
