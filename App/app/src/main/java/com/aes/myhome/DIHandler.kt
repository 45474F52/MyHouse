package com.aes.myhome

import android.app.Application
import android.content.res.Resources
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DIHandler : Application() {
    companion object {
        private lateinit var _instance: DIHandler
        private lateinit var _resources: Resources

        fun getInstance(): DIHandler {
            return _instance
        }

        fun getResources(): Resources {
            return _resources
        }
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        _resources = resources
    }
}