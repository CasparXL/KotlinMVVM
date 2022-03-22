package com.caspar.commom.utils.status

import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.File
import java.io.FileInputStream
import java.util.*


object RomUtils {
    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错
    val lightStatusBarAvailableRomType: Int
        get() {
            if (isFlymeV4OrAbove) {
                return AvailableRomType.FLYME
            }
            //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错
            if (isMiUIV7OrAbove) {
                return AvailableRomType.ANDROID_NATIVE
            }
            if (isMiUIV6OrAbove) {
                return AvailableRomType.MIUI
            }
            return if (isAndroidMOrAbove) {
                AvailableRomType.ANDROID_NATIVE
            } else AvailableRomType.NA
        }//版本号4以上，形如4.x.

    //Flyme V4的displayId格式为 [Flyme OS 4.x.x.xA]
    //Flyme V5的displayId格式为 [Flyme 5.x.x.x beta]
    private val isFlymeV4OrAbove: Boolean
         get() {
            val displayId = Build.DISPLAY
            if (!TextUtils.isEmpty(displayId) && displayId.contains("Flyme")) {
                val displayIdArray = displayId.split(" ").toTypedArray()
                for (temp in displayIdArray) {
                    //版本号4以上，形如4.x.
                    if (temp.matches(Regex("^[4-9]\\.(\\d+\\.)+\\S*"))) {
                        return true
                    }
                }
            }
            return false
        }

    //Android Api 23以上
    private val isAndroidMOrAbove: Boolean
         get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    private const val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private val isMiUIV6OrAbove: Boolean
         get() = try {
            val properties = Properties()
            properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
            val uiCode = properties.getProperty(KEY_MIUI_VERSION_CODE, null)
            if (uiCode != null) {
                val code = uiCode.toInt()
                code >= 4
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    val isMiUIV7OrAbove: Boolean
        get() = try {
            val properties = Properties()
            properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
            val uiCode = properties.getProperty(KEY_MIUI_VERSION_CODE, null)
            if (uiCode != null) {
                val code = uiCode.toInt()
                code >= 5
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }

    internal object AvailableRomType {
        const val MIUI = 1
        const val FLYME = 2
        const val ANDROID_NATIVE = 3
        const val NA = 4
    }
}