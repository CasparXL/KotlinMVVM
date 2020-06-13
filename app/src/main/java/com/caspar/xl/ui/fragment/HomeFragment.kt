package com.caspar.xl.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.caspar.base.base.BaseFragment
import com.caspar.base.ext.dip
import com.caspar.base.ext.hide
import com.caspar.base.utils.permissions.OnPermission
import com.caspar.base.utils.permissions.Permission
import com.caspar.base.utils.permissions.XXPermissions
import com.caspar.xl.R
import com.caspar.xl.config.ARouterApi
import com.caspar.xl.databinding.FragmentHomeBinding
import com.caspar.xl.ui.adapter.HomeMenuAdapter
import com.caspar.xl.utils.decoration.Decoration
import com.caspar.xl.viewmodel.HomeViewModel

/**
 *  @Create 2020/6/13.
 *  @Use
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    val mAdapter: HomeMenuAdapter by lazy { HomeMenuAdapter() }

    val mViewModel: HomeViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvLeft.hide()
        initAdapter()
    }

    private fun initAdapter() {
        mBindingView.rvList.layoutManager = GridLayoutManager(context, 2)
        mBindingView.rvList.addItemDecoration(
            Decoration.GridDecoration(
                2,
                context?.dip(10) ?: 0,
                true
            )
        )
        mBindingView.rvList.adapter = mAdapter
        mAdapter.setList(mViewModel.mData)
        mAdapter.setOnItemClickListener { _, _, position ->
            run {
                if (position == 0) {
                    arStart(ARouterApi.TRANSLATE)
                } else if (position == 1) {
                    mViewModel.permission()
                }
            }
        }
    }

}