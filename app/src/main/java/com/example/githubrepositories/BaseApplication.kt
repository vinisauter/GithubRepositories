package com.example.githubrepositories

import android.util.Log
import androidx.multidex.MultiDexApplication


class BaseApplication : MultiDexApplication() {

    companion object {
        private lateinit var instance_: BaseApplication
        fun getInstance() = instance_
    }

    override fun onCreate() {
        super.onCreate()
        instance_ = this
        Log.i("BaseApplication", "BaseApplication.onCreate")
    }
}