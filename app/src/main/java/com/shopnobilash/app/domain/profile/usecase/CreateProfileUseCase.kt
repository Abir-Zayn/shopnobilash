package com.shopnobilash.app.domain.profile.usecase

import com.shopnobilash.app.data.profile.model.Profile
import com.shopnobilash.app.data.profile.repository.ProfileRepository

class CreateProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile): Result<Unit> = repository.createProfile(profile)
}
