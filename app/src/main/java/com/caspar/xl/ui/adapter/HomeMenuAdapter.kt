package com.caspar.xl.ui.adapter

import com.caspar.xl.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *  @Create 2020/6/13.
 *  @Use
 */
class HomeMenuAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_menu) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.btn_name, item)
    }
}