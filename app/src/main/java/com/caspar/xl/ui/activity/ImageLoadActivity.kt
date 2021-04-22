package com.caspar.xl.ui.activity

import android.os.Bundle
import android.view.View
import coil.Coil
import coil.clear
import coil.load
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import coil.transform.GrayscaleTransformation
import coil.transform.RoundedCornersTransformation
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.dp
import com.caspar.base.ext.setOnClickListener
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityImageLoadBinding

class ImageLoadActivity : BaseActivity<ActivityImageLoadBinding>(), View.OnClickListener {
    private val imageUrl: String =
        "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2496571732,442429806&fm=26&gp=0.jpg"

    override fun initIntent() {

    }

    override fun initView(savedInstanceState: Bundle?) {
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
                    ivBlur.load(imageUrl) {
                        transformations(BlurTransformation(this@ImageLoadActivity))
                    }
                    //加载圆形图
                    ivCircle.load(imageUrl) {
                        transformations(CircleCropTransformation())
                    }
                    //加载灰色图片
                    ivGrayscale.load(imageUrl) {
                        transformations(GrayscaleTransformation())
                    }
                    //加载圆角图
                    ivRounded.load(imageUrl) {
                        transformations(
                            RoundedCornersTransformation(
                                topLeft = 15f.dp,
                                topRight = 15f.dp,
                                bottomLeft = 15f.dp,
                                bottomRight = 15f.dp
                            )
                        )
                    }
                }
            }
        }
    }
}