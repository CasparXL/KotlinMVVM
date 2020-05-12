package com.caspar.xl.bean

import java.io.Serializable

/**
 * Created by leo
 * on 17/12/26.
 * 这个类是泛型类，可根据后端的返回字段修改
 */
class Resource<T>(var status:Int = 0, var msg: String? = null, var obj: T? = null):Serializable