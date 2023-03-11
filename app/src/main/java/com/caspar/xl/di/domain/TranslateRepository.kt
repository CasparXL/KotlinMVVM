package com.caspar.xl.di.domain

import com.caspar.xl.bean.response.ImageBean

/**
 * 用户相关请求仓库
 */
interface TranslateRepository {
    /**
     * 翻译接口
     * @return 翻译结果
     */
    suspend fun requestGetImages(): Result<List<ImageBean>>
}