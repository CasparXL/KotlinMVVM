package com.caspar.xl.ui.adapter

import com.caspar.xl.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.io.File

class SelectFileAdapter: BaseQuickAdapter<FileBean, BaseViewHolder>(R.layout.item_files) {
    override fun convert(holder: BaseViewHolder, item: FileBean) {
        holder.setText(R.id.tv_name,item.name)
        when {
            holder.adapterPosition == 0 -> {
                holder.setImageResource(R.id.iv_file_type,R.drawable.ic_left)
            }
            item.file.isDirectory -> {
                holder.setImageResource(R.id.iv_file_type,R.drawable.ic_folder)
            }
            else -> {
                holder.setImageResource(R.id.iv_file_type,R.drawable.ic_file)
            }
        }
    }
}

class FileBean(val name: String, val file: File)