package com.caspar.xl.bean

class BaseBean<T>(
    val data: T? = null,
    val status: Int? = 0,
    val message: String? = null,
    val page: PageBean? = null,
    val timestamp: String? = null,
    val pattern: String? = null,
) {
    fun getResult(): Result<T> {
        return if (status == 200) {
            Result.success(data!!)
        } else {
            Result.failure(Exception("$message:$status"))
        }
    }

}

class PageBean(val pageNo: Int? = 1, val pageSize: Int? = 10, val pageTotal: Int? = 1)