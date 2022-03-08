package com.caspar.base.ext


import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

/**
 * @date 2020/6/25
 * time 15:30
 * description
 * 权限请求扩展函数
 */
const val DENIED = "DENIED"
const val EXPLAINED = "EXPLAINED"

/**
 * @param permission 权限名称
 * @param granted 申请成功
 * @param denied 被拒绝且未勾选不再询问
 * @param explained 被拒绝且勾选不再询问
 */
inline fun ComponentActivity.requestPermission(
    permission: String,
    crossinline granted: (permission: String) -> Unit = {},
    crossinline denied: (permission: String) -> Unit = {},
    crossinline explained: (permission: String) -> Unit = {}
): ActivityResultLauncher<String> {
    return registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (Build.VERSION.SDK_INT >= 23) {
            when {
                result -> granted.invoke(permission)
                shouldShowRequestPermissionRationale(permission) -> denied.invoke(permission)
                else -> explained.invoke(permission)
            }
        } else {
            when {
                result -> granted.invoke(permission)
                else -> explained.invoke(permission)
            }
        }
    }
}

/**
 * @param allGranted 所有权限均申请成功
 * @param denied 被拒绝且未勾选不再询问，同时被拒绝且未勾选不再询问的权限列表
 * @param explained 被拒绝且勾选不再询问，同时被拒绝且勾选不再询问的权限列表
 */
inline fun ComponentActivity.requestMultiplePermissions(
    crossinline allGranted: () -> Unit = {},
    crossinline denied: (List<String>) -> Unit = {},
    crossinline explained: (List<String>) -> Unit = {}
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
        //过滤 value 为 false 的元素并转换为 list
        val deniedList = result.filter { !it.value }.map { it.key }
        when {
            deniedList.isNotEmpty() -> {
                //对被拒绝全选列表进行分组，分组条件为是否勾选不再询问
                val map = deniedList.groupBy { permission ->
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                    } else {
                        DENIED
                    }
                }
                //被拒接且没勾选不再询问
                map[DENIED]?.let { denied.invoke(it) }
                //被拒接且勾选不再询问
                map[EXPLAINED]?.let { explained.invoke(it) }
            }
            else -> allGranted.invoke()
        }
    }
}

/**
 * [permission] 权限名称
 * [granted] 申请成功
 * [denied] 被拒绝且未勾选不再询问
 * [explained] 被拒绝且勾选不再询问
 */
inline fun Fragment.requestPermission(
    permission: String,
    crossinline granted: (permission: String) -> Unit = {},
    crossinline denied: (permission: String) -> Unit = {},
    crossinline explained: (permission: String) -> Unit = {}
): ActivityResultLauncher<String> {
    return registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        when {
            result -> granted.invoke(permission)
            shouldShowRequestPermissionRationale(permission) -> denied.invoke(permission)
            else -> explained.invoke(permission)
        }
    }
}

/**
 * [allGranted] 所有权限均申请成功
 * [denied] 被拒绝且未勾选不再询问，同时被拒绝且未勾选不再询问的权限列表
 * [explained] 被拒绝且勾选不再询问，同时被拒绝且勾选不再询问的权限列表
 */
inline fun Fragment.requestMultiplePermissions(
    crossinline allGranted: () -> Unit = {},
    crossinline denied: (List<String>) -> Unit = {},
    crossinline explained: (List<String>) -> Unit = {}
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
        //过滤 value 为 false 的元素并转换为 list
        val deniedList = result.filter { !it.value }.map { it.key }
        when {
            deniedList.isNotEmpty() -> {
                //对被拒绝全选列表进行分组，分组条件为是否勾选不再询问
                val map = deniedList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                //被拒接且没勾选不再询问
                map[DENIED]?.let { denied.invoke(it) }
                //被拒接且勾选不再询问
                map[EXPLAINED]?.let { explained.invoke(it) }
            }
            else -> allGranted.invoke()
        }
    }
}
