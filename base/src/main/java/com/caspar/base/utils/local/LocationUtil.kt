package com.caspar.base.utils.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.caspar.base.utils.log.LogUtil
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 根据系统定位获取当前位置信息
 * 定位权限等操作自己在外部判断
 * @param loc 如果需要变成别的语言，这里传入Locale相关地区的即可
 * 新增安卓 11.0 以上的定位方法判断，暂未测试，不确定是否可用
 * @return first[是否获取到定位],second[获取到的定位信息],third[获取不到经纬度的错误原因]
 */
@SuppressLint("MissingPermission")
suspend fun Context.getLocation(loc: Locale? = null): Triple<Boolean, LocationBean, Throwable?> {
    return suspendCoroutine { continuation ->
        (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            .takeIf { it }?.let {
                //没有定位权限
                continuation.resume(Triple(false, LocationBean(), Throwable("no local permission")))
            } ?: run {
            //拥有定位权限
            val locationManager = ContextCompat.getSystemService(this, LocationManager::class.java)
            locationManager?.apply {
                val providers = this.getProviders(true)
                providers.takeIf { it.isEmpty() }?.let {
                    continuation.resume(Triple(false, LocationBean(), Throwable("providers null")))
                } ?: run {
                    var local = false
                    val locationListener: LocationListener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            if (!local){
                                local = true
                                val latitude = location.latitude
                                val longitude = location.longitude
                                val geocoder = Geocoder(this@getLocation, loc ?: Locale.getDefault())
                                try {
                                    val address = geocoder.getFromLocation(latitude, longitude, 1)?.getOrElse(0) { null }
                                    address?.apply {
                                        val countryName = this.countryName
                                        val countryCode = this.countryCode
                                        LogUtil.d(LocationBean(latitude, longitude, countryName, countryCode, address).toString())
                                    }
                                } catch (e: IOException) {
                                    LogUtil.e(e)
                                    e.printStackTrace()
                                }
                            }
                            LogUtil.d("触发定位信息")
                            //updateLocation(location, callback);
                        }
                        @Deprecated("Deprecated in Java")
                        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }

                    val mLocationProvider = when {
                        providers.contains(LocationManager.NETWORK_PROVIDER) -> {
                            LocationManager.NETWORK_PROVIDER
                        }
                        providers.contains(LocationManager.GPS_PROVIDER) -> {
                            LocationManager.GPS_PROVIDER
                        }
                        providers.contains(LocationManager.PASSIVE_PROVIDER) -> {
                            LocationManager.PASSIVE_PROVIDER
                        }
                        else -> {
                            continuation.resume(Triple(false, LocationBean(), Throwable("providers error")))
                            return@apply
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        this.getCurrentLocation(mLocationProvider, CancellationSignal(),{
                            LogUtil.d("----")
                        },{
                            val latitude = it.latitude
                            val longitude = it.longitude
                            val geocoder = Geocoder(this@getLocation, loc ?: Locale.getDefault())
                            try {
                                val address = geocoder.getFromLocation(latitude, longitude, 1)?.getOrElse(0) { null }
                                address?.apply {
                                    val countryName = this.countryName
                                    val countryCode = this.countryCode
                                    continuation.resume(Triple(true, LocationBean(latitude, longitude, countryName, countryCode, address), null))
                                }?:run{
                                    continuation.resume(Triple(false, LocationBean(), Throwable("address null")))
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                continuation.resume(Triple(false, LocationBean(), e))
                            }
                        })
                    } else {
                        this.requestSingleUpdate(mLocationProvider, locationListener, null)
                        val location = this.getLastKnownLocation(mLocationProvider)
                        location?.apply {
                            val latitude = this.latitude
                            val longitude = this.longitude
                            val geocoder = Geocoder(this@getLocation, loc ?: Locale.getDefault())
                            try {
                                val address = geocoder.getFromLocation(latitude, longitude, 1)?.getOrElse(0) { null }
                                address?.apply {
                                    val countryName = this.countryName
                                    val countryCode = this.countryCode
                                    continuation.resume(Triple(true, LocationBean(latitude, longitude, countryName, countryCode, address), null))
                                }?:run{
                                    continuation.resume(Triple(false, LocationBean(), Throwable("address null")))
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                continuation.resume(Triple(false, LocationBean(), e))
                            }
                        }?:run {
                            continuation.resume(Triple(false, LocationBean(), Throwable("location null")))
                        }
                    }
                }
            } ?: run {
                continuation.resume(Triple(false, LocationBean(), Throwable("locationManager null")))
            }
        }
    }
}

suspend fun Context.getLocal(
    int: Int = 1,
    maxRetry: Int = 3,
    locale: Locale? = Locale.getDefault(),
): LocationBean? {
    val location = this.getLocation(locale)
    if (location.first) {
        return location.second
    } else {
        if (int < maxRetry) {
            delay(100)
            getLocal(int + 1)
        } else {
            return null
        }
    }
    return null
}