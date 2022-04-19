package com.caspar.xl.widget.captcha

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import android.view.MotionEvent
import kotlin.math.abs

/**
 * 拼图区域控件
 * Created by luozhanming on 2018/1/17.
 */
class PictureVerifyView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatImageView(
        context!!, attrs, defStyleAttr) {
    private var mState = STATE_IDEL //当前状态
    private var shadowInfo //拼图缺块阴影的位置
            : PositionInfo? = null
    private var blockInfo //拼图缺块的位置
            : PositionInfo? = null
    private var verfityBlock //拼图缺块Bitmap
            : Bitmap? = null
    private var blockShape //拼图缺块形状
            : Path? = null
    private val bitmapPaint //绘制拼图缺块的画笔
            : Paint
    private val shadowPaint //绘制拼图缺块阴影的画笔
            : Paint
    private var startTouchTime //滑动/触动开始时间
            : Long = 0
    private var looseTime //滑动/触动松开时间
            : Long = 0
    private var blockSize = 50
    private var mTouchEnable = true //是否可触动
    private var callback: Callback? = null
    private var mStrategy: CaptchaStrategy
    private var mMode //Captcha验证模式
            = 0

    interface Callback {
        fun onSuccess(time: Long)
        fun onFailed()
    }

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null) : this(context, attrs, 0) {
    }

    private fun initDrawElements() {
        if (shadowInfo == null) {
            shadowInfo = mStrategy.getBlockPostionInfo(width, height, blockSize)
            blockInfo = if (mMode == Captcha.MODE_BAR) {
                PositionInfo(0, shadowInfo!!.top)
            } else {
                mStrategy.getPositionInfoForSwipeBlock(width, height, blockSize)
            }
        }
        if (blockShape == null) {
            blockShape = mStrategy.getBlockShape(blockSize)
            blockShape!!.offset(shadowInfo!!.left.toFloat(), shadowInfo!!.top.toFloat())
        }
        if (verfityBlock == null) {
            verfityBlock = createBlockBitmap()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initDrawElements()
        if (mState != STATE_ACCESS) {
            canvas.drawPath(blockShape!!, shadowPaint)
        }
        if (mState == STATE_MOVE || mState == STATE_IDEL || mState == STATE_DOWN || mState == STATE_UNACCESS) {
            canvas.drawBitmap(verfityBlock!!,
                blockInfo!!.left.toFloat(),
                blockInfo!!.top.toFloat(),
                bitmapPaint)
        }
    }

    /**
     * 按下滑动条(滑动条模式)
     */
    fun down(progress: Int) {
        startTouchTime = System.currentTimeMillis()
        mState = STATE_DOWN
        blockInfo!!.left = (progress / 100f * (width - blockSize)).toInt()
        invalidate()
    }

    /**
     * 触动拼图块(触动模式)
     */
    fun downByTouch(x: Float, y: Float) {
        mState = STATE_DOWN
        blockInfo!!.left = (x - blockSize / 2f).toInt()
        blockInfo!!.top = (y - blockSize / 2f).toInt()
        startTouchTime = System.currentTimeMillis()
        invalidate()
    }

    /**
     * 移动拼图缺块(滑动条模式)
     */
    fun move(progress: Int) {
        mState = STATE_MOVE
        blockInfo!!.left = (progress / 100f * (width - blockSize)).toInt()
        invalidate()
    }

    /**
     * 触动拼图缺块(触动模式)
     */
    fun moveByTouch(offsetX: Float, offsetY: Float) {
        mState = STATE_MOVE
        blockInfo!!.left += offsetX.toInt()
        blockInfo!!.top += offsetY.toInt()
        invalidate()
    }

    /**
     * 松开
     */
    fun loose() {
        mState = STATE_LOOSEN
        looseTime = System.currentTimeMillis()
        checkAccess()
        invalidate()
    }

    /**
     * 复位
     */
    fun reset() {
        mState = STATE_IDEL
        verfityBlock = null
        shadowInfo = null
        blockShape = null
        invalidate()
    }

    fun unAccess() {
        mState = STATE_UNACCESS
        invalidate()
    }

    fun access() {
        mState = STATE_ACCESS
        invalidate()
    }

    fun callback(callback: Callback?) {
        this.callback = callback
    }

    fun setCaptchaStrategy(strategy: CaptchaStrategy) {
        mStrategy = strategy
    }

    fun setBlockSize(size: Int) {
        blockSize = size
        blockShape = null
        blockInfo = null
        shadowInfo = null
        verfityBlock = null
        invalidate()
    }

    fun setBitmap(bitmap: Bitmap?) {
        blockShape = null
        blockInfo = null
        shadowInfo = null
        verfityBlock!!.recycle()
        verfityBlock = null
        setImageBitmap(bitmap)
    }

    fun setMode(@Captcha.Mode mode: Int) {
        mMode = mode
        blockShape = null
        blockInfo = null
        shadowInfo = null
        verfityBlock = null
        invalidate()
    }

    fun setTouchEnable(enable: Boolean) {
        mTouchEnable = enable
    }

    /**
     * 生成拼图缺块的Bitmap
     */
    private fun createBlockBitmap(): Bitmap? {
        val tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(tempBitmap)
        drawable.setBounds(0, 0, width, height)
        canvas.clipPath(blockShape!!)
        drawable.draw(canvas)
        mStrategy.decorateSwipeBlockBitmap(canvas, blockShape)
        return cropBitmap(tempBitmap)
    }

    /**
     * 保留拼图缺块大小的bitmap
     */
    private fun cropBitmap(bmp: Bitmap): Bitmap? {
        var result: Bitmap? = null
        result = Bitmap.createBitmap(bmp, shadowInfo!!.left, shadowInfo!!.top, blockSize, blockSize)
        bmp.recycle()
        return result
    }

    /**
     * 检测是否通过
     */
    private fun checkAccess() {
        if (abs(blockInfo!!.left - shadowInfo!!.left) < TOLERANCE && abs(blockInfo!!.top - shadowInfo!!.top) < TOLERANCE) {
            access()
            if (callback != null) {
                val deltaTime = looseTime - startTouchTime
                callback?.onSuccess(deltaTime)
            }
        } else {
            unAccess()
            if (callback != null) {
                callback?.onFailed()
            }
        }
    }

    private var tempX = 0f
    private var tempY = 0f
    private var downX = 0f
    private var downY = 0f
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        //触动模式下，点击超出拼图缺块的区域不进行处理
        if (event.action == MotionEvent.ACTION_DOWN && mMode == Captcha.MODE_NONBAR) {
            if (event.x < blockInfo!!.left || event.x > blockInfo!!.left + blockSize || event.y < blockInfo!!.top || event.y > blockInfo!!.top + blockSize) {
                return false
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mMode == Captcha.MODE_NONBAR && verfityBlock != null && mTouchEnable) {
            val x = event.x
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = x
                    downY = y
                    downByTouch(x, y)
                }
                MotionEvent.ACTION_UP -> loose()
                MotionEvent.ACTION_MOVE -> {
                    val offsetX = x - tempX
                    val offsetY = y - tempY
                    moveByTouch(offsetX, offsetY)
                }
            }
            tempX = x
            tempY = y
        }
        return true
    }

    companion object {
        //状态码
        private const val STATE_DOWN = 1
        private const val STATE_MOVE = 2
        private const val STATE_LOOSEN = 3
        private const val STATE_IDEL = 4
        private const val STATE_ACCESS = 5
        private const val STATE_UNACCESS = 6
        private const val TOLERANCE = 10 //验证的最大容差
    }

    init {
        mStrategy = DefaultCaptchaStrategy(context!!)
        shadowPaint = mStrategy.blockShadowPaint!!
        bitmapPaint = mStrategy.blockBitmapPaint!!
        setLayerType(LAYER_TYPE_SOFTWARE, bitmapPaint)
    }
}