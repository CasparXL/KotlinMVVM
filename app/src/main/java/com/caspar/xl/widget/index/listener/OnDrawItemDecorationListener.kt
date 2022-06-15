package com.caspar.xl.widget.index.listener

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint

interface OnDrawItemDecorationListener {
    /**
     * 自定义的 GroupHeader 绘制接口
     *
     * @param c
     * @param paint
     * @param textPaint
     * @param params    共四个值left、top、right、bottom 代表GroupHeader所在区域的四个坐标值
     * @param position  原始数据源中的position
     */
    fun onDrawGroupHeader(c: Canvas?, paint: Paint?, textPaint: TextPaint?, params: IntArray?, position: Int)

    fun onDrawSuspensionGroupHeader(c: Canvas?, paint: Paint?, textPaint: TextPaint?, params: IntArray?, position: Int)
}