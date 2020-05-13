package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caspar.base.base.BaseViewModel
import com.caspar.xl.bean.Resource
import com.caspar.xl.bean.response.City
import com.caspar.xl.repository.TestRepository
import kotlinx.coroutines.launch

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
class TestViewModel(application: Application) : BaseViewModel(application) {
    //dataBean，根据接口生命多个dataBean
    val mData:MutableLiveData<City> by lazy {
        MutableLiveData<City>()
    }
    //errorBean，一个Activity共用一个就行了
    val mError:MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }

    fun getCity() {
        viewModelScope.launch {
            val value = TestRepository.getCity()
            if (value.code==200){ //网络请求成功，则返回数据
                mData.value=value.body
            }else{//网络请求失败，则返回一个errorBean，errorBean一个Activity拦截一次就够了，统一做处理
                mError.value= Resource(status = value.code,msg = value.msg,obj = null)
            }
        }
    }
}