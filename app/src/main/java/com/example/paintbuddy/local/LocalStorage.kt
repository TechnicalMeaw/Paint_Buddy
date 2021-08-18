package com.example.paintbuddy.local

import android.content.SharedPreferences

class LocalStorage {

    companion object {
        var sharedPref: SharedPreferences? = null

        var status: String?
            get() {
                return sharedPref?.getString("status", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("status", value)?.apply()
            }
    }
}