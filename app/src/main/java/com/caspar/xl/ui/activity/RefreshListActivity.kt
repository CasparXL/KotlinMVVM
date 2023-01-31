package com.caspar.xl.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.databinding.ActivityRefreshListBinding
import com.caspar.xl.ext.binding
import com.caspar.xl.ui.adapter.RefreshListAdapter
import com.caspar.xl.ui.viewmodel.RefreshListViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * DiffUtil刷新UI功能
 * 提高UI刷新速度
 * 不论是什么顺序的数据,都可能存在问题，根据需求自行调整，规避最严重的bug即可,只要数据出现的频率不是很大，那么很多时候刷新页面数据就能解决当前已有的所有问题
 */
@AndroidEntryPoint
class RefreshListActivity : BaseActivity() {
    private val mBindingView: ActivityRefreshListBinding by binding()
    val mAdapter: RefreshListAdapter = RefreshListAdapter()
    val mViewModel: RefreshListViewModel by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvCenter.text = "适配器高效刷新"
        mBindingView.title.tvLeft.setOnClickListener {
            finish()
        }
        mBindingView.btnChanged.setOnClickListener {
            mViewModel.revered = !mViewModel.revered
            mBindingView.btnChanged.text =
                "数据逆反顺序切换，切刷新加载逻辑也变化(现在:${if (mViewModel.revered) "从大到小" else "从小到大"})"
            if (mViewModel.revered) {
                toast("新数据顶在第二页的前面,所以第二页出现重复数据，移除部分重复内容")
            } else {
                toast("新数据会直接顶到最后面")
            }
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
                mViewModel.pageNo += 1
                mViewModel.messageList()
            }
        })
        mBindingView.rvList.itemAnimator = null
        //设置UI回调
        mBindingView.rvList.adapter = mAdapter
        mAdapter.setOnItemClickListener { a, v, p ->
            //更新单个数据这么操作即可,
            //更多时候，单个数据更新是最合理的,点击进入详情界面，
            //做完操作以后，从详情请求最新数据，带回来到当前Item更新数据,这个时候只刷新当前Item的数据，即可达到最新状态
            //若当前设备详情查询不到,则代表已被移除,回来时移除当前Item即可
            mAdapter[p] = mAdapter.items[p].copy(name = "$p")
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun initObserver() {
        lifecycleScope.launch {
            mViewModel.messageEvent.collect {
                //没有更多数据时，pageNo-1,不刷新列表
                if (it.isEmpty() && mViewModel.pageNo != 1) {
                    mViewModel.pageNo--
                    return@collect
                }
                //新增数据不是在列表最前方的话(即最新数据在列表最顶部的情况(当前时间的数据在最顶部))，该方法可以高效刷新当前页或者加载更多数据
                measureTime {
                    mBindingView.srlRefresh.finishRefresh()
                    mBindingView.srlRefresh.finishLoadMore()
                    val data = it.toMutableList()
                    if (mViewModel.pageNo == 1) {
                        mAdapter.submitList(data)
                    } else {
                        //将所有数据源按一页Size的数量分组
                        val list = mAdapter.items.chunked(mViewModel.pageSize).toMutableList()
                        //如果分组数量小于当前页数，代表新增数据
                        if (list.size < mViewModel.pageNo) {
                            list.add(it)
                        } else {
                            list[mViewModel.pageNo - 1] = it
                        }
                        //所有数据重新叠加
                        val emptyList = list.flatten()
                        //当排序是反方向时,先排序反方向，然后筛选掉旧数据，最终在反方向回来
                        val finalList = emptyList.reversed().distinctBy { db -> db.id }.reversed()
                            .toMutableList()
                        //刷新UI
                        mAdapter.submitList(finalList)
                        //最终列表没有达到当前页需要的数量时，pageNo - 1
                        // (正常来说从大到小的顺序时(即时间顺序从现在到以前)，第二页出现了重复数据，才有可能减少pageNo的情况)
                        if (finalList.size < mViewModel.pageNo * mViewModel.pageSize) {
                            mViewModel.pageNo--
                        }
                    }
                }.let {
                    LogUtil.d("加载时间 -> ${it.inWholeMilliseconds} 毫秒")
                }
            }
        }
    }
}