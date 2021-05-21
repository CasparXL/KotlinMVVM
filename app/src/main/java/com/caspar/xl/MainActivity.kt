package com.caspar.xl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.helper.LogUtil
import com.caspar.xl.databinding.ActivityMainBinding
import com.caspar.xl.ui.adapter.BaseFragmentAdapter
import com.caspar.xl.ui.fragment.HomeFragment
import com.caspar.xl.ui.fragment.MineFragment
import java.lang.Exception

class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {
    private lateinit var mPagerAdapter: BaseFragmentAdapter<Fragment>

    override fun initIntent() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        setOnClickListener(this, R.id.tv_home, R.id.tv_mine)
        initViewPager()
    }

    private fun initViewPager() {
        mPagerAdapter = BaseFragmentAdapter(this)
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
