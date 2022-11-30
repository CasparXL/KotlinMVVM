package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.viewbinding.ViewBinding
import coil.load
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityPaletteBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaletteActivity : BaseActivity(), View.OnClickListener {
    private var bitmap: Bitmap? = null
    private lateinit var mBindingView: ActivityPaletteBinding
    override fun getViewBinding(): ViewBinding {
        return ActivityPaletteBinding.inflate(layoutInflater).apply {
            mBindingView = this
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setOnClickListener(this, R.id.tv_left)
        mBindingView.title.tvCenter.text = "颜色提取器"
        initImageTouchAndLoading()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initImageTouchAndLoading() {
        mBindingView.ivImage.load("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg4.cache.netease.com%2Fphoto%2F0001%2F2009-10-01%2F5KI1QL2T0UQ20001.jpg&refer=http%3A%2F%2Fimg4.cache.netease.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1622197388&t=2f6a650c5908e16381aa3ad1ddf46567")
        mBindingView.ivImage.setOnTouchListener { view, event ->
            //bitmap的长宽
            if (event.y in (0f..(view.height).toFloat()) && event.x in (0f..(view.width).toFloat())) {
                val circleLocalX = event.x + view.left - (mBindingView.ivCircle.width / 2)
                val circleLocalY = event.y + view.top - (mBindingView.ivCircle.height / 2)
                mBindingView.ivCircle.apply {
                    y = circleLocalY
                    x = circleLocalX
                }
                bitmap?.apply {
                    val xsdX = event.x / view.width
                    val xsdY = event.y / view.height
                    LogUtil.e("bit宽高[${this.width},${this.height}],图片宽高[${view.width},${view.height}]")
                    val color: Int =
                        getPixel((xsdX * this.width).toInt(), (xsdY * this.height).toInt())
                    val r: Int = Color.red(color)
                    val g: Int = Color.green(color)
                    val b: Int = Color.blue(color)
                    mBindingView.viewBackground.setBackgroundColor(color)
                    LogUtil.e("颜色->[r->$r,g->$g,b->$b]")
                } ?: run {
                    bitmap = mBindingView.ivImage.drawable.toBitmap(height = view.height)
                        .copy(Bitmap.Config.ARGB_8888, true)
                    LogUtil.e("创建了一个bitmap")
                }
                LogUtil.e("移动位置->[${event.x},${event.y}]")
            }
            true
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_left -> {
                finish()
            }
        }
    }
}