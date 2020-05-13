package com.caspar.xl.repository

import com.caspar.base.base.BaseRepository
import com.caspar.xl.helper.call
import com.caspar.xl.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
object TestRepository : BaseRepository() {
    //网络请求，获取城市列表
    suspend fun getCity() = withContext(Dispatchers.IO) {
        call {
            Api.api.getCity()
        }
    }
}