package com.caspar.xl

import android.content.Intent
import android.os.Bundle
import com.caspar.base.annotations.ContentView
import com.caspar.base.base.BaseActivity
import com.caspar.base.base.BaseViewModel
import com.caspar.xl.config.ARouterApi
import com.caspar.xl.databinding.ActivitySplashBinding


@ContentView(R.layout.activity_splash)
class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {

    override fun initIntent() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        // 后台返回时可能启动这个页面 http://blog.csdn.net/jianiuqi/article/details/54091181
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        startMain()
    }

    //两秒后跳转首页，由于初始化问题，可能时间会久一点，此时就不延时跳转了，根据手机性能决定什么时候跳转
    private fun startMain() {
        arStart(ARouterApi.MAIN)
        finish()
    }

}


