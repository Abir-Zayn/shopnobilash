package com.shopnobilash.app.di

import com.shopnobilash.app.domain.auth.usecase.CheckSessionUseCase
import com.shopnobilash.app.domain.auth.usecase.GetCurrentUserEmailUseCase
import com.shopnobilash.app.domain.auth.usecase.LoginUseCase
import com.shopnobilash.app.domain.auth.usecase.LoginWithOAuthUseCase
import com.shopnobilash.app.domain.auth.usecase.LogoutUseCase
import com.shopnobilash.app.domain.auth.usecase.ResendOtpUseCase
import com.shopnobilash.app.domain.auth.usecase.SignUpUseCase
import com.shopnobilash.app.domain.auth.usecase.VerifyOtpUseCase
import com.shopnobilash.app.domain.notification.usecase.GetNotificationsUseCase
import com.shopnobilash.app.domain.owner.usecase.CreateOwnerUseCase
import com.shopnobilash.app.domain.owner.usecase.ResolveOwnerUseCase
import com.shopnobilash.app.domain.profile.usecase.CreateProfileUseCase
import com.shopnobilash.app.domain.profile.usecase.GetProfileUseCase
import com.shopnobilash.app.domain.verification.usecase.GetVerificationStatusUseCase
import com.shopnobilash.app.domain.verification.usecase.SubmitVerificationUseCase
import com.shopnobilash.app.domain.property.usecase.CreatePropertyUseCase
import com.shopnobilash.app.domain.property.usecase.GetListingsUseCase
import com.shopnobilash.app.domain.property.usecase.GetPropertyByIdUseCase
import com.shopnobilash.app.domain.property.usecase.GetSavedPropertyIdsUseCase
import com.shopnobilash.app.domain.property.usecase.SearchPropertiesUseCase
import com.shopnobilash.app.domain.property.usecase.ToggleSavePropertyUseCase
import com.shopnobilash.app.presentation.auth.viewmodel.AuthViewModel
import com.shopnobilash.app.presentation.chat.viewmodel.ChatViewModel
import com.shopnobilash.app.presentation.checkout.viewmodel.CheckoutViewModel
import com.shopnobilash.app.presentation.detail.viewmodel.DetailViewModel
import com.shopnobilash.app.presentation.home.viewmodel.HomeViewModel
import com.shopnobilash.app.presentation.listing.viewmodel.ListingViewModel
import com.shopnobilash.app.presentation.notifications.viewmodel.NotificationsViewModel
import com.shopnobilash.app.presentation.owner.viewmodel.OwnerDashboardViewModel
import com.shopnobilash.app.presentation.profile.viewmodel.ProfileViewModel
import com.shopnobilash.app.presentation.profile_setup.viewmodel.ProfileSetupViewModel
import com.shopnobilash.app.presentation.search.viewmodel.SearchViewModel
import com.shopnobilash.app.presentation.verification.viewmodel.VerificationViewModel
import com.shopnobilash.app.presentation.wishlist.viewmodel.WishlistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val useCaseModule = module {
    // Auth
    factory { CheckSessionUseCase(get()) }
    factory { GetCurrentUserEmailUseCase(get()) }
    factory { SignUpUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LoginWithOAuthUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    factory { ResendOtpUseCase(get()) }

    // Profile
    factory { GetProfileUseCase(get()) }
    factory { CreateProfileUseCase(get()) }

    // Verification
    factory { GetVerificationStatusUseCase(get()) }
    factory { SubmitVerificationUseCase(get()) }

    // Notifications
    factory { GetNotificationsUseCase(get()) }

    // Property
    factory { GetListingsUseCase(get()) }
    factory { GetPropertyByIdUseCase(get()) }
    factory { ToggleSavePropertyUseCase(get()) }
    factory { GetSavedPropertyIdsUseCase(get()) }
    factory { CreatePropertyUseCase(get()) }
    factory { SearchPropertiesUseCase(get()) }

    // Owner
    factory { ResolveOwnerUseCase(get()) }
    factory { CreateOwnerUseCase(get()) }
}

val appModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ListingViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::WishlistViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::ProfileSetupViewModel)
    viewModel { (propertyId: String) -> DetailViewModel(propertyId, get(), get(), get()) }
    viewModel { (propertyId: String) -> CheckoutViewModel(propertyId, get()) }
    viewModel { (propertyId: String?) -> ChatViewModel(get(), propertyId) }
    viewModelOf(::ProfileViewModel)
    viewModelOf(::VerificationViewModel)
    viewModelOf(::OwnerDashboardViewModel)
}
