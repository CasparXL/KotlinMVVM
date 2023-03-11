package com.caspar.xl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.xl.bean.response.ImageBean
import com.caspar.xl.di.domain.TranslateRepository
import com.caspar.xl.eventandstate.ViewEvent
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
) :
    ViewModel() {
    /***基础请求管理**/
    private val _viewEvent: SharedFlowEvents<ViewEvent> = SharedFlowEvents()
    val viewEvent = _viewEvent.asSharedFlow()

    //设置性质网络的请求，使用MutableSharedFlow，
    //读取并固定展示性质的网络请求，可以使用MutableStateFlow，可以在不必要的时候节省资源,类似于LiveData的生命周期感知
    private val _translateResult: MutableSharedFlow<List<ImageBean>> = MutableSharedFlow()
    val translateResult = _translateResult.asSharedFlow()

    /**
     * 网络请求
     */
    fun getImage() {
        viewModelScope.launch {
            _viewEvent.setEvent(ViewEvent.ShowDialog)
            val result = repository.requestGetImages()
            result.onSuccess { _translateResult.emit(it) }
                .onFailure { _viewEvent.setEvent(ViewEvent.ShowToast(it.message ?: "")) }
            _viewEvent.setEvent(ViewEvent.DismissDialog)
        }
    }
}
