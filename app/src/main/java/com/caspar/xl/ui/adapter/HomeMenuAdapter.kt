package com.caspar.xl.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.caspar.xl.R
import com.caspar.xl.databinding.ItemMenuBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule


/**
 *  @Create 2020/6/13.
 *  @Use
 */
class HomeMenuAdapter : BaseQuickAdapter<String, BaseViewBindingHolder<ItemMenuBinding>>(R.layout.item_menu), DraggableModule {
    //如果非要使用ViewBinding，则应该重写onCreateDefViewHolder方法，否则将会导致类型无法强转的Crash
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewBindingHolder<ItemMenuBinding> {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }

    override fun convert(holder: BaseViewBindingHolder<ItemMenuBinding>, item: String) {
        holder.viewBinding.btnName.text = item
    }
}