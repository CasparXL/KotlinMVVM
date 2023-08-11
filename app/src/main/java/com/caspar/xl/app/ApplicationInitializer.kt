package com.caspar.xl.app

import android.app.Application
import android.content.Context
import android.view.Gravity
import androidx.startup.Initializer
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.caspar.base.helper.ActivityStackManager
import com.caspar.base.utils.log.LogFileManager
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.MainActivity
import com.caspar.xl.R
import com.caspar.xl.ui.CrashActivity
import com.hjq.toast.Toaster
import com.hjq.toast.style.BlackToastStyle
import com.tencent.mmkv.MMKV

class ApplicationInitializer : Initializer<String> {
    override fun create(context: Context): String {
        //Crash日志存放目录初始化
        LogFileManager.initPath(packageName = context.packageName, parentPath = context.filesDir.path, name = "CustomLog")
        //Log日志工具类初始化
        LogUtil.init(context.resources.getBoolean(R.bool.log_enable), "浪", mFile = LogFileManager.getEventLog())
        //本地储存初始化
        MMKV.initialize(context)
        //toast相关初始化
        Toaster.init(context as Application?)
        Toaster.setStyle(BlackToastStyle())
        Toaster.setGravity(Gravity.BOTTOM, 0, 100)
        //全局堆栈管理初始化
        ActivityStackManager.init(context)
        // Crash 捕捉界面
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
            .enabled(context.resources.getBoolean(R.bool.log_enable))
            .trackActivities(true)
            .minTimeBetweenCrashesMs(2000) // 重启的 Activity
            .restartActivity(MainActivity::class.java) // 错误的 Activity
            .errorActivity(CrashActivity::class.java) // 设置监听器
            .apply()
        return "初始化完成"
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}