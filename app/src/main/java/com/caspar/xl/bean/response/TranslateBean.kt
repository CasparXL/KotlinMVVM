package com.caspar.xl.bean.response


/**
 *  @Create 2020/6/13.
 *  @Use
 */
data class TranslateBean(
    var elapsedTime: Int?,
    var errorCode: Int?,
    var translateResult: List<List<TranslateResult>>?,
    var type: String?
)
data class TranslateResult(
    var src: String?="",
    var tgt: String?=""
)