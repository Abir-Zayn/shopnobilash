package com.dorent.app

import android.app.Application
import com.dorent.app.di.appModule
import com.dorent.app.di.networkModule
import com.dorent.app.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DoRentApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DoRentApp)
            modules(networkModule, repositoryModule, appModule)
        }
    }
}
