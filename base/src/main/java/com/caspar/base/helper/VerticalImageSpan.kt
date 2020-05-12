package com.caspar.base.helper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

class VerticalImageSpan : ImageSpan {
    constructor(drawable: Drawable?) : super(drawable!!) {}
    constructor(b: Bitmap?) : super(b!!) {}

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val b = drawable
        val fm = paint.fontMetricsInt
        val transY = (y + fm.descent + y + fm.ascent) / 2 - b.bounds.bottom / 2 //计算y方向的位移
        canvas.save()
        canvas.translate(x, transY.toFloat()) //绘制图片位移一段距离
        b.draw(canvas)
        canvas.restore()
    }
}