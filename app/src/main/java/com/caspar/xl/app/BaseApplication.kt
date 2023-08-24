package com.caspar.xl.app

import android.app.Application
import android.os.Build
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.multidex.MultiDexApplication
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import dagger.hilt.android.HiltAndroidApp


/**
 * 初始化Application
 */
@HiltAndroidApp
class BaseApplication : MultiDexApplication(), CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()
        context = this
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
                layout.setPrimaryColorsId(com.caspar.base.R.color.appColor, android.R.color.black)
            }
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
                ClassicsFooter(context).setDrawableSize(20f)
            }
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> //全局设置主题颜色（优先级第二低，可以覆盖 DefaultRefreshInitializer 的配置，与下面的ClassicsHeader绑定）
                MaterialHeader(context).setColorSchemeResources(
                    com.caspar.base.R.color.appColor,
                    android.R.color.black)
            }
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}
