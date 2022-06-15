package com.caspar.xl.widget.index.listener

interface OnSideBarTouchListener {
    /**
     * 触摸SideBar时回调
     *
     * @param text     SideBar上选中的索引字符
     * @param position RecyclerView将要滚动到的位置(-1代表未找到目标位置，则列表不用滚动)
     */
    fun onTouch(text: String, position: Int)

    /**
     * 触摸结束回调
     */
    fun onTouchEnd()
}