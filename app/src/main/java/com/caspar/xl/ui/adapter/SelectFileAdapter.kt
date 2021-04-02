package com.caspar.xl.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.caspar.xl.R
import com.caspar.xl.databinding.ItemFilesBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import java.io.File

class SelectFileAdapter: BaseQuickAdapter<FileBean, BaseViewBindingHolder<ItemFilesBinding>>(R.layout.item_files) {

    //如果非要使用ViewBinding，则应该重写onCreateDefViewHolder方法，否则将会导致类型无法强转的Crash
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewBindingHolder<ItemFilesBinding> {
        val binding = ItemFilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }

    override fun convert(holder: BaseViewBindingHolder<ItemFilesBinding>, item: FileBean) {
        holder.viewBinding.tvName.text = item.name
        when {
            holder.adapterPosition == 0 -> {
                holder.viewBinding.ivFileType.setImageResource(R.drawable.ic_left)
            }
            item.file.isDirectory -> {
                holder.viewBinding.ivFileType.setImageResource(R.drawable.ic_folder)
            }
            else -> {
                holder.viewBinding.ivFileType.setImageResource(R.drawable.ic_file)
            }
        }
    }
}

class FileBean(val name: String, val file: File)