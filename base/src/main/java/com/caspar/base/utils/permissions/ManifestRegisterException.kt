package com.caspar.base.utils.permissions

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/07/18
 * desc   : 动态申请的权限没有在清单文件中注册会抛出的异常
 */
internal class ManifestRegisterException(permission: String?) :
    RuntimeException(if (permission == null) "No permissions are registered in the manifest file" else "$permission: Permissions are not registered in the manifest file")