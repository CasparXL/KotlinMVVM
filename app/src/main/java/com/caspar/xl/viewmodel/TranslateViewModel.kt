package com.caspar.xl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.di.BodyOkHttpClient
import com.caspar.xl.di.HeaderOkHttpClient
import com.caspar.xl.eventandstate.ViewEvent
import com.caspar.xl.ext.SharedFlowEvents
import com.caspar.xl.ext.setEvent
import com.caspar.xl.helper.exportError
import com.caspar.xl.helper.otherResult
import com.caspar.xl.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
@HiltViewModel
class TranslateViewModel @Inject constructor(@BodyOkHttpClient private val api:ApiService) : ViewModel() {
    /***基础请求管理**/
    private val _viewEvent: SharedFlowEvents<ViewEvent> = SharedFlowEvents()
    val viewEvent = _viewEvent.asSharedFlow()

    //设置性质网络的请求，使用MutableSharedFlow，
    //读取并固定展示性质的网络请求，可以使用MutableStateFlow，可以在不必要的时候节省资源,类似于LiveData的生命周期感知
    private val _translateResult: MutableSharedFlow<TranslateBean> = MutableSharedFlow()
    val translateResult = _translateResult.asSharedFlow()

    /**
     * 网络请求
     */
    fun translate(text: String) {
        viewModelScope.launch {
            val result = otherResult { api.translate(text = text) }
            result.onSuccess { _translateResult.emit(it) }
                .onFailure { _viewEvent.setEvent(ViewEvent.ShowToast(exportError(it).second)) }
            _viewEvent.setEvent(ViewEvent.DismissDialog)
        }
    }
}
