package com.shopnobilash.app.di

import com.shopnobilash.app.data.repository.PropertyRepository
import com.shopnobilash.app.data.repository.PropertyRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<PropertyRepository> { PropertyRepositoryImpl() }
}
