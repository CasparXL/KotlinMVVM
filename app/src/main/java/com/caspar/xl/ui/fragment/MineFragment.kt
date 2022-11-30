package com.caspar.xl.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.caspar.base.base.BaseFragment
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.databinding.FragmentMineBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 *  @Create 2020/6/13.
 *  @Use
 */
@AndroidEntryPoint
class MineFragment : BaseFragment() {
    private lateinit var mBindingView: FragmentMineBinding

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
        return FragmentMineBinding.inflate(inflater, container, false).apply {
            mBindingView = this
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        LogUtil.e("切换MineFragment")
    }
}