package com.caspar.xl.ui.fragment

import android.content.Context
import android.os.Bundle
import com.caspar.base.base.BaseFragment
import com.caspar.base.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.databinding.FragmentHomeBinding
import com.caspar.xl.databinding.FragmentMineBinding

/**
 *  @Create 2020/6/13.
 *  @Use
 */
class MineFragment :BaseFragment<FragmentMineBinding>(R.layout.fragment_mine){
    override fun initView(savedInstanceState: Bundle?) {
        LogUtil.e("切换MineFragment")
    }
}