package com.caspar.xl.helper


import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.network.util.NetException
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * 数据仓库中的异步请求Error回调
 */
fun Any.call(throwable: Throwable): Pair<Int,String>{
    LogUtil.e(throwable)
    return when (throwable) {
        is HttpException -> {
            var errorBody = throwable.response()?.errorBody()?.string()
            errorBody = when (throwable.code()) {
                404 -> "The right resources were not found"
                500 -> "Server internal error"
                else -> NetException.BAD_NETWORK + errorBody
            }
            return throwable.code() to errorBody
        }
        is ConnectException, is UnknownHostException -> -1 to NetException.CONNECT_ERROR
        is InterruptedIOException -> -1 to NetException.CONNECT_TIMEOUT
        is JsonParseException, is JSONException, is ParseException -> -1 to NetException.PARSE_ERROR
        else -> -1 to NetException.UNKNOWN_ERROR
    }
}
