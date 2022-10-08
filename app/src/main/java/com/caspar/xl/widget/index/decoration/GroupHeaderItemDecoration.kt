package com.caspar.xl.widget.index.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.caspar.base.ext.dp
import com.caspar.base.ext.sp
import com.caspar.xl.widget.index.ext.getTextHeight
import com.caspar.xl.widget.index.ext.getTextWidth
import com.caspar.xl.widget.index.listener.OnDrawItemDecorationListener

class GroupHeaderItemDecoration(var tags: List<String>? = null) : ItemDecoration() {

    //GroupHeader高度
    private var groupHeaderHeight: Int

    //GroupHeader的左padding
    private var groupHeaderLeftPadding = 0
    private var show = true //是否显示顶部悬浮的GroupHeader
    private val mPaint: Paint
    private val mTextPaint: TextPaint
    private var drawItemDecorationListener: OnDrawItemDecorationListener? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State, ) {
        super.getItemOffsets(outRect, view, parent, state)
        tags?.apply {
            takeIf { this.isNotEmpty() }?.let {
                val manager = parent.layoutManager
                //只处理线性垂直类型的列表
                if (manager is LinearLayoutManager && LinearLayoutManager.VERTICAL != manager.orientation) {
                    return
                }
                val position = parent.getChildAdapterPosition(view)
                //ItemView的position==0 或者 当前itemView的data的tag和上一个ItemView的不相等，则为当前itemView设置top 偏移量
                if (position == 0 || this[position] != this[position - 1]) {
                    outRect[0, groupHeaderHeight, 0] = 0
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        tags?.apply {
            takeIf { this.isNotEmpty() }?.let {
                for (i in 0 until parent.childCount) {
                    val view = parent.getChildAt(i)
                    val position = parent.getChildAdapterPosition(view)
                    val tag = this[position]
                    //和getItemOffsets()里的条件判断类似，开始绘制分组的GroupHeader
                    if (position == 0 || tag != this[position - 1]) {
                        drawItemDecorationListener?.onDrawGroupHeader(c, mPaint, mTextPaint, getGroupHeaderCoordinate(parent, view), position) ?: drawGroupHeader(c, parent, view, tag)
                    }
                }
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        tags?.apply {
            takeIf { this.isNotEmpty() }?.let {
                if (!show) {
                    return
                }
                //列表第一个可见的ItemView位置
                val position = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                //判断是我自己加上去的，以后有问题删掉
                if (position >= 0) {
                    val tag = this[position]
                    //第一个可见的ItemView
                    val view = parent.findViewHolderForAdapterPosition(position)!!.itemView
                    //当前ItemView的data的tag和下一个ItemView的不相等，则代表将要重新绘制悬停的GroupHeader
                    var flag = false
                    if (position + 1 < this.size && tag != this[position + 1]) {
                        //如果第一个可见ItemView的底部坐标小于groupHeaderHeight，则执行Canvas垂直位移操作
                        if (view.bottom <= groupHeaderHeight) {
                            c.save()
                            flag = true
                            c.translate(0f, (view.height + view.top - groupHeaderHeight).toFloat())
                        }
                    }
                    drawItemDecorationListener?.onDrawSuspensionGroupHeader(c, mPaint, mTextPaint, getSuspensionGroupHeaderCoordinate(parent), position) ?: drawSuspensionGroupHeader(c, parent, tag)
                    if (flag) {
                        c.restore()
                    }
                }
            }
        }
    }

    fun getGroupHeaderCoordinate(parent: RecyclerView, view: View): IntArray {
        val params = view.layoutParams as RecyclerView.LayoutParams
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val bottom = view.top - params.topMargin
        val top = bottom - groupHeaderHeight
        return intArrayOf(left, top, right, bottom)
    }

    private fun drawGroupHeader(c: Canvas, parent: RecyclerView, view: View, tag: String) {
        val params = getGroupHeaderCoordinate(parent, view)
        c.drawRect(params[0].toFloat(), params[1].toFloat(), params[2].toFloat(), params[3].toFloat(), mPaint)
        val x = params[0] + groupHeaderLeftPadding
        val y: Int = params[1] + (groupHeaderHeight + mTextPaint.getTextHeight(tag)) / 2
        c.drawText(tag, x.toFloat(), y.toFloat(), mTextPaint)
    }

    fun getSuspensionGroupHeaderCoordinate(parent: RecyclerView): IntArray {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val bottom = groupHeaderHeight
        val top = 0
        return intArrayOf(left, top, right, bottom)
    }

    private fun drawSuspensionGroupHeader(c: Canvas, parent: RecyclerView, tag: String) {
        val params = getSuspensionGroupHeaderCoordinate(parent)
        c.drawRect(params[0].toFloat(), params[1].toFloat(), params[2].toFloat(), params[3]
            .toFloat(), mPaint)
        val x = params[0] + groupHeaderLeftPadding
        val y: Int = params[1] + (groupHeaderHeight + mTextPaint.getTextWidth(tag)) / 2
        c.drawText(tag, x.toFloat(), y.toFloat(), mTextPaint)
    }

    fun setGroupHeaderLeftPadding(groupHeaderLeftPadding: Int): GroupHeaderItemDecoration {
        this.groupHeaderLeftPadding = groupHeaderLeftPadding.dp
        return this
    }

    fun setGroupHeaderTextColor(groupHeaderTextColor: Int): GroupHeaderItemDecoration {
        mTextPaint.color = groupHeaderTextColor
        return this
    }

    fun setGroupHeaderTextColor(groupHeaderTextColor: String?): GroupHeaderItemDecoration {
        mTextPaint.color = Color.parseColor(groupHeaderTextColor)
        return this
    }

    fun setGroupHeaderTextSize(groupHeaderTextSize: Int): GroupHeaderItemDecoration {
        mTextPaint.textSize = groupHeaderTextSize.toFloat()
        return this
    }

    fun setGroupHeaderColor(groupHeaderColor: Int): GroupHeaderItemDecoration {
        mPaint.color = groupHeaderColor
        return this
    }

    fun setGroupHeaderColor(groupHeaderColor: String?): GroupHeaderItemDecoration {
        mPaint.color = Color.parseColor(groupHeaderColor)
        return this
    }

    fun setGroupHeaderHeight(groupHeaderHeight: Int): GroupHeaderItemDecoration {
        this.groupHeaderHeight = groupHeaderHeight.dp
        return this
    }

    fun setTags(tags: List<String>?): GroupHeaderItemDecoration {
        this.tags = tags
        return this
    }

    fun showSuspensionGroupHeader(show: Boolean): GroupHeaderItemDecoration {
        this.show = show
        return this
    }

    fun setOnDrawItemDecorationListener(drawItemDecorationListener: OnDrawItemDecorationListener?): GroupHeaderItemDecoration {
        this.drawItemDecorationListener = drawItemDecorationListener
        return this
    }

    init {
        groupHeaderHeight = 20.dp
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = Color.parseColor("#FFEEEEEE")
        //mPaint.setColor(Color.WHITE);
        mTextPaint = TextPaint()
        mTextPaint.isAntiAlias = true
        mTextPaint.isFakeBoldText = true
        mTextPaint.color = Color.parseColor("#FF999999")
        // mTextPaint.setColor(Color.BLACK);
        mTextPaint.textSize = 14.sp.toFloat()
    }
}