package com.caspar.xl

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.caspar.base.annotations.ContentView
import com.caspar.base.base.BaseActivity
import com.caspar.base.helper.Utils
import com.caspar.xl.config.ARouterApi
import com.caspar.xl.databinding.ActivityMainBinding
import com.caspar.xl.network.util.GsonUtils
import com.caspar.xl.viewmodel.TestViewModel

@Route(path = ARouterApi.MAIN)
@ContentView(R.layout.activity_main)
class MainActivity : BaseActivity<TestViewModel, ActivityMainBinding>() {

    override fun initIntent() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.tvText.movementMethod = ScrollingMovementMethod.getInstance()//TextView支持滑动
        initObserver()
        mViewModel?.getCity()
    }
    //LiveData的订阅都在该方法中，并且他们只会在OnCreate方法中订阅一次
    private fun initObserver() {
        mViewModel?.mData?.observe(this, Observer { bean ->
            run {
                bean?.apply {
                    mBindingView.tvText.text = Utils.stringToJSON(GsonUtils.toJson(this))
                }
            }
        })
        mViewModel?.mError?.observe(this, Observer { bean ->
            run {
                bean?.apply {
                    mBindingView.tvText.text = "code->${bean.status},msg->${bean.msg}"
                }
            }
        })
    }
}
