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

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvCenter.text = "适配器高效刷新"
        mBindingView.title.tvLeft.setOnClickListener {
            finish()
        }
        mBindingView.btnRefresh.setOnClickListener {
            mViewModel.pageNo = 2
            mViewModel.messageList()
        }
        initAdapter()
        initObserver()
        mViewModel.messageList()
    }

    private fun initAdapter() {
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
        //设置UI回调
        mAdapter.setDiffCallback(RefreshListAdapter.DiffDemoCallback())
        mBindingView.rvList.adapter = mAdapter
        mAdapter.setOnItemClickListener { a, v, p ->
            //更新单个数据这么操作即可
            mAdapter.setData(p, mAdapter.data[p].copy(name = "$p"))
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun initObserver() {
        lifecycleScope.launch {
            mViewModel.messageEvent.collect {
                measureTime {
                    mBindingView.srlRefresh.finishRefresh()
                    mBindingView.srlRefresh.finishLoadMore()
                    val data = it.toMutableList()
                    if (mViewModel.pageNo == 1) {
                        mAdapter.setDiffNewData(data)
                    } else {
                        //将所有数据源按一页Size的数量分组
                        val list = mAdapter.data.chunked(mViewModel.pageSize).toMutableList()
                        //如果分组数量小于当前页数，代表新增数据
                        if (list.size < mViewModel.pageNo) {
                            list.add(it)
                        } else {
                            list[mViewModel.pageNo - 1] = it
                        }
                        //所有数据重新叠加
                        val emptyList = mutableListOf<MessageListBean>()
                        list.map {
                            emptyList.addAll(it)
                        }
                        //刷新UI
                        mAdapter.setDiffNewData(emptyList)
                    }
                }.let {
                    LogUtil.d("加载时间 -> ${it.inWholeMilliseconds} 毫秒")
                }
            }
        }
    }
}