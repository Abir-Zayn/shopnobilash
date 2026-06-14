package com.shopnobilash.app

import android.app.Application
import android.util.Log
import com.shopnobilash.app.di.appModule
import com.shopnobilash.app.di.appwriteModule
import com.shopnobilash.app.di.networkModule
import com.shopnobilash.app.di.repositoryModule
import io.appwrite.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class ShopnobilashApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ShopnobilashApp)
            modules(networkModule, repositoryModule, appModule, appwriteModule)
        }
        pingAppwrite()
    }

    private fun pingAppwrite() {
        val client: Client by inject()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.ping()
                Log.d("Appwrite", "Ping successful")
            } catch (e: Exception) {
                Log.e("Appwrite", "Ping failed: ${e.message}")
            }
        }
    }
}
