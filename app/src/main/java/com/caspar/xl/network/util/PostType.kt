package com.caspar.xl.network.util

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*


/**
 *
 *
 *  上传单张
 *  @Multipart
 *  @POST(ApiConstants.uploadImg)
 *  suspend fun uploadImg(@Part part: List<MultipartBody.Part>): BaseBean<String>
 *  上传多张图片
 *  @Multipart
 *  @POST(ApiConstants.deviceInstallImg)
 *  suspend fun deviceInstallImg(@Part part: List<MultipartBody.Part>): BaseBean<List<String>>
 *
 *  调用时 val base = Api.api.uploadImg(head.toMultipartBody())
 */
/**
 * 上传图片类型转换
 *
 * @param value
 * @return
 */
fun File.toFileRequestBody(): RequestBody {
    return this.asRequestBody("multipart/form-data".toMediaTypeOrNull())
}

/**
 * 单图片上传
 */
fun File.toMultipartBody(): List<MultipartBody.Part> {
    return MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("file", this.name, this.toFileRequestBody())
        .build().parts
}

/**
 * 多图片上传
 */
fun List<File>.toMultipartBody(): List<MultipartBody.Part>{
    val mutableList:MutableList<MultipartBody.Part> = mutableListOf()
    this.forEach {
        val parts = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", it.name, it.toFileRequestBody())
            .build().parts
        mutableList.addAll(parts)
    }
    return mutableList
}
