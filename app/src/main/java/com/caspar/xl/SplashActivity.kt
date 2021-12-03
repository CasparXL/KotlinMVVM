package com.caspar.xl

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.acStart
import com.caspar.xl.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 后台返回时可能启动这个页面 http://blog.csdn.net/jianiuqi/article/details/54091181
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // 处理启动画面转换。该代码需要在setContentView之前调用
            val splashScreen = installSplashScreen()
        }
        super.onCreate(savedInstanceState)

    }

    override fun initIntent() {}

    override fun initView(savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenCreated {
            delay(2000) //延时两秒跳转界面
            finish()
            startMain()
        }
    }

    //两秒后跳转首页，由于初始化问题，可能时间会久一点，此时就不延时跳转了，根据手机性能决定什么时候跳转
    private fun startMain() {
        acStart<MainActivity>()
    }

}


