package com.caspar.xl.ui.activity

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.dp
import com.caspar.base.ext.setOnClickListener
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityImageLoadBinding
import com.caspar.xl.ext.binding
import com.caspar.xl.helper.loadNetCircle
import com.caspar.xl.helper.loadNetRounded
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageLoadActivity : BaseActivity(), View.OnClickListener {
    private val mBindingView:ActivityImageLoadBinding by binding()
    private val imageUrl: String =
        "https://img2.baidu.com/it/u=3202947311,1179654885&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500"

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvCenter.text = "图片加载"
        setOnClickListener(this, R.id.tv_left, R.id.btn_load)
        mBindingView.etUrl.setText(imageUrl)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_left -> finish()
            R.id.btn_load -> {
                //注意：transformations内可以组合使用，可以即圆型，又灰化。例子： transformations(CircleCropTransformation(),GrayscaleTransformation())
                with(mBindingView) {
                    //加载圆形图
                    ivCircle.loadNetCircle(url = imageUrl, default = R.drawable.image_loading_bg)
                    //加载圆角图
                    ivRounded.loadNetRounded(url = imageUrl, default = R.drawable.image_loading_bg, defaultRound = 15)
                }
            }
        }
    }
}