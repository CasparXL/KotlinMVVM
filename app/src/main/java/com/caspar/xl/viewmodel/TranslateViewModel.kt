package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caspar.base.helper.LogUtil
import com.caspar.xl.bean.NetworkResult
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.helper.call
import com.caspar.xl.repository.MenuRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
class TranslateViewModel(application: Application) : AndroidViewModel(application) {
    //mData，使用Triple<A,B,C>储存网络请求反应，A为网络请求是否成功，B为成功的消息，C为错误的信息
    var translateResult : MutableStateFlow<NetworkResult<TranslateBean>> = MutableStateFlow(NetworkResult.Loading())

    fun translate(text:String){
        viewModelScope.launch {
            MenuRepository.translate(text).collect {
                //单个网络请求，请求结束以后，该次flow会释放掉(也就是onCompletion方法会被执行)，translateResult只有界面销毁以后会释放掉
                translateResult.emit(it)
            }
        }
    }
}
