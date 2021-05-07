package com.caspar.xl.app

import android.app.Application
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.multidex.MultiDexApplication
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.caspar.base.helper.ActivityStackManager
import com.caspar.base.helper.LogUtil
import com.caspar.xl.BuildConfig
import com.caspar.xl.MainActivity
import com.caspar.xl.R
import com.caspar.xl.ui.CrashActivity
import com.caspar.xl.utils.rxjava.RxBus
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.BlackToastStyle
import com.tencent.mmkv.MMKV


/**
 * 初始化Application
 */
class BaseApplication : MultiDexApplication(), CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()
        context = this
        init()
    }

    //第三方框架或本地工具类初始化
    private fun init() {
        //打印日志初始化,打正式包将不再打印日志
        LogUtil.init(BuildConfig.LOG_ENABLE, "浪")
        MMKV.initialize(this)//本地储存初始化
        RxBus.init()//RxBus初始化，用于全局WebSocket网络请求发送
        //Toast弹框初始化
        ToastUtils.init(this)
        ToastUtils.setStyle(BlackToastStyle())
        ToastUtils.setGravity(Gravity.BOTTOM, 0, 100)
        //全局堆栈管理初始化
        ActivityStackManager.init(context)
        // Crash 捕捉界面
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
            .enabled(true)
            .trackActivities(true)
            .minTimeBetweenCrashesMs(2000) // 重启的 Activity
            .restartActivity(MainActivity::class.java) // 错误的 Activity
            .errorActivity(CrashActivity::class.java) // 设置监听器
            //.eventListener(new YourCustomEventListener())
            .apply()
    }


    companion object {
        //Application上下文
        lateinit var context: Application

        init {
            //启用矢量图兼容
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            //设置全局默认配置（优先级最低，会被其他设置覆盖）
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}
