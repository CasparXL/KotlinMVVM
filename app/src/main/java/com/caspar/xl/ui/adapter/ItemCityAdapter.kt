package com.caspar.xl.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.caspar.xl.R
import com.caspar.xl.databinding.ItemCityBinding
import com.chad.library.adapter4.BaseQuickAdapter

/**
 * 单个条目剧中适配器
 */
class ItemCityAdapter : BaseQuickAdapter<String, BaseViewBindingHolder<ItemCityBinding>>() {
    //如果非要使用ViewBinding，则应该重写onCreateDefViewHolder方法，否则将会导致类型无法强转的Crash
    override fun onBindViewHolder(
        holder: BaseViewBindingHolder<ItemCityBinding>,
        position: Int,
        item: String?
    ) {
        with(holder.viewBinding){
            title.text = item
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewBindingHolder<ItemCityBinding> {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }
}