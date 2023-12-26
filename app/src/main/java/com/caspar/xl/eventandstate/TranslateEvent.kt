package com.caspar.xl.eventandstate

import com.caspar.xl.bean.response.ImageBean

interface TranslateEvent:ViewState {
    data class TransitionViewState(val imageList: List<ImageBean>) : ViewState
}