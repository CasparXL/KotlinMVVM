package com.caspar.xl.utils.decoration

import android.graphics.Rect
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import java.util.*

class RecyclerViewSpacesItemDecoration2 internal constructor(private val mSpaceValueMap: HashMap<String, Int>) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.apply{
            if (this.getItemViewType(parent.getChildAdapterPosition(view))==0){
                if (mSpaceValueMap[TOP_DECORATION] != null) outRect.top = mSpaceValueMap[TOP_DECORATION]!!
                if (mSpaceValueMap[LEFT_DECORATION] != null) outRect.left = mSpaceValueMap[LEFT_DECORATION]!!
                if (mSpaceValueMap[RIGHT_DECORATION] != null) outRect.right = mSpaceValueMap[RIGHT_DECORATION]!!
                if (mSpaceValueMap[BOTTOM_DECORATION] != null) outRect.bottom = mSpaceValueMap[BOTTOM_DECORATION]!!
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