package com.shopnobilash.app.di

import com.shopnobilash.app.data.auth.repository.AuthRepository
import com.shopnobilash.app.data.auth.repository.AuthRepositoryImpl
import com.shopnobilash.app.data.profile.repository.ProfileRepository
import com.shopnobilash.app.data.profile.repository.ProfileRepositoryImpl
import com.shopnobilash.app.data.property.repository.PropertyRepository
import com.shopnobilash.app.data.property.repository.PropertyRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<PropertyRepository> { PropertyRepositoryImpl() }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
}
