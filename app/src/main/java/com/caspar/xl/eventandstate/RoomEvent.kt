package com.caspar.xl.eventandstate

sealed interface RoomEvent : ViewState {
    data class RoomViewState(
        var str: String = "",
        var teacherId: Long = -1L,
        var userId: Long = -1L,
    ) : RoomEvent
}