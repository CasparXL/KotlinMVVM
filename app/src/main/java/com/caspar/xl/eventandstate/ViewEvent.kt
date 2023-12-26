package com.caspar.xl.eventandstate

import com.caspar.xl.bean.response.ImageBean

sealed interface ViewState {
    /***全局性质的状态**/
    interface Global : ViewState {
        data object Loading : Global
        data class Error(val message: String) : Global
    }
    /***局部性质的状态**/
    interface Content : ViewState {
        data class RoomViewState(var str: String = "", var teacherId: Long = -1L, var userId: Long = -1L, ) : Content
        data class TransitionViewState(val imageList: List<ImageBean>) : Content
    }
}