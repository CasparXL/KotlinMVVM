package com.caspar.xl.widget.captcha

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.caspar.base.ext.dp
import com.caspar.xl.R

/**
 * Created by luozhanming on 2018/1/17.
 */
class TextSeekbar : AppCompatSeekBar {
    private var textPaint: Paint? = null
    private var mMode = 0 //Captcha验证模式
    private var mTextSize = 0 //字体大小
    private var mTextColor = 0 //字体颜色

    constructor(context: Context) : super(context)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextSeekbar)
        mMode = typedArray.getInteger(R.styleable.TextSeekbar_text_mode, MODE_NO_TEXT)
        mTextColor = typedArray.getColor(R.styleable.TextSeekbar_text_color, ContextCompat.getColor(context, com.caspar.base.R.color.appColor))
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.TextSeekbar_text_size, 14.dp)
        typedArray.recycle()

        textPaint = Paint()
        textPaint?.apply {
            textAlign = Paint.Align.CENTER
            textSize = mTextSize.toFloat()
            isAntiAlias = true
            color = mTextColor
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mMode == MODE_TEXT){
            val fontMetrics = textPaint?.fontMetrics
            fontMetrics?.apply {
                val top = this.top //为基线到字体上边框的距离,即上图中的top
                val bottom = this.bottom //为基线到字体下边框的距离,即上图中的bottom
                val baseLineY = height / 2 - top / 2 - bottom / 2 //基线中间点的y轴计算公式
                canvas.drawText("向右滑动滑块完成拼图", (width / 2).toFloat(), baseLineY, textPaint!!)
            }
        }
    }
    companion object {
        /**
         * 无文字
         */
        const val MODE_NO_TEXT = 1

        /**
         * 无文字
         */
        const val MODE_TEXT = 2
    }
}