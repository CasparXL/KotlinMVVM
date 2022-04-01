package com.caspar.xl.eventandstate

/**
 * 基础视图管理
 */
sealed class ViewEvent {
    data class ShowToast(val message: String) : ViewEvent()
    object ShowDialog : ViewEvent()
    object DismissDialog : ViewEvent()
}

data class RoomViewState(
    var str: String = "",
    var teacherId: Long = -1L,
    var userId: Long = -1L,
)