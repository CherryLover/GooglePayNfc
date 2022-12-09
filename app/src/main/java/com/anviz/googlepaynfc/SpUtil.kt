package com.anviz.googlepaynfc

import android.content.Context

fun Context.getSpBoolean(key: String, defValue: Boolean = false): Boolean {
  return getSp(this).getBoolean(key, defValue)
}

fun Context.putSpBoolean(key: String, value: Boolean) {
  getSp(this).edit().putBoolean(key, value).apply()
}

private fun getSp(context: Context): android.content.SharedPreferences {
  return context.getSharedPreferences("sp", Context.MODE_PRIVATE)
}