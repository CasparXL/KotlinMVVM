package com.caspar.xl

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.caspar.base.base.BaseActivity
import com.caspar.base.helper.Utils
import com.caspar.xl.config.ARouterApi
import com.caspar.xl.databinding.ActivityMainBinding
import com.caspar.xl.network.util.GsonUtils
import com.caspar.xl.viewmodel.TestViewModel

@Route(path = ARouterApi.MAIN)
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    //需要使用ViewModel的界面使用如下方式声名ViewModel，by用来委托前面的 ViewModel.class
    private val mViewModel : TestViewModel by viewModels()

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
                if (bean.first) {//first字段是网络请求是否成功
                    hideDialog()
                    bean.second?.let {//second是网络请求成功后的数据
                        mBindingView.tvText.text = Utils.stringToJSON(GsonUtils.toJson(it))
                    }
                }else{ //这里是网络请求失败的回调
                    hideDialog()
                    bean.third.let {//third是网络请求失败后的数据
                        mBindingView.tvText.text = "code->${it.status},msg->${it.msg}"
                    }
                }
            }
        })

    }
}
