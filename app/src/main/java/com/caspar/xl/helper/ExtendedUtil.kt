package com.caspar.xl.helper


import com.caspar.base.helper.LogUtil
import com.caspar.xl.network.util.NetException
import com.google.gson.JsonParseException
import kotlinx.coroutines.CoroutineExceptionHandler
import org.json.JSONException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * 数据仓库中的异步请求回调
 */
fun Any.call(job: (code:Int,message:String) -> Unit): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, throwable ->
        LogUtil.e(throwable)
        when (throwable) {
            is HttpException -> {
                var errorBody = throwable.response()?.errorBody()?.string()
                errorBody = when (throwable.code()) {
                    404 -> "The right resources were not found"
                    500 -> "Server internal error"
                    else -> NetException.BAD_NETWORK + errorBody
                }
                job.invoke(throwable.code(),errorBody)
            }
            is ConnectException, is UnknownHostException -> job.invoke(-1,NetException.CONNECT_ERROR)
            is InterruptedIOException -> job.invoke(-1,NetException.CONNECT_TIMEOUT)
            is JsonParseException, is JSONException, is ParseException -> job.invoke(-1,NetException.PARSE_ERROR)
            else -> job.invoke(-1,NetException.UNKNOWN_ERROR)
        }
    }
}
