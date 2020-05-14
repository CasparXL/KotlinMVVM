package com.caspar.xl.app

import android.app.Application
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.ToastAliPayStyle
import com.caspar.base.helper.ActivityStackManager
import com.caspar.base.helper.LogUtil
import com.caspar.xl.BuildConfig
import com.caspar.xl.MainActivity
import com.caspar.xl.R
import com.caspar.xl.db.ObjectBox
import com.caspar.xl.ui.CrashActivity
import com.caspar.xl.utils.rxjava.RxBus
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import io.objectbox.android.AndroidObjectBrowser
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits


/**
 * 初始化Application
 */
class BaseApplication : MultiDexApplication() {

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
        ObjectBox.init(this)//初始化数据库
        if (BuildConfig.DEBUG) {//开启浏览器访问ObjectBox
            val started = AndroidObjectBrowser(ObjectBox.boxStore).start(this)
            LogUtil.e("" + started)
        }
        RxBus.init()//RxBus初始化，用于全局WebSocket网络请求发送
        //Toast弹框初始化
        ToastUtils.init(context)
        ToastUtils.initStyle(ToastAliPayStyle(context))
        ToastUtils.setGravity(Gravity.BOTTOM, 0, 100)//Toast默认在中间，该设置将toast设置到距离底部100px
        //全局堆栈管理初始化
        ActivityStackManager.init(context)
        //阿里路由跳转初始化，这里要注意，若打签名包，除第一次签名包外，以后的都要修改项目的versionCode和VersionName，否则新增界面可能会无法生效[阿里框架的机制]
        // These two lines must be written before init, otherwise these configurations will be invalid in the init process
        ARouter.openLog() // Print log
        ARouter.openDebug() // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        ARouter.init(this)
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
        //全局化适配
        AutoSizeConfig.getInstance()
            .setBaseOnWidth(true)
            .unitsManager
            .supportSubunits = Subunits.MM
    }


    companion object {
        //Application上下文
        lateinit var context: Application

        init {
            //启用矢量图兼容
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            //设置全局默认配置（优先级最低，会被其他设置覆盖）
            SmartRefreshLayout.setDefaultRefreshInitializer { _, layout -> //全局设置（优先级最低）
                //设置是否监听列表在滚动到底部时触发加载事件（默认true）
                layout.setEnableAutoLoadMore(true)
                // 设置是否启用越界拖动（仿苹果效果）
                layout.setEnableOverScrollDrag(false)
                //设置是否启用越界回弹
                layout.setEnableOverScrollBounce(true)
                //设置在内容不满一页的时候，是否可以上拉加载更多
                layout.setEnableLoadMoreWhenContentNotFull(true)
                //是否在刷新完成之后滚动内容显示新数据
                layout.setEnableScrollContentWhenRefreshed(true)
                layout.setPrimaryColorsId(R.color.appColor, android.R.color.white)
            }

            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> //全局设置主题颜色（优先级第二低，可以覆盖 DefaultRefreshInitializer 的配置，与下面的ClassicsHeader绑定）
                MaterialHeader(context).setColorSchemeResources(R.color.appColor, android.R.color.white)
            }
        }
    }
}