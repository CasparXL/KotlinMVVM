package com.caspar.commom.utils.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 根据系统定位获取当前位置信息
 * 定位权限等操作自己在外部判断
 * @param loc 如果需要变成别的语言，这里传入Locale相关地区的即可
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
                    val locationListener: LocationListener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            //updateLocation(location, callback);
                        }
                        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }
                    val mLocationProvider = when {
                        providers.contains(LocationManager.NETWORK_PROVIDER) -> {
                            this.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)
                            LocationManager.NETWORK_PROVIDER
                        }
                        providers.contains(LocationManager.GPS_PROVIDER) -> {
                            this.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
                            LocationManager.GPS_PROVIDER
                        }
                        providers.contains(LocationManager.PASSIVE_PROVIDER) -> {
                            this.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, locationListener, null)
                            LocationManager.PASSIVE_PROVIDER
                        }
                        else -> {
                            continuation.resume(Triple(false, LocationBean(), Throwable("providers error")))
                            return@apply
                        }
                    }
                    val location = this.getLastKnownLocation(mLocationProvider)
                    location?.apply {
                        val latitude = this.latitude
                        val longitude = this.longitude
                        val geocoder = Geocoder(this@getLocation, loc ?: Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                            val address = if (addresses != null && addresses.size > 0) addresses[0] else null
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
            } ?: run {
                continuation.resume(Triple(false, LocationBean(), Throwable("locationManager null")))
            }
        }
    }
}
