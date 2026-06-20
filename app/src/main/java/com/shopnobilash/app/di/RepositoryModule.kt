package com.shopnobilash.app.di

import com.shopnobilash.app.data.auth.repository.AuthRepository
import com.shopnobilash.app.data.auth.repository.AuthRepositoryImpl
import com.shopnobilash.app.data.notification.repository.NotificationRepository
import com.shopnobilash.app.data.notification.repository.NotificationRepositoryImpl
import com.shopnobilash.app.data.owner.repository.OwnerRepository
import com.shopnobilash.app.data.owner.repository.OwnerRepositoryImpl
import com.shopnobilash.app.data.profile.repository.ProfileRepository
import com.shopnobilash.app.data.profile.repository.ProfileRepositoryImpl
import com.shopnobilash.app.data.property.repository.PropertyRepository
import com.shopnobilash.app.data.property.repository.PropertyRepositoryImpl
import com.shopnobilash.app.data.storage.repository.StorageRepository
import com.shopnobilash.app.data.storage.repository.StorageRepositoryImpl
import com.shopnobilash.app.data.verification.repository.VerificationRepository
import com.shopnobilash.app.data.verification.repository.VerificationRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<PropertyRepository> { PropertyRepositoryImpl(get(), get()) }
    single<OwnerRepository> { OwnerRepositoryImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<StorageRepository> { StorageRepositoryImpl(get()) }
    single<VerificationRepository> { VerificationRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}
