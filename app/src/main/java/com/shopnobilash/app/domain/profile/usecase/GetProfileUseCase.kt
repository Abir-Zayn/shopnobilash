package com.shopnobilash.app.domain.profile.usecase

import com.shopnobilash.app.data.profile.model.Profile
import com.shopnobilash.app.data.profile.repository.ProfileRepository

class GetProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String): Result<Profile?> = repository.getProfile(userId)
}
