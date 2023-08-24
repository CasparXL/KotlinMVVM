package com.caspar.xl.utils.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import java.util.*

/**
 * 普通纵向布局
 */
class RecyclerViewSpacesItemDecoration internal constructor(private val mSpaceValueMap: HashMap<String, Int>) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            if (layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
                if (mSpaceValueMap[TOP_DECORATION] != null) outRect.top = mSpaceValueMap[TOP_DECORATION]!!
                if (mSpaceValueMap[LEFT_DECORATION] != null) outRect.left = mSpaceValueMap[LEFT_DECORATION]!!
                if (mSpaceValueMap[RIGHT_DECORATION] != null) {
                    if (parent.adapter?.itemCount != 0 && position == (parent.adapter?.itemCount?:0) - 1){
                        outRect.right = mSpaceValueMap[RIGHT_DECORATION]!!
                    }
                }
                if (mSpaceValueMap[BOTTOM_DECORATION] != null) outRect.bottom = mSpaceValueMap[BOTTOM_DECORATION]!!
            } else if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                if (mSpaceValueMap[TOP_DECORATION] != null) outRect.top = mSpaceValueMap[TOP_DECORATION]!!
                if (mSpaceValueMap[LEFT_DECORATION] != null) outRect.left = mSpaceValueMap[LEFT_DECORATION]!!
                if (mSpaceValueMap[RIGHT_DECORATION] != null) outRect.right = mSpaceValueMap[RIGHT_DECORATION]!!
                if (mSpaceValueMap[BOTTOM_DECORATION] != null){
                    if (parent.adapter?.itemCount != 0 && position == (parent.adapter?.itemCount?:0) - 1){
                        outRect.bottom = mSpaceValueMap[BOTTOM_DECORATION]!!
                    }
                }
            }
        }
    }

    companion object {
        const val TOP_DECORATION = "top_decoration"
        const val BOTTOM_DECORATION = "bottom_decoration"
        const val LEFT_DECORATION = "left_decoration"
        const val RIGHT_DECORATION = "right_decoration"
    }

}