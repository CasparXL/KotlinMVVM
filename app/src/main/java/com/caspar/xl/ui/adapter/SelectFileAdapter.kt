package com.caspar.xl.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caspar.xl.R
import com.caspar.xl.databinding.ItemFilesBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import java.io.File

class SelectFileAdapter: BaseQuickAdapter<FileBean, BaseViewBindingHolder<ItemFilesBinding>>() {

    override fun onBindViewHolder(holder: BaseViewBindingHolder<ItemFilesBinding>, position: Int, item: FileBean?) {
        item?.apply {
            holder.viewBinding.tvName.text = item.name
            when {
                holder.absoluteAdapterPosition == 0 -> {
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

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewBindingHolder<ItemFilesBinding> {
        val binding = ItemFilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }
}

class FileBean(val name: String, val file: File)