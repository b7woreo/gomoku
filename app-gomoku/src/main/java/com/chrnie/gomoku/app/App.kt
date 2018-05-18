package com.chrnie.gomoku.app

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

class App : Application(){

    companion object {
        private const val BUGLY_ID = "e512fcc35e"
    }

    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(applicationContext, BUGLY_ID, BuildConfig.DEBUG)
    }
}