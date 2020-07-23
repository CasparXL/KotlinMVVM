package com.caspar.xl.utils.decoration

import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.entity.SectionEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.util.*

/**
 * 应用于RecyclerView的GridLayoutManager，水平方向上固定间距大小，从而使条目宽度自适应。<br></br>
 * 配合Brvah的Section使用，不对Head生效，仅对每个Head的子Grid列表生效<br></br>
 * Section Grid中Item的宽度应设为MATCH_PARAENT
 *
 * @param gapHorizontalDp       item之间的水平间距
 * @param gapVerticalDp         item之间的垂直间距
 * @param sectionEdgeHPaddingDp section左右两端的padding大小
 * @param sectionEdgeVPaddingDp section上下两端的padding大小
 */
class GridSectionAverageGapItemDecoration(private val gapHorizontalDp: Float, private val gapVerticalDp: Float, private val sectionEdgeHPaddingDp: Float, private val sectionEdgeVPaddingDp: Float) : ItemDecoration() {
    private inner class Section {
        var startPos = 0
        var endPos = 0
        val count: Int
            get() = endPos - startPos + 1

        operator fun contains(pos: Int): Boolean {
            return pos >= startPos && pos <= endPos
        }

        override fun toString(): String {
            return "Section{" + "startPos=" + startPos + ", endPos=" + endPos + '}'
        }
    }

    private var gapHSizePx = -1
    private var gapVSizePx = -1
    private var sectionEdgeHPaddingPx = 0
    private var eachItemHPaddingPx = 0 //每个条目应该在水平方向上加的padding 总大小，即=paddingLeft+paddingRight
    private var sectionEdgeVPaddingPx = 0
    private val mSectionList: MutableList<Section?> = ArrayList()
    private var mAdapter: BaseSectionQuickAdapter<*, *>? = null
    private val mDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            markSections()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            markSections()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            markSections()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            markSections()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            markSections()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            markSections()
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager is GridLayoutManager && parent.adapter is BaseSectionQuickAdapter<*, *>) {
            val layoutManager = parent.layoutManager as GridLayoutManager?
            val adapter: BaseSectionQuickAdapter<SectionEntity, BaseViewHolder> = parent.adapter as BaseSectionQuickAdapter<SectionEntity, BaseViewHolder>
            if (mAdapter !== adapter) {
                setUpWithAdapter(adapter)
            }
            val spanCount = layoutManager!!.spanCount
            val position = parent.getChildAdapterPosition(view) - mAdapter!!.headerLayoutCount
            val entity = adapter.getItem(position)
            if (entity.isHeader) {
                //不处理header
                outRect[0, 0, 0] = 0
                //                Log.w("GridAverageGapItem", "pos=" + position + "," + outRect.toShortString());
                return
            }
            val section = findSectionLastItemPos(position)
            if (gapHSizePx < 0 || gapVSizePx < 0) {
                transformGapDefinition(parent, spanCount)
            }
            outRect.top = gapVSizePx
            outRect.bottom = 0
            //下面的visualPos为单个Section内的视觉Pos
            val visualPos = position + 1 - section!!.startPos
            if (visualPos % spanCount == 1) {
                //第一列
                outRect.left = sectionEdgeHPaddingPx
                outRect.right = eachItemHPaddingPx - sectionEdgeHPaddingPx
            } else if (visualPos % spanCount == 0) {
                //最后一列
                outRect.left = eachItemHPaddingPx - sectionEdgeHPaddingPx
                outRect.right = sectionEdgeHPaddingPx
            } else {
                outRect.left = gapHSizePx.minus((eachItemHPaddingPx - sectionEdgeHPaddingPx))
                outRect.right = eachItemHPaddingPx - outRect.left
            }
            if (visualPos - spanCount <= 0) {
                //第一行
                outRect.top = sectionEdgeVPaddingPx
            }
            if (isLastRow(visualPos, spanCount, section.count)) {
                //最后一行
                outRect.bottom = sectionEdgeVPaddingPx
                //                Log.w("GridAverageGapItem", "last row pos=" + position);
            }
            //            Log.w("GridAverageGapItem", "pos=" + position + ",vPos=" + visualPos + "," + outRect.toShortString());
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }

    private fun setUpWithAdapter(adapter: BaseSectionQuickAdapter<*, *>?) {
        mAdapter?.unregisterAdapterDataObserver(mDataObserver)
        mAdapter = adapter
        mAdapter?.registerAdapterDataObserver(mDataObserver)
        markSections()
    }

    private fun markSections() {
        if (mAdapter != null) {
            val adapter = mAdapter
            mSectionList.clear()
            var sectionEntity: SectionEntity
            var section: Section = Section()
            var i = 0
            val size = adapter?.itemCount
            size?.apply {
                while (i < size) {
                    sectionEntity = adapter.getItem(i)
                    if (sectionEntity.isHeader) {
                        //找到新Section起点
                        if (i != 0) {
                            //已经有待添加的section
                            section.endPos = i - 1
                            mSectionList.add(section)
                        }
                        section = Section()
                        section.startPos = i + 1
                    } else {
                        section.endPos = i
                    }
                    i++
                }
            }
            //处理末尾情况
            if (!mSectionList.contains(section)) {
                mSectionList.add(section)
            }
            //            Log.w("GridAverageGapItem", "section list=" + mSectionList);
        }
    }

    private fun transformGapDefinition(parent: RecyclerView, spanCount: Int) {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            parent.display.getMetrics(displayMetrics)
        }
        gapHSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, gapHorizontalDp, displayMetrics
        ).toInt()
        gapVSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, gapVerticalDp, displayMetrics
        ).toInt()
        sectionEdgeHPaddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sectionEdgeHPaddingDp, displayMetrics
        ).toInt()
        sectionEdgeVPaddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sectionEdgeVPaddingDp, displayMetrics
        ).toInt()
        eachItemHPaddingPx = (sectionEdgeHPaddingPx * 2 + gapHSizePx * (spanCount - 1)) / spanCount
    }

    private fun findSectionLastItemPos(curPos: Int): Section? {
        for (section in mSectionList) {
            if (section!!.contains(curPos)) {
                return section
            }
        }
        return null
    }

    private fun isLastRow(visualPos: Int, spanCount: Int, sectionItemCount: Int): Boolean {
        var lastRowCount = sectionItemCount % spanCount
        lastRowCount = if (lastRowCount == 0) spanCount else lastRowCount
        return visualPos > sectionItemCount - lastRowCount
    }

}