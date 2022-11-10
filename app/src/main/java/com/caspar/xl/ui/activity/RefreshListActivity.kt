package com.caspar.xl.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.databinding.ActivityRefreshListBinding
import com.caspar.xl.ui.adapter.MessageListBean
import com.caspar.xl.ui.adapter.RefreshListAdapter
import com.caspar.xl.viewmodel.RefreshListViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * DiffUtil刷新UI功能
 * 提高UI刷新速度
 */
class RefreshListActivity : BaseActivity<ActivityRefreshListBinding>() {
    val mAdapter: RefreshListAdapter = RefreshListAdapter()
    val mViewModel: RefreshListViewModel by viewModels()

    @OptIn(ExperimentalTime::class)
    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvLeft.setOnClickListener {
            mViewModel.pageNo = 2
            mViewModel.messageList()
        }
        mBindingView.srlRefresh.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mViewModel.pageNo = 1
                mViewModel.messageList()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                //页数不够，则上拉加载时重新加载第二页
                if (mAdapter.itemCount < mViewModel.pageNo * mViewModel.pageSize) {
                    mViewModel.pageNo -= 1
                    if (mViewModel.pageNo < 1) {
                        mViewModel.pageNo = 1
                    }
                } else {
                    mViewModel.pageNo += 1
                }
                mViewModel.messageList()
            }
        })
        mBindingView.rvList.itemAnimator = null
        mAdapter.setDiffCallback(RefreshListAdapter.DiffDemoCallback())
        mBindingView.rvList.adapter = mAdapter
        mAdapter.setOnItemClickListener { a, v, p ->

        }
        lifecycleScope.launch {
            mViewModel.messageEvent.collect {
                measureTime {
                    mBindingView.srlRefresh.finishRefresh()
                    mBindingView.srlRefresh.finishLoadMore()
                    val data = it.toMutableList()
                    if (mViewModel.pageNo == 1) {
                        mAdapter.setDiffNewData(data)
                    } else {
                        val list = mAdapter.data.chunked(mViewModel.pageSize).toMutableList()
                        if (list.size < mViewModel.pageNo) {
                            list.add(it)
                        } else {
                            list[mViewModel.pageNo - 1] = it
                        }
                        val emptyList = mutableListOf<MessageListBean>()
                        list.map {
                            emptyList.addAll(it)
                        }
                        mAdapter.setDiffNewData(emptyList)
                    }
                }.let {
                    LogUtil.d("加载时间 -> ${it.inWholeMilliseconds} 毫秒")
                }
            }
        }
    }
}