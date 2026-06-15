package com.shopnobilash.app.di

import com.shopnobilash.app.constants.APPWRITE_PROJECT_ID
import com.shopnobilash.app.constants.APPWRITE_PUBLIC_ENDPOINT
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appwriteModule = module {
    single {
        Client(androidContext())
            .setEndpoint(APPWRITE_PUBLIC_ENDPOINT)
            .setProject(APPWRITE_PROJECT_ID)
    }
    single { Account(get()) }
    single { Databases(get()) }
}
