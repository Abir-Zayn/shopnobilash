package com.shopnobilash.app.di

import com.shopnobilash.app.ui.feature.auth.AuthViewModel
import com.shopnobilash.app.ui.feature.chat.ChatViewModel
import com.shopnobilash.app.ui.feature.checkout.CheckoutViewModel
import com.shopnobilash.app.ui.feature.detail.DetailViewModel
import com.shopnobilash.app.ui.feature.home.HomeViewModel
import com.shopnobilash.app.ui.feature.listing.ListingViewModel
import com.shopnobilash.app.ui.feature.notifications.NotificationsViewModel
import com.shopnobilash.app.ui.feature.profile.ProfileViewModel
import com.shopnobilash.app.ui.feature.wishlist.WishlistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ListingViewModel)
    viewModelOf(::WishlistViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModel { (propertyId: String) -> DetailViewModel(propertyId, get()) }
    viewModel { (propertyId: String) -> CheckoutViewModel(propertyId, get()) }
    viewModel { (propertyId: String?) -> ChatViewModel(get(), propertyId) }
    viewModelOf(::ProfileViewModel)
}
