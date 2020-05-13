package com.caspar.xl

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
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
class MainActivity : BaseActivity<ActivityMainBinding>() {

    //需要使用ViewModel的界面使用如下方式声名ViewModel，by用来委托前面的 ViewModel.class
    private val mViewModel: TestViewModel by viewModels()

    override fun initIntent() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.tvText.movementMethod = ScrollingMovementMethod.getInstance()//TextView支持滑动
        initObserver()
        showLoadingDialog("网络请求中")
        mViewModel.getCity()
    }

    //LiveData的订阅都在该方法中，并且只需要在onCreate中调用一次即可
    private fun initObserver() {
        //当接口获取到数据以后将会回调到当前回调
        mViewModel.mData.observe(this, Observer { bean ->
            run {
                hideDialog()
                bean?.apply {
                    mBindingView.tvText.text = Utils.stringToJSON(GsonUtils.toJson(this))
                }
            }
        })
        //当接口出现错误时会返回到这个回调之中
        mViewModel.mError.observe(this, Observer { bean ->
            run {
                hideDialog()
                bean?.apply {
                    mBindingView.tvText.text = "code->${bean.status},msg->${bean.msg}"
                }
            }
        })
    }
}
