package com.caspar.xl.network

import com.google.gson.JsonParseException
import com.caspar.xl.network.util.NetException
import org.json.JSONException
import retrofit2.HttpException
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException

class ApiResponse<T> {
    var code = SUCCESS_CODE
    var body: T? = null
    var msg: String? = null

    constructor(response: Response<T>) {
        body = response.body()
    }

    constructor(t: T) {
        body = t
    }

    constructor(throwable: Throwable) {
        body = null
        code = ERROR_CODE
        msg = when (throwable) {
            is HttpException -> {
                code = throwable.code()
                val errorBody = throwable.response()?.errorBody()?.string()
                when (throwable.code()) {
                    404 -> "The right resources were not found"
                    500 -> "Server internal error"
                    else -> NetException.BAD_NETWORK + errorBody
                }
            }
            is ConnectException, is UnknownHostException -> NetException.CONNECT_ERROR
            is InterruptedIOException -> NetException.CONNECT_TIMEOUT
            is JsonParseException, is JSONException, is ParseException -> NetException.PARSE_ERROR
            else -> throwable.message
        }
    }

    companion object {
        const val ERROR_CODE = 99999
        const val SUCCESS_CODE = 200
    }
}
