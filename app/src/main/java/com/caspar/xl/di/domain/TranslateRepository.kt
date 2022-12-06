package com.caspar.xl.di.domain

import com.caspar.xl.bean.response.TranslateBean

/**
 * 用户相关请求仓库
 */
interface TranslateRepository {
    /**
     * 翻译接口
     * @return 登录结果
     */
    suspend fun requestTranslate(text:String): Result<TranslateBean>
}