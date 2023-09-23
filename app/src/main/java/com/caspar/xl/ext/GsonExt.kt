package com.caspar.xl.ext

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


val gson: Gson
    get() {
        val builder = GsonBuilder().setPrettyPrinting()
        return builder.create()
    }

@Suppress("PROTECTED_CALL_FROM_PUBLIC_INLINE")
inline fun <reified T: Any> gsonTypeToken(): Type = object : TypeToken<T>() {} .type

fun Any.toJson(): String {
    return gson.toJson(this)
}

inline fun <reified T: Any> String.fromJson(): T {
    return gson.fromJson(this, T::class.java)
}

