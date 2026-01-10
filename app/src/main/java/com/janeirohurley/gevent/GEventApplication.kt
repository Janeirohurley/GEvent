package com.janeirohurley.gevent

import android.app.Application
import com.janeirohurley.gevent.utils.TokenManager

class GEventApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialiser le TokenManager
        TokenManager.init(this)
    }
}
