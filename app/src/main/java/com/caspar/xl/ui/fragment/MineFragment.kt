package com.caspar.xl.ui.fragment

import android.os.Bundle
import com.caspar.base.base.BaseFragment
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.databinding.FragmentMineBinding

/**
 *  @Create 2020/6/13.
 *  @Use
 */
class MineFragment :BaseFragment<FragmentMineBinding>(){
    override fun initView(savedInstanceState: Bundle?) {
        LogUtil.e("切换MineFragment")
    }
}