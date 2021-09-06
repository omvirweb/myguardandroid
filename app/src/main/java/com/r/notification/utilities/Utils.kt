package com.r.notification.utilities

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.Toast


object Utils {

    fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun hideKeyboard(mContext: Context) {
        val inputManager =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focus = (mContext as Activity).currentFocus
        if (focus != null) {
            inputManager.hideSoftInputFromWindow(
                focus.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    /* public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
*/
    fun isNetworkAvailable1(context: Context?): Boolean {
        var isNetAvailable = false
        if (context != null) {
            val mConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var mobileNetwork = false
            var wifiNetwork = false
            var mobileNetworkConnecetd = false
            var wifiNetworkConnecetd = false
            val mobileInfo =
                mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (mobileInfo != null) {
                mobileNetwork = mobileInfo.isAvailable
            }
            if (wifiInfo != null) {
                wifiNetwork = wifiInfo.isAvailable
            }
            if (wifiNetwork || mobileNetwork) {
                if (mobileInfo != null) mobileNetworkConnecetd =
                    mobileInfo.isConnectedOrConnecting
                wifiNetworkConnecetd = wifiInfo!!.isConnectedOrConnecting
            }
            isNetAvailable = mobileNetworkConnecetd || wifiNetworkConnecetd
        }
        return isNetAvailable
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } else {
            null
        }

        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        } else {
            return isNetworkAvailable1(context)
        }
        return false
    }
}