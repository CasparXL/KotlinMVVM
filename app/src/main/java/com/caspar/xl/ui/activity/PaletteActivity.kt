package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.utils.log.dLog
import com.caspar.base.utils.log.iLog
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityPaletteBinding
import com.caspar.xl.ext.binding
import com.caspar.xl.helper.loadUrl
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaletteActivity : BaseActivity(), View.OnClickListener {
    private var bitmap: Bitmap? = null
    private val mBindingView: ActivityPaletteBinding by binding()

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvCenter.text = "颜色提取器"
        setOnClickListener(this, R.id.tv_left)
        initImageTouchAndLoading()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initImageTouchAndLoading() {
        mBindingView.ivImage.loadUrl("https://img2.baidu.com/it/u=3202947311,1179654885&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500")
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
                    "bit宽高[${this.width},${this.height}],图片宽高[${view.width},${view.height}]".dLog()
                    val color: Int =
                        getPixel((xsdX * this.width).toInt(), (xsdY * this.height).toInt())
                    val r: Int = Color.red(color)
                    val g: Int = Color.green(color)
                    val b: Int = Color.blue(color)
                    mBindingView.viewBackground.setBackgroundColor(color)
                    "颜色->[r->$r,g->$g,b->$b]".dLog()
                } ?: run {
                    bitmap = mBindingView.ivImage.drawable.toBitmap(height = view.height)
                        .copy(Bitmap.Config.ARGB_8888, true)
                    "创建了一个bitmap".dLog()
                }
                "移动位置->[${event.x},${event.y}]".iLog()
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