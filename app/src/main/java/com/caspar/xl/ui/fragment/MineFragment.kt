package com.caspar.xl.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caspar.base.base.BaseFragment
import com.caspar.base.utils.log.dLog
import com.caspar.xl.databinding.FragmentMineBinding
import com.caspar.xl.ext.binding
import com.caspar.xl.network.util.getIPAddress
import dagger.hilt.android.AndroidEntryPoint

/**
 *  @Create 2020/6/13.
 *  @Use
 */
@AndroidEntryPoint
class MineFragment : BaseFragment() {
    private val mBindingView: FragmentMineBinding by binding()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return mBindingView.root
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.tvName.text = getIPAddress()
    }
}