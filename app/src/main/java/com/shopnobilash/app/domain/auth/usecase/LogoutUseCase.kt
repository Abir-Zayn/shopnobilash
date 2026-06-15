package com.shopnobilash.app.domain.auth.usecase

import com.shopnobilash.app.data.auth.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
