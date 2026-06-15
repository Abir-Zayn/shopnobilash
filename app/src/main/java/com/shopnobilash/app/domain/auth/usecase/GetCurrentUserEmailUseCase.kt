package com.shopnobilash.app.domain.auth.usecase

import com.shopnobilash.app.data.auth.repository.AuthRepository

class GetCurrentUserEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<String> = repository.getCurrentUserEmail()
}
