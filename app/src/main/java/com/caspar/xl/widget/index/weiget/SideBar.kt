package com.caspar.xl.widget.index.weiget

import android.annotation.SuppressLint
import android.content.Context
import kotlin.jvm.JvmOverloads
import android.util.TypedValue
import android.text.TextPaint
import com.caspar.xl.widget.index.listener.OnSideBarTouchListener
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.caspar.xl.R
import com.caspar.xl.widget.index.ext.getTextHeight
import com.caspar.xl.widget.index.ext.getTextWidth

class SideBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var INDEX_COLOR = Color.BLACK //索引字符颜色
    private var TOUCH_COLOR = Color.parseColor("#88999999") //SideBar被触摸时的背景色
    private var UNTOUCH_COLOR = Color.TRANSPARENT //SideBar默认背景色
    private var INDEX_SIZE = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics).toInt()

    //索引字符数组
    var indexArray = arrayOf("↑", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")

    /*  //索引字符数组
    public String[] indexArray = { "#"};*/
    private var tags: List<String> = listOf()
    private var mWidth //字符所在区域宽度
            = 0
    private var mHeight //字符所在区域高度
            = 0f
    private var mMarginTop //顶部间距
            = 0f
    private var lastPos = -1 //记录上次触摸的索引字符pos
    private var mTextPaint: TextPaint = TextPaint()
    private var maxWidth = 0
    private var maxHeight = 0
    private var onSideBarTouchListener: OnSideBarTouchListener? = null
    private fun init(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SideBar, 0, 0)
        for (i in 0 until ta.indexCount) {
            when (val attr = ta.getIndex(i)) {
                R.styleable.SideBar_text_size -> {
                    INDEX_SIZE = ta.getDimensionPixelSize(attr, INDEX_SIZE)
                }
                R.styleable.SideBar_text_color -> {
                    INDEX_COLOR = ta.getColor(attr, INDEX_COLOR)
                }
                R.styleable.SideBar_touch_color -> {
                    TOUCH_COLOR = ta.getColor(attr, TOUCH_COLOR)
                }
                R.styleable.SideBar_untouch_color -> {
                    UNTOUCH_COLOR = ta.getColor(attr, UNTOUCH_COLOR)
                }
            }
        }
        ta.recycle()
        mTextPaint.color = INDEX_COLOR
        mTextPaint.textSize = INDEX_SIZE.toFloat()
        mTextPaint.isAntiAlias = true
        setBackgroundColor(UNTOUCH_COLOR)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h * 1.0f / indexArray.size
        mMarginTop = (h - mHeight * indexArray.size) / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        //重新计算SideBar宽高
        if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.AT_MOST) {
            maxTextSize
            if (heightMode == MeasureSpec.AT_MOST) {
                heightSize = (maxHeight + 15) * indexArray.size
            }
            if (widthMode == MeasureSpec.AT_MOST) {
                widthSize = maxWidth + 10
            }
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in indexArray.indices) {
            val index = indexArray[i]
            val x: Float = (mWidth - mTextPaint.getTextWidth(index)).toFloat() / 2
            val y: Float =
                mMarginTop + mHeight * i + (mHeight + mTextPaint.getTextHeight(index)) / 2
            canvas.drawText(index, x, y, mTextPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // 选中字符的下标
                val pos = ((event.y - mMarginTop) / mHeight).toInt()
                if (pos == lastPos) {
                    return true
                }
                if (pos >= 0 && pos < indexArray.size) {
                    lastPos = pos
                    setBackgroundColor(TOUCH_COLOR)
                    onSideBarTouchListener?.apply {
                        var i = 0
                        while (i < tags.size) {
                            if (indexArray[pos] == tags[i]) {
                                this.onTouch(indexArray[pos], i)
                                break
                            }
                            if (i == tags.size - 1) {
                                this.onTouch(indexArray[pos], -1)
                            }
                            i++
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                lastPos = -1
                setBackgroundColor(UNTOUCH_COLOR)
                onSideBarTouchListener?.onTouchEnd()
            }
        }
        return true
    }

    /**
     * 计算索引字符的最大宽度、高度
     */
    private val maxTextSize: Unit
         get() {
            for (index in indexArray) {
                maxWidth = maxWidth.toFloat().coerceAtLeast(mTextPaint.measureText(index)).toInt()
                maxHeight = maxHeight.coerceAtLeast(mTextPaint.getTextHeight(index))
            }
        }

    fun setOnSideBarTouchListener(tags: List<String>, onSideBarTouchListener: OnSideBarTouchListener?) {
        this.tags = tags
        this.onSideBarTouchListener = onSideBarTouchListener
    }

    init {
        init(context, attrs)
    }
}