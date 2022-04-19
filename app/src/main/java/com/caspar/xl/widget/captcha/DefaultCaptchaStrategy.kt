package com.caspar.xl.widget.captcha

import android.content.Context
import android.graphics.*
import com.caspar.xl.widget.captcha.CaptchaStrategy
import com.caspar.xl.widget.captcha.PositionInfo
import java.util.*

/**
 * 默认CaptchaStrategy
 * Created by luozhanming on 2018/1/19.
 */
class DefaultCaptchaStrategy(ctx: Context) : CaptchaStrategy(ctx) {
    override fun getBlockShape(blockSize: Int): Path {
        val gap = (blockSize / 5f).toInt()
        val path = Path()
        path.moveTo(0f, gap.toFloat())
        path.rLineTo(blockSize / 2.5f, 0f)
        path.rLineTo(0f, -gap.toFloat())
        path.rLineTo(gap.toFloat(), 0f)
        path.rLineTo(0f, gap.toFloat())
        path.rLineTo((2 * gap).toFloat(), 0f)
        path.rLineTo(0f, (4 * gap).toFloat())
        path.rLineTo((-5 * gap).toFloat(), 0f)
        path.rLineTo(0f, -1.5f * gap)
        path.rLineTo(gap.toFloat(), 0f)
        path.rLineTo(0f, -gap.toFloat())
        path.rLineTo(-gap.toFloat(), 0f)
        path.close()
        return path
    }

    override fun getBlockPostionInfo(width: Int, height: Int, blockSize: Int): PositionInfo {
        val random = Random()
        var left = random.nextInt(width - blockSize + 1)
        //Avoid robot frequently and quickly click the start point to access the captcha.
        if (left < blockSize) {
            left = blockSize
        }
        var top = random.nextInt(height - blockSize + 1)
        if (top < 0) {
            top = 0
        }
        return PositionInfo(left, top)
    }

    override fun getPositionInfoForSwipeBlock(
        width: Int,
        height: Int,
        blockSize: Int
    ): PositionInfo {
        val random = Random()
        val left = random.nextInt(width - blockSize + 1)
        var top = random.nextInt(height - blockSize + 1)
        if (top < 0) {
            top = 0
        }
        return PositionInfo(left, top)
    }

    override val blockShadowPaint: Paint
        get() {
            val shadowPaint = Paint()
            shadowPaint.color = Color.parseColor("#000000")
            shadowPaint.alpha = 165
            return shadowPaint
        }
    override val blockBitmapPaint: Paint
        get() = Paint()

    override fun decorateSwipeBlockBitmap(canvas: Canvas?, shape: Path?) {
        val paint = Paint()
        paint.color = Color.parseColor("#FFFFFF")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 10F)
        val path = Path(shape)
        canvas!!.drawPath(path, paint)
    }
}