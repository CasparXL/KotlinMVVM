package com.caspar.xl.config

/**
 * 用于存放所有界面跳转路径的
 */
object ARouterKey {
    //房间uuid
    const val RoomUuid = "roomUuid"

    //分组uuid
    const val GroupUuid = "groupUuid"

    //房间名称
    const val RoomName = "roomName"

    //场景数据
    const val Scene = "scene"

    //设备的uuid
    const val DeviceUuid = "deviceUuid"

    //设备的名称
    const val DeviceName = "deviceName"

    //扫描设备的类型【0是普通，1是check界面，2是change界面】
    const val isCheck = "isCheck"
    //需要替换的设备
    const val arrayList = "arrayList"
}