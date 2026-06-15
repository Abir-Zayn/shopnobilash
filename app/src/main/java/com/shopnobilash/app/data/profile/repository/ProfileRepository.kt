package com.shopnobilash.app.data.profile.repository

import com.shopnobilash.app.data.profile.model.Profile

interface ProfileRepository {
    suspend fun createProfile(profile: Profile): Result<Unit>
    suspend fun getProfile(userId: String): Result<Profile?>
}
