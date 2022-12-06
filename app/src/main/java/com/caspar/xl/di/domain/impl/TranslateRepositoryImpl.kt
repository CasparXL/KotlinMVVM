package com.caspar.xl.di.domain.impl

import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.di.BodyOkHttpClient
import com.caspar.xl.di.domain.TranslateRepository
import com.caspar.xl.network.util.otherResult
import com.caspar.xl.network.ApiService
import javax.inject.Inject

class TranslateRepositoryImpl @Inject constructor(
    @BodyOkHttpClient private val api: ApiService
) : TranslateRepository {
    override suspend fun requestTranslate(text: String): Result<TranslateBean> {
        return otherResult {
            api.translate(text = text)
        }
    }
}