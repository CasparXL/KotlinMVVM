package com.caspar.xl.utils.decoration

import java.util.*

/**
 * Created by win7 on 2018/11/21.
 */
object Decoration {
    /**
     * RecyclerView 线性布局
     *
     * @param top    上
     * @param bottom 下
     * @param left   左
     * @param right  右
     * @return
     */
    fun decoration(top: Int, bottom: Int, left: Int, right: Int): RecyclerViewSpacesItemDecoration {
        val stringIntegerHashMap = HashMap<String, Int>()
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.TOP_DECORATION] = top //top间距
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION] = bottom //底部间距
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.LEFT_DECORATION] = left //左间距
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.RIGHT_DECORATION] = right //右间距
        return RecyclerViewSpacesItemDecoration(stringIntegerHashMap)
    }

    /**
     * RecyclerView 线性布局
     * 针对有父节点的布局，子节点无间隔，父节点有间隔
     *
     * @param top    上
     * @param bottom 下
     * @param left   左
     * @param right  右
     * @return
     */
    fun decoration2(top: Int, bottom: Int, left: Int, right: Int): RecyclerViewSpacesItemDecoration2 {
        val stringIntegerHashMap = HashMap<String, Int>()
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.TOP_DECORATION] = top //top间距
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION] = bottom //底部间距
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.LEFT_DECORATION] = left //左间距
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.RIGHT_DECORATION] = right //右间距
        return RecyclerViewSpacesItemDecoration2(stringIntegerHashMap)
    }

    /**
     * RecyclerView 网格布局
     *
     * @param spanCount   一行几列
     * @param spacing     空格多少
     * @param includeEdge 是否判断最左边和最右边有空格
     * @return
     */
    fun GridDecoration(spanCount: Int, spacing: Int, includeEdge: Boolean): RecyclerViewGridDecoration {
        return RecyclerViewGridDecoration(spanCount, spacing, includeEdge)
    }
}