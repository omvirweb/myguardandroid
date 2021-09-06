package com.r.notification.utilities

import android.content.Context
import android.content.SharedPreferences

class SessionManager {
    fun getPrefData(prefName: String?, context: Context): String? {
        val prefValue: String?
        val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        prefValue = pref.getString(prefName, "")
        return prefValue
    }

    fun setPrefData(prefName: String?, value: String?, context: Context?) {
        if (context != null) {
            val editor: SharedPreferences.Editor
            val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            editor = pref.edit()
            editor.putString(prefName, value)
            editor.apply()
        }
    }

    fun getPrefIntData(prefName: String?, context: Context): Int? {
        val prefValue: Int
        val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        prefValue = pref.getInt(prefName, 0)
        return prefValue
    }

    fun setPrefIntData(prefName: String?, value: Int?, context: Context) {
        val editor: SharedPreferences.Editor
        val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        editor = pref.edit()
        editor.putInt(prefName, value!!)
        editor.apply()
    }

    fun setBooleanPrefData(prefName: String?, value: Boolean, context: Context) {
        val editor: SharedPreferences.Editor
        val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        editor = pref.edit()
        editor.putBoolean(prefName, value)
        editor.apply()
    }

    fun getBooleanPrefData(prefName: String?, context: Context): Boolean {
        val prefValue: Boolean
        val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        prefValue = pref.getBoolean(prefName, false)
        return prefValue
    }
}