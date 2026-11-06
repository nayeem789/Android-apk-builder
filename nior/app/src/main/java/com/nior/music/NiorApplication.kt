package com.nior.music

import android.app.Application
import com.nior.music.data.NiorDatabase

class NiorApplication : Application() {
    val database: NiorDatabase by lazy { NiorDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
