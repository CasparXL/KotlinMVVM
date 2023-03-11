package com.caspar.xl.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caspar.xl.databinding.ItemSelectImageBinding
import com.caspar.xl.helper.loadFile
import com.chad.library.adapter.base.BaseQuickAdapter
import java.io.File

class SelectImageAdapter : BaseQuickAdapter<String, BaseViewBindingHolder<ItemSelectImageBinding>>() {

    private val selectImage = mutableListOf<String>()

    fun selectItem(image: String) {
        if (selectImage.contains(image)) {
            selectImage.remove(image)
        } else {
            selectImage.add(image)
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: BaseViewBindingHolder<ItemSelectImageBinding>,
        position: Int,
        item: String?
    ) {
        with(holder.viewBinding) {
            ivImageSelectCheck.isChecked = selectImage.contains(item)
            ivImage.loadFile(File(item))
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewBindingHolder<ItemSelectImageBinding> {
        val binding = ItemSelectImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }

}

