package com.caspar.xl.ui.adapter

import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * 方便 ViewBinding 的使用
 *
 * @param BD : ViewDataBinding
 * @property viewBinding BD?
 * @constructor
 */
open class BaseViewBindingHolder<BD : ViewBinding>(view: BD) : BaseViewHolder(view.root) {
    val viewBinding = view
}