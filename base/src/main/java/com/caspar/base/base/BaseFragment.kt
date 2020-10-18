package com.caspar.base.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * @author CasparXL
 * @time 2020/4/2
 */
abstract class BaseFragment<SV : ViewDataBinding>(@LayoutRes val contentLayoutId: Int) : Fragment() {
    /**
     * 绑定布局的ViewDatabinding,本项目中主要用于findViewById的作用，未来可用ViewBinding代替
     */
    protected lateinit var mBindingView: SV

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBindingView = DataBindingUtil.inflate(inflater, contentLayoutId, null, false)
        return mBindingView.root
    }

    //获取父Activity
    private fun getParentActivity(): Activity? {
        return activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView(savedInstanceState)
    }

    abstract fun initView(savedInstanceState: Bundle?)

    override fun onDestroy() {
        mBindingView.unbind()
        super.onDestroy()
    }

}