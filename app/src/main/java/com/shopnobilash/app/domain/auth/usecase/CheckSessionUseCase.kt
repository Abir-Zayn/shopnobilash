package com.shopnobilash.app.domain.auth.usecase

import com.shopnobilash.app.data.auth.repository.AuthRepository

class CheckSessionUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<String> = repository.checkSession()
}
