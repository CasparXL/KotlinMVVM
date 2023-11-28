package com.caspar.xl.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.caspar.base.utils.log.dLog
import com.caspar.xl.databinding.ItemRefreshListBinding
import com.chad.library.adapter4.BaseDifferAdapter


/**
 * 我的消息中列表的适配器
 */
class RefreshListAdapter : BaseDifferAdapter<MessageListBean, BaseViewBindingHolder<ItemRefreshListBinding>>(DiffDemoCallback()) {

    class DiffDemoCallback : DiffUtil.ItemCallback<MessageListBean>() {
        /**
         * 判断是否是同一个item
         *
         * @param oldItem New data
         * @param newItem old Data
         * @return
         */
        override fun areItemsTheSame(oldItem: MessageListBean, newItem: MessageListBean): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * 当是同一个item时，再判断内容是否发生改变
         *
         * @param oldItem New data
         * @param newItem old Data
         * @return
         */
        override fun areContentsTheSame(
            oldItem: MessageListBean,
            newItem: MessageListBean
        ): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
                    && oldItem.age == newItem.age
        }

        /**
         * 可选实现
         * 如果需要精确修改某一个view中的内容，请实现此方法。
         * 如果不实现此方法，或者返回null，将会直接刷新整个item。
         *
         * @param oldItem Old data
         * @param newItem New data
         * @return Payload info. if return null, the entire item will be refreshed.
         */
        override fun getChangePayload(
            oldItem: MessageListBean,
            newItem: MessageListBean
        ): Any? {
            return null
        }
    }

    override fun onBindViewHolder(holder: BaseViewBindingHolder<ItemRefreshListBinding>, position: Int, item: MessageListBean?) {
        with(holder.viewBinding) {
            "refresh${holder.bindingAdapterPosition}".dLog()
            item?.apply {
                tvId.text = "Id:${id}"
                tvName.text = "Name:${name}"
                tvAge.text = "Age:${age}"
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): BaseViewBindingHolder<ItemRefreshListBinding> {
        val binding = ItemRefreshListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }
}
data class MessageListBean(val id:Int,val name:String,val age:Int)
