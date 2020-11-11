package com.caspar.base.helper

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
        val mutableList: MutableList<Activity> = ArrayList()
        for (key in mActivitySet) {
            if (key != null && !key.isFinishing) {
                var whiteClazz = false
                for (clazz in classArray) {
                    if (key.javaClass == clazz?.javaClass) {
                        whiteClazz = true
                    }
                }
                // 如果不是白名单上面的 Activity 就销毁掉
                if (!whiteClazz) {
                    LogUtil.e("remove activity ${key.localClassName}")
                    key.finish()
                    mutableList.add(key)
                }
            }
        }
        if (mutableList.size > 0) {
            mActivitySet.removeAll(mutableList)
            mutableList.clear()
        }
    }

    /**
     * 销毁部分Activity
     */
    @SafeVarargs
    fun finishActivities(vararg classArray: Class<out Activity>) {
        val mutableList: MutableList<Activity> = ArrayList()
        for (key in mActivitySet) {
            key?.apply {
                if (!this.isFinishing) {
                    var whiteClazz = true
                    for (clazz in classArray) {
                        if (key.javaClass == clazz.javaClass) {
                            whiteClazz = false
                        }
                    }
                    // 如果不是白名单上面的 Activity 就销毁掉
                    if (whiteClazz) {
                        LogUtil.e("remove activity ${key.localClassName}")
                        key.finish()
                        mutableList.add(key)
                    }
                }
            }
        }
        if (mutableList.size > 0) {
            mActivitySet.removeAll(mutableList)
            mutableList.clear()
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
