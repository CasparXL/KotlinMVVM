package com.caspar.xl.di.domain.impl

import com.caspar.xl.bean.response.ImageBean
import com.caspar.xl.di.BodyOkHttpClient
import com.caspar.xl.di.domain.TranslateRepository
import com.caspar.xl.network.ApiService
import com.caspar.xl.network.util.otherResult
import javax.inject.Inject

class TranslateRepositoryImpl @Inject constructor(
    @BodyOkHttpClient private val api: ApiService
) : TranslateRepository {
    override suspend fun requestGetImages(): Result<List<ImageBean>> {
        return otherResult {
            api.requestTranslate()
        }
   }
}