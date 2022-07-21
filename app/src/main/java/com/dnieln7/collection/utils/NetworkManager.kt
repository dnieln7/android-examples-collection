package com.dnieln7.collection.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress

object NetworkManager {

    private const val SERVICE = Context.CONNECTIVITY_SERVICE

    suspend fun canPingGoogle(timeout: Int): Boolean {
        if (timeout < 1000) {
            return false
        }

        val connected = try {
            withContext(Dispatchers.IO) {
                InetAddress.getByName("www.google.com").isReachable(timeout)
            }
        } catch (e: Exception) {
            false
        }

        return connected
    }

    fun isWifiEnabled(context: Context): Boolean {
        val manager = context.getSystemService(SERVICE) as ConnectivityManager
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)

        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun isMobileDataEnabled(context: Context): Boolean {
        val manager = context.getSystemService(SERVICE) as ConnectivityManager
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)

        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun isWifiOrMobileDataEnabled(context: Context): Boolean {
        return isWifiEnabled(context) || isMobileDataEnabled(context)
    }
}