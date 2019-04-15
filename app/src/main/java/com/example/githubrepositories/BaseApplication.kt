package com.example.githubrepositories

import android.util.Log
import androidx.multidex.MultiDexApplication


class BaseApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Log.i("BaseApplication", "BaseApplication.onCreate")
    }

}