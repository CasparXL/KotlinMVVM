package com.caspar.commom.utils.local

import android.location.Address

class LocationBean(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var countryName: String? = null,
    var countryCode: String? = null,
    var address: Address? = null,
) {
    //国家
    val country: String
        get() = countryName ?: countryCode ?: ""
    //省
    val province: String
        get() {
            address?.adminArea?.let {
                return it
            } ?: run {
                return "unknown"
            }
        }
    //城市
    val city: String
        get() {
            address?.let {
                return it.locality
            } ?: run {
                return "unknown"
            }
        }

    //区
    val area: String
        get() {
            address?.subLocality?.let {
                return it
            } ?: run {
                return "unknown"
            }
        }
    //详细地址
    val addressDetail: String
        get() {
            address?.featureName?.let {
                return it
            } ?: run {
                return "unknown"
            }
        }
    //邮编
    val adCode: String
        get() {
            address?.extras?.let {
                return (it.get("adcode") as String?)?: "unknown"
            } ?: run {
                return "unknown"
            }
        }
    //区号
    val cityCode: String
        get() {
            address?.extras?.let {
                return (it.get("citycode") as String?)?: "unknown"
            } ?: run {
                return "unknown"
            }
        }
}