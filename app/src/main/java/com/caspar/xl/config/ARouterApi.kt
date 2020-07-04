package com.caspar.xl.config

/**
 * 用于存放所有界面跳转路径的
 */
object ARouterApi {
    //--------------------------------父板块[用于界面分组]----------------------------------------------------------
    /***首页相关板块 */
    private const val APP = "/app/"
    private const val HOME = "/home/"

    //--------------------------------父板块----------------------------------------------------------

    //------------------------------------子版块[界面的完整路径]-----------------------------------------------------------
    /***首页 */
    const val MAIN = "${APP}main"
    const val TRANSLATE = "${HOME}translate"
    const val CAMERA = "${HOME}camera"
    const val ROOM = "${HOME}room"

}