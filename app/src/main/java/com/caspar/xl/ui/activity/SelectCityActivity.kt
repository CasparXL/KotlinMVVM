package com.caspar.xl.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.caspar.base.base.BaseActivity
import com.caspar.xl.databinding.ActivitySelectCityBinding
import com.caspar.xl.ui.adapter.ItemCityAdapter
import com.caspar.xl.widget.index.bean.ItemData
import com.caspar.xl.widget.index.decoration.DivideItemDecoration
import com.caspar.xl.widget.index.decoration.GroupHeaderItemDecoration
import com.caspar.xl.widget.index.ext.sortByLetter
import com.caspar.xl.widget.index.listener.OnSideBarTouchListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCityActivity : BaseActivity() {
    private lateinit var mBindingView: ActivitySelectCityBinding
    private val mAdapter: ItemCityAdapter by lazy {
        ItemCityAdapter()
    }
    var tags: MutableList<ItemData> = mutableListOf()
    override fun getViewBinding(): ViewBinding {
        return ActivitySelectCityBinding.inflate(layoutInflater).apply {
            mBindingView = this
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.include.tvLeft.setOnClickListener { finish() }
        mBindingView.include.tvCenter.text = "城市列表"
        tags.add(ItemData("北京"))
        tags.add(ItemData("上海"))
        tags.add(ItemData("广州"))
        tags.add(ItemData("深圳"))
        tags.add(ItemData("西安"))
        tags.add(ItemData("成都"))
        tags.add(ItemData("南京"))
        tags.add(ItemData("三亚"))
        tags.add(ItemData("开封"))
        tags.add(ItemData("杭州"))
        tags.add(ItemData("嘉兴"))
        tags.add(ItemData("兰州"))
        tags.add(ItemData("新疆"))
        tags.add(ItemData("西藏"))
        tags.add(ItemData("昆明"))
        tags.add(ItemData("大理"))
        tags.add(ItemData("桂林"))
        tags.add(ItemData("东莞"))
        tags.add(ItemData("台湾"))
        tags.add(ItemData("香港"))
        tags.add(ItemData("澳门"))
        tags.add(ItemData("宝鸡"))
        tags.add(ItemData("蚌埠"))
        tags.add(ItemData("钓鱼岛"))
        tags.add(ItemData("安康"))
        tags.add(ItemData("苏州"))
        tags.add(ItemData("青岛"))
        tags.add(ItemData("郑州"))
        tags.add(ItemData("洛阳"))
        tags.add(ItemData("石家庄"))
        tags.add(ItemData("乌鲁木齐"))
        tags.add(ItemData("武汉"))
        tags.add(ItemData("←_←"))
        tags.add(ItemData("⊙﹏⊙"))
        tags.add(ItemData("Hello China"))
        tags.add(ItemData("宁波"))
        //方法内缺少拼音解析，找到对应的库或者接口返回即可实现标题的城市列表
        tags.sortByLetter()
        val listTag = tags.map { it.tag }.toList()
        val listTitle = tags.map { it.title }.toList()
        mBindingView.rvList.addItemDecoration(
            GroupHeaderItemDecoration(listTag).setGroupHeaderHeight(
                30
            ).setGroupHeaderLeftPadding(20)
        )
        mBindingView.rvList.addItemDecoration(DivideItemDecoration().setTags(listTag))
        mBindingView.rvList.adapter = mAdapter
        mAdapter.submitList(listTitle)
        mBindingView.sbRight.setOnSideBarTouchListener(listTag, object : OnSideBarTouchListener {
            override fun onTouch(text: String, position: Int) {
                mBindingView.tip.isVisible = true
                mBindingView.tip.text = text
                if (position != -1) {
                    (mBindingView.rvList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        position,
                        0
                    )
                }
            }

            override fun onTouchEnd() {
                mBindingView.tip.isVisible = false
            }
        })
    }
}