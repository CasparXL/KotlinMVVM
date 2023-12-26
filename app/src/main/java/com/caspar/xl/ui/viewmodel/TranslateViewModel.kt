package com.caspar.xl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.xl.di.domain.TranslateRepository
import com.caspar.xl.eventandstate.ViewState
import com.caspar.xl.ext.SharedFlowEvents
import com.caspar.xl.ext.setEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val repository: TranslateRepository
) :ViewModel() {
    /***基础请求管理**/
    private val _viewEvent: SharedFlowEvents<ViewState> = SharedFlowEvents()
    val viewEvent = _viewEvent.asSharedFlow()
    /**
     * 网络请求
     */
    fun getImage() {
        viewModelScope.launch {
            _viewEvent.setEvent(ViewState.Global.Loading)
            val result = repository.requestGetImages()
            result.onSuccess { _viewEvent.setEvent(ViewState.Content.TransitionViewState(it)) }
                .onFailure { _viewEvent.setEvent(ViewState.Global.Error(it.message ?: "")) }
        }
    }
}
