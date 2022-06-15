package com.caspar.xl.widget.index.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.caspar.xl.widget.index.decoration.DivideItemDecoration

class DivideItemDecoration : ItemDecoration() {
    //列表数据源的tag集合
    private var tags : List<String>? = null
    private var divideHeight = 1 //分割线高度（px）
    private val mPaint: Paint = Paint()
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        tags?.apply {
            takeIf {
                this.isNotEmpty()
            }?.let {
                val manager = parent.layoutManager
                //只处理线性垂直类型的列表
                if (manager is LinearLayoutManager && LinearLayoutManager.VERTICAL != manager.orientation) {
                    return
                }
                val position = parent.getChildAdapterPosition(view)
                if (this.isNotEmpty() && position + 1 < this.size && this[position] == this[position + 1]) {
                    //当前ItemView的data的tag和下一个ItemView的不相等，则为当前ItemView设置bottom 偏移量
                    outRect[0, 0, 0] = divideHeight
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        tags?.apply {
            takeIf {
                this.isNotEmpty()
            }?.let {
                for (i in 0 until parent.childCount) {
                    val view = parent.getChildAt(i)
                    val position = parent.getChildAdapterPosition(view)
                    //和getItemOffsets()里的条件判断类似
                    if (position + 1 < this.size && this[position] == this[position + 1]) {
                        drawDivide(c, parent, view)
                    }
                }
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }

    private fun drawDivide(c: Canvas, parent: RecyclerView, view: View) {
        val params = view.layoutParams as RecyclerView.LayoutParams
        val left = parent.paddingLeft
        val right = parent.width
        val top = view.bottom + params.bottomMargin
        val bottom = top + divideHeight
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
    }

    fun setTags(tags: List<String>?): DivideItemDecoration {
        this.tags = tags
        return this
    }

    fun setDevideColor(divideColor: Int): DivideItemDecoration {
        mPaint.color = divideColor
        return this
    }

    fun setDevideColor(divideColor: String?): DivideItemDecoration {
        mPaint.color = Color.parseColor(divideColor)
        return this
    }

    fun setDevideHeight(divideHeight: Int): DivideItemDecoration {
        this.divideHeight = divideHeight
        return this
    }

    init {
        mPaint.isAntiAlias = true
        mPaint.color = Color.parseColor("#44333333")
    }
}