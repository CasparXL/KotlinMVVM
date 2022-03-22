package com.caspar.xl.repository

import com.caspar.commom.helper.LogUtil
import com.caspar.xl.bean.NetworkResult
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.helper.call
import com.caspar.xl.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
object MenuRepository {
    //网络请求，获取城市列表
    suspend fun getCity() = withContext(Dispatchers.IO) {
        Api.api.getCity()
    }

    //网络请求，获取城市列表
    fun translate(text: String) = flow<NetworkResult<TranslateBean>> {
        //当网络请求成功会走完当前void，并返回Success出去
        val result = Api.api.translate(text = text)
        emit(NetworkResult.Success(result))
    }.catch { ex->
        //当网络请求尚未完成，且抛出了error，则返回Error出去
        val networkResult = call<TranslateBean>(ex)
        emit(networkResult)
    }.distinctUntilChanged()
}