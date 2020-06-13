package com.caspar.xl.config

/**
 * 存放网络接口
 */
object ApiConfig {
    //网络请求的BaseUrl层
    var BaseUrl = "http://www.baidu.com/"
    //网络请求,阿里云的城市获取
    const val getCity =
        "http://datavmap-public.oss-cn-hangzhou.aliyuncs.com/areas/csv/100000_province.json"

    //网络请求,翻译
    const val Translate = "http://fanyi.youdao.com/translate"
}