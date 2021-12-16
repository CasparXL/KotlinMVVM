package com.caspar.xl.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.Coil
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.caspar.xl.R
import com.caspar.xl.databinding.ItemSelectImageBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import java.io.File

class SelectImageAdapter : BaseQuickAdapter<String, BaseViewBindingHolder<ItemSelectImageBinding>>(R.layout.item_select_image) {

    private val selectImage = mutableListOf<String>()

    fun selectItem(image: String) {
        if (selectImage.contains(image)) {
            selectImage.remove(image)
        } else {
            selectImage.add(image)
        }
        notifyDataSetChanged()
    }

    //如果非要使用ViewBinding，则应该重写onCreateDefViewHolder方法，否则将会导致类型无法强转的Crash
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewBindingHolder<ItemSelectImageBinding> {
        val binding = ItemSelectImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewBindingHolder(binding)
    }

    override fun convert(holder: BaseViewBindingHolder<ItemSelectImageBinding>, item: String) {
        with(holder.viewBinding) {
            ivImageSelectCheck.isChecked = selectImage.contains(item)
            ivImage.load(File(item))
        }
    }

}

