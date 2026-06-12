package com.dorent.app.di

import com.dorent.app.ui.feature.chat.ChatViewModel
import com.dorent.app.ui.feature.checkout.CheckoutViewModel
import com.dorent.app.ui.feature.detail.DetailViewModel
import com.dorent.app.ui.feature.home.HomeViewModel
import com.dorent.app.ui.feature.listing.ListingViewModel
import com.dorent.app.ui.feature.notifications.NotificationsViewModel
import com.dorent.app.ui.feature.profile.ProfileViewModel
import com.dorent.app.ui.feature.wishlist.WishlistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ListingViewModel)
    viewModelOf(::WishlistViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModel { (propertyId: String) -> DetailViewModel(propertyId, get()) }
    viewModel { (propertyId: String) -> CheckoutViewModel(propertyId, get()) }
    viewModel { (propertyId: String?) -> ChatViewModel(get(), propertyId) }
    viewModelOf(::ProfileViewModel)
}
