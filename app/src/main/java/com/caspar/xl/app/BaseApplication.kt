package com.caspar.xl.app

import android.app.Application
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.core.app.ActivityCompat
import androidx.multidex.MultiDexApplication
import cat.ereza.customactivityoncrash.config.CaocConfig
import coil.Coil
import coil.ComponentRegistry
import coil.ImageLoader
import com.caspar.base.helper.ActivityStackManager
import com.caspar.base.utils.log.LogFileManager
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.BuildConfig
import com.caspar.xl.MainActivity
import com.caspar.xl.R
import com.caspar.xl.helper.PauseInterceptor
import com.caspar.xl.ui.CrashActivity
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.BlackToastStyle
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


/**
 * 初始化Application
 */
@HiltAndroidApp
class BaseApplication : MultiDexApplication(), CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()
        context = this
        init()
    }

    //第三方框架或本地工具类初始化
    private fun init() {
        //打印日志初始化,打正式包将不再打印日志
        LogFileManager.initPath(packageName = packageName, parentPath = filesDir.path, name = "CustomLog")
        LogUtil.init(BuildConfig.LOG_ENABLE, "浪", mFile = LogFileManager.getEventLog())
        MMKV.initialize(this)//本地储存初始化
        //Toast弹框初始化
        ToastUtils.init(this)
        ToastUtils.setStyle(BlackToastStyle())
        ToastUtils.setGravity(Gravity.BOTTOM, 0, 100)
        //全局堆栈管理初始化
        ActivityStackManager.init(context)
        //Coil增加拦截器，用于判断RecyclerView快速滑动卡顿的问题
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .placeholder(ActivityCompat.getDrawable(this, R.drawable.image_loading_bg))
                .error(ActivityCompat.getDrawable(this, R.drawable.image_loading_bg))
                .components(fun ComponentRegistry.Builder.() {
                    add(PauseInterceptor())
                })
                .build()
        )
        // Crash 捕捉界面
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
            .enabled(true)
            .trackActivities(true)
            .minTimeBetweenCrashesMs(2000) // 重启的 Activity
            .restartActivity(MainActivity::class.java) // 错误的 Activity
            .errorActivity(CrashActivity::class.java) // 设置监听器
            .apply()
    }

    companion object {
        //Application上下文
        lateinit var context: Application

        //全局使用的协程，因为官方不推荐使用GlobalScope，因此在Application中创建一个全局的协程以便于非Activity，ViewModel的类使用协程
        var job = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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
                layout.setPrimaryColorsId(R.color.appColor, android.R.color.black)
            }
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
                ClassicsFooter(context).setDrawableSize(20f)
            }
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> //全局设置主题颜色（优先级第二低，可以覆盖 DefaultRefreshInitializer 的配置，与下面的ClassicsHeader绑定）
                MaterialHeader(context).setColorSchemeResources(R.color.appColor,
                    android.R.color.black)
            }
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}
