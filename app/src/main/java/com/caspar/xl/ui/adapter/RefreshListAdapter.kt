package com.caspar.xl.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.R
import com.caspar.xl.databinding.ItemRefreshListBinding
import com.chad.library.adapter.base.BaseQuickAdapter


/**
 * 我的消息中列表的适配器
 */
class RefreshListAdapter : BaseQuickAdapter<MessageListBean, BaseViewBindingHolder<ItemRefreshListBinding>>(
    R.layout.item_refresh_list) {

    //如果非要使用ViewBinding，则应该重写onCreateDefViewHolder方法，否则将会导致类型无法强转的Crash
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewBindingHolder<ItemRefreshListBinding> {
        val binding = ItemRefreshListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }

    //类型 0=系统通知,1=天气预警,2=故障告警,3=安装通知
    override fun convert(holder: BaseViewBindingHolder<ItemRefreshListBinding>, item: MessageListBean) {
        with(holder.viewBinding) {
            LogUtil.d("refresh${holder.bindingAdapterPosition}")
            tvId.text = "Id:${item.id}"
            tvName.text = "Name:${item.name}"
            tvAge.text = "Age:${item.age}"
        }
    }
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
}
data class MessageListBean(val id:Int,val name:String,val age:Int)
