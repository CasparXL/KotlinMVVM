package com.caspar.xl.utils.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * 横向Grid管理器空格
 */
class RecyclerViewGridHorDecoration internal constructor(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column
        outRect.top = spacing
        outRect.right = spacing
        if (column != 0) {
            outRect.bottom = spacing
        }
        if (position in 0 until spanCount) {
            outRect.left = spacing
        }
    }

}