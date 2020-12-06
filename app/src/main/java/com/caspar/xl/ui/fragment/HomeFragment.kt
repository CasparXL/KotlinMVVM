package com.caspar.xl.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.caspar.base.base.BaseFragment
import com.caspar.base.ext.acStart
import com.caspar.base.ext.dp
import com.caspar.base.ext.hide
import com.caspar.xl.databinding.FragmentHomeBinding
import com.caspar.xl.ui.activity.RoomActivity
import com.caspar.xl.ui.activity.TranslateActivity
import com.caspar.xl.ui.adapter.HomeMenuAdapter
import com.caspar.xl.utils.decoration.Decoration
import com.caspar.xl.viewmodel.HomeViewModel

/**
 *  @Create 2020/6/13.
 *  @Use
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val mAdapter: HomeMenuAdapter by lazy { HomeMenuAdapter() }
    private val mViewModel: HomeViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvLeft.hide()
        initAdapter()
    }

    private fun initAdapter() {
        mBindingView.rvList.layoutManager = GridLayoutManager(context, 2)
        mBindingView.rvList.addItemDecoration(Decoration.GridDecoration(2, 10.dp, true))
        mBindingView.rvList.adapter = mAdapter
        mAdapter.setList(mViewModel.mData)
        mAdapter.setOnItemClickListener { _, _, position ->
            run {
                when (position) {
                    0 -> {
                        acStart(TranslateActivity::class.java)
                    }
                    1 -> {
                        mViewModel.permission()
                    }
                    2 -> {
                        acStart(RoomActivity::class.java)
                    }
                }
            }
        }
    }

}