package com.dorent.app.di

import com.dorent.app.data.repository.PropertyRepository
import com.dorent.app.data.repository.PropertyRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<PropertyRepository> { PropertyRepositoryImpl() }
}
