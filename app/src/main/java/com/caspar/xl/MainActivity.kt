package com.caspar.xl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.base.base.FragmentPagerAdapter
import com.caspar.base.ext.*
import com.caspar.base.utils.log.dLog
import com.caspar.xl.databinding.ActivityMainBinding
import com.caspar.xl.ext.binding
import com.caspar.xl.helper.createNetty
import com.caspar.xl.helper.isInit
import com.caspar.xl.network.util.isPortAvailable
import com.caspar.xl.ui.fragment.HomeFragment
import com.caspar.xl.ui.fragment.MineFragment
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), View.OnClickListener {
    private val mBindingView: ActivityMainBinding by binding()
    private lateinit var mPagerAdapter: FragmentPagerAdapter<Fragment>

    private var service: NettyApplicationEngine? = null

    override fun initView(savedInstanceState: Bundle?) {
        initViewPager()
        setOnClickListener(this, R.id.tv_home, R.id.tv_mine)
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                delay(1000)
                if (8080.isPortAvailable()) {
                    service = (service ?: createNetty(8080))
                    if (service?.isInit() == false){
                        "未初始化服务,启动Netty服务".dLog()
                        service?.start(wait = true)
                    } else {
                        "服务已启动,因此不再重复启动Netty服务".dLog()
                    }
                }
            }
        }
    }

    private fun initViewPager() {
        mPagerAdapter = FragmentPagerAdapter(this)
        mPagerAdapter.addFragment(HomeFragment())
        mPagerAdapter.addFragment(MineFragment())
        mBindingView.nvPager.adapter = mPagerAdapter
        // 限制页面数量
        mBindingView.nvPager.offscreenPageLimit = mPagerAdapter.count
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_home -> {
                mBindingView.nvPager.currentItem = 0
            }

            R.id.tv_mine -> {
                mBindingView.nvPager.currentItem = 1
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (service?.isInit() == true){
            service?.stop(500, 500)
            service = null
            "关闭Netty服务".dLog()
        }
    }
}
