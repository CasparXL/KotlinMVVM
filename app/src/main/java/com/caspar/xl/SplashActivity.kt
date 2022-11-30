package com.caspar.xl

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.acStart
import com.caspar.xl.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override fun getViewBinding(): ViewBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }


    override fun initView(savedInstanceState: Bundle?) {
        // 后台返回时可能启动这个页面 http://blog.csdn.net/jianiuqi/article/details/54091181
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        lifecycleScope.launch {
            delay(2000) //延时两秒跳转界面
            startMain()
        }
    }

    //两秒后跳转首页，由于初始化问题，可能时间会久一点，此时就不延时跳转了，根据手机性能决定什么时候跳转
    private fun startMain() {
        acStart<MainActivity>()
        finish()
    }

}


