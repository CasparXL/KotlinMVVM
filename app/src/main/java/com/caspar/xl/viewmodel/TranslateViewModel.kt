package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.bean.NetworkResult
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.network.util.GsonUtils
import com.caspar.xl.repository.MenuRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
class TranslateViewModel(application: Application) : AndroidViewModel(application) {
    //数据仓库类
    private val repository: MenuRepository by lazy {
        MenuRepository()
    }

    //网络请求，使用MutableStateFlow，可以在不必要的时候节省资源,类似于LiveData的生命周期感知
    var translateResult: MutableStateFlow<NetworkResult<TranslateBean>> =
        MutableStateFlow(NetworkResult.Loading())

    /**
     * 网络请求
     */
    fun translate(text: String) {
        viewModelScope.launch {
            repository.translate(text).collect {
                translateResult.value = it
            }
        }
    }
}
