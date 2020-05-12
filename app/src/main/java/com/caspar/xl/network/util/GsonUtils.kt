package com.caspar.xl.network.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/04/05
 * desc  : utils about gson
</pre> *
 */
class GsonUtils private constructor() {
    companion object {
        //GsonUtils.fromJson(text,new TypeToken<BaseResponse<List<LightInfoBean>>>(){}.getType() )
        private val GSON = createGson(true)
        private val GSON_NO_NULLS = createGson(false)

        /**
         * Gets pre-configured [Gson] instance.
         *
         * @return [Gson] instance.
         */
        val gson: Gson
            get() = getGson(true)

        /**
         * Gets pre-configured [Gson] instance.
         *
         * @param serializeNulls Determines if nulls will be serialized.
         * @return [Gson] instance.
         */
        fun getGson(serializeNulls: Boolean): Gson {
            return if (serializeNulls) GSON_NO_NULLS else GSON
        }
        /**
         * Serializes an object into json.
         *
         * @param object       The object to serialize.
         * @param includeNulls Determines if nulls will be included.
         * @return object serialized into json.
         */
        /**
         * Serializes an object into json.
         *
         * @param object The object to serialize.
         * @return object serialized into json.
         */
        @JvmOverloads
        fun toJson(`object`: Any?, includeNulls: Boolean = true): String {
            return if (includeNulls) GSON.toJson(`object`) else GSON_NO_NULLS.toJson(
                `object`
            )
        }
        /**
         * Serializes an object into json.
         *
         * @param src          The object to serialize.
         * @param typeOfSrc    The specific genericized type of src.
         * @param includeNulls Determines if nulls will be included.
         * @return object serialized into json.
         */
        /**
         * Serializes an object into json.
         *
         * @param src       The object to serialize.
         * @param typeOfSrc The specific genericized type of src.
         * @return object serialized into json.
         */
        @JvmOverloads
        fun toJson(
            src: Any?,
            typeOfSrc: Type?,
            includeNulls: Boolean = true
        ): String {
            return if (includeNulls) GSON.toJson(
                src,
                typeOfSrc
            ) else GSON_NO_NULLS.toJson(src, typeOfSrc)
        }

        /**
         * Converts [String] to given type.
         *
         * @param json The json to convert.
         * @param type Type json will be converted to.
         * @return instance of type
         */
        fun <T> fromJson(json: String?, type: Class<T>?): T {
            return GSON.fromJson(json, type)
        }

        /**
         * Converts [String] to given type.
         *
         * @param json the json to convert.
         * @param type type type json will be converted to.
         * @return instance of type
         */
        fun <T> fromJson(json: String?, type: Type?): T {
            return GSON.fromJson(json, type)
        }

        /**
         * Converts [Reader] to given type.
         *
         * @param reader the reader to convert.
         * @param type   type type json will be converted to.
         * @return instance of type
         */
        fun <T> fromJson(reader: Reader?, type: Class<T>?): T {
            return GSON.fromJson(reader, type)
        }

        /**
         * Converts [Reader] to given type.
         *
         * @param reader the reader to convert.
         * @param type   type type json will be converted to.
         * @return instance of type
         */
        fun <T> fromJson(reader: Reader?, type: Type?): T {
            return GSON.fromJson(reader, type)
        }

        /**
         * Return the type of [List] with the `type`.
         *
         * @param type The type.
         * @return the type of [List] with the `type`
         */
        fun getListType(type: Type?): Type {
            return TypeToken.getParameterized(MutableList::class.java, type)
                .type
        }

        /**
         * Return the type of [Set] with the `type`.
         *
         * @param type The type.
         * @return the type of [Set] with the `type`
         */
        fun getSetType(type: Type?): Type {
            return TypeToken.getParameterized(MutableSet::class.java, type)
                .type
        }

        /**
         * Return the type of map with the `keyType` and `valueType`.
         *
         * @param keyType   The type of key.
         * @param valueType The type of value.
         * @return the type of map with the `keyType` and `valueType`
         */
        fun getMapType(
            keyType: Type?,
            valueType: Type?
        ): Type {
            return TypeToken.getParameterized(
                MutableMap::class.java,
                keyType,
                valueType
            ).type
        }

        /**
         * Return the type of array with the `type`.
         *
         * @param type The type.
         * @return the type of map with the `type`
         */
        fun getArrayType(type: Type?): Type {
            return TypeToken.getArray(type).type
        }

        /**
         * Return the type of `rawType` with the `typeArguments`.
         *
         * @param rawType       The raw type.
         * @param typeArguments The type of arguments.
         * @return the type of map with the `type`
         */
        fun getType(
            rawType: Type?,
            vararg typeArguments: Type?
        ): Type {
            return TypeToken.getParameterized(rawType, *typeArguments).type
        }

        /**
         * Create a pre-configured [Gson] instance.
         *
         * @param serializeNulls determines if nulls will be serialized.
         * @return [Gson] instance.
         */
        private fun createGson(serializeNulls: Boolean): Gson {
            val builder = GsonBuilder()
            if (serializeNulls) builder.serializeNulls()
            return builder.create()
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}