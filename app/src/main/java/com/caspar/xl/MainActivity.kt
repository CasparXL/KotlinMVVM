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
import com.caspar.xl.network.util.isPortAvailable
import com.caspar.xl.ui.fragment.HomeFragment
import com.caspar.xl.ui.fragment.MineFragment
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), View.OnClickListener {
    private val mBindingView: ActivityMainBinding by binding()
    private lateinit var mPagerAdapter: FragmentPagerAdapter<Fragment>

    @Inject
    lateinit var service: NettyApplicationEngine

    override fun initView(savedInstanceState: Bundle?) {
        initViewPager()
        setOnClickListener(this, R.id.tv_home, R.id.tv_mine)
        lifecycleScope.launch(Dispatchers.IO) {
            if (8080.isPortAvailable()) {
                "启动本地服务器".dLog()
                service.start(true)
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

}
