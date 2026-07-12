package com.example.kasudlo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

object NetworkUtils {

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Registers a connectivity callback and calls [onAvailable] / [onLost]
     * on the main thread whenever network state changes.
     *
     * Returns the NetworkCallback so the caller can unregister it in onDestroy.
     *
     * Example:
     *   private var networkCallback: ConnectivityManager.NetworkCallback? = null
     *
     *   override fun onCreate(...) {
     *       networkCallback = NetworkUtils.registerCallback(this,
     *           onAvailable = { offlineBanner.visibility = View.GONE },
     *           onLost      = { offlineBanner.visibility = View.VISIBLE }
     *       )
     *   }
     *
     *   override fun onDestroy() {
     *       super.onDestroy()
     *       NetworkUtils.unregisterCallback(this, networkCallback)
     *   }
     */
    fun registerCallback(
        context: Context,
        onAvailable: () -> Unit,
        onLost: () -> Unit
    ): ConnectivityManager.NetworkCallback {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                android.os.Handler(android.os.Looper.getMainLooper()).post { onAvailable() }
            }
            override fun onLost(network: Network) {
                android.os.Handler(android.os.Looper.getMainLooper()).post { onLost() }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(request, callback)
        return callback
    }

    fun unregisterCallback(
        context: Context,
        callback: ConnectivityManager.NetworkCallback?
    ) {
        callback ?: return
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            cm.unregisterNetworkCallback(callback)
        } catch (_: Exception) {}
    }
}