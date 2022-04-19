package com.caspar.xl.widget.captcha

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.caspar.xl.widget.captcha.PositionInfo

/**
 * Captcha的拼图区域策咯
 * Created by luozhanming on 2018/1/19.
 */
abstract class CaptchaStrategy(protected var context: Context) {

    /**
     * 定义缺块的形状
     *
     * @param blockSize 单位dp，注意转化为px
     * @return path of the shape
     */
    abstract fun getBlockShape(blockSize: Int): Path?

    /**
     * 定义缺块的位置信息
     *
     * @param width  picture width unit:px
     * @param height picture height unit:px
     * @param blockSize
     * @return position info of the block
     */
    abstract fun getBlockPostionInfo(width: Int, height: Int, blockSize: Int): PositionInfo?

    /**
     * 定义滑块图片的位置信息(只有设置为无滑动条模式有用)
     *
     * @param width  picture width
     * @param height picture height
     * @return position info of the block
     */
    open fun getPositionInfoForSwipeBlock(width: Int, height: Int, blockSize: Int): PositionInfo? {
        return getBlockPostionInfo(width, height, blockSize)
    }

    /**
     * 获得缺块阴影的Paint
     */
    abstract val blockShadowPaint: Paint?

    /**
     * 获得滑块图片的Paint
     */
    abstract val blockBitmapPaint: Paint?

    /**
     * 装饰滑块图片，在绘制图片后执行，即绘制滑块前景
     */
    open fun decorateSwipeBlockBitmap(canvas: Canvas?, shape: Path?) {}
}