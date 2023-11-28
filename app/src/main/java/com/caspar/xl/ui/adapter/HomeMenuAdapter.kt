package com.caspar.xl.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caspar.base.utils.log.dLog
import com.caspar.xl.databinding.ItemMenuBinding
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.dragswipe.listener.DragAndSwipeDataCallback


/**
 *  @Create 2020/6/13.
 *  @Use
 */
class HomeMenuAdapter : BaseQuickAdapter<String, BaseViewBindingHolder<ItemMenuBinding>>(),
    DragAndSwipeDataCallback {
    //如果非要使用ViewBinding，则应该重写onCreateDefViewHolder方法，否则将会导致类型无法强转的Crash

    override fun onBindViewHolder(holder: BaseViewBindingHolder<ItemMenuBinding>, position: Int, item: String?) {
        holder.viewBinding.btnName.text = item
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewBindingHolder<ItemMenuBinding> {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }

    override fun dataMove(fromPosition: Int, toPosition: Int) {
        move(fromPosition, toPosition)
        "swap($fromPosition,${toPosition})".dLog()
    }

    override fun dataRemoveAt(position: Int) {
        removeAt(position)
        "dataRemoveAt($position)".dLog()
    }
}