package com.shopnobilash.app.presentation.owner.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.owner.model.Owner
import com.shopnobilash.app.data.owner.model.OwnerResult
import com.shopnobilash.app.data.profile.model.Profile
import com.shopnobilash.app.data.property.model.PropertyDraft
import com.shopnobilash.app.data.storage.repository.StorageRepository
import com.shopnobilash.app.domain.auth.usecase.CheckSessionUseCase
import com.shopnobilash.app.domain.owner.usecase.CreateOwnerUseCase
import com.shopnobilash.app.domain.owner.usecase.ResolveOwnerUseCase
import com.shopnobilash.app.domain.profile.usecase.GetProfileUseCase
import com.shopnobilash.app.domain.property.usecase.CreatePropertyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OwnerStep(val index: Int) {
    Verify(0),
    OwnerInfo(1),
    PropertyForm(2),
    Done(3),
}

data class CreatedProperty(
    val id: String,
    val houseName: String,
    val rent: Double,
    val areaSqft: Int,
    val bedNo: Int,
    val firstImageUrl: String?,
)

data class OwnerDashboardState(
    val step: OwnerStep = OwnerStep.Verify,
    val isLoading: Boolean = true,
    val isVerified: Boolean = false,
    val profile: Profile? = null,
    val owner: Owner? = null,
    val isSubmittingOwner: Boolean = false,
    val tinError: String? = null,
    val isSubmittingProperty: Boolean = false,
    val uploadTotal: Int = 0,
    val uploadDone: Int = 0,
    val createdProperty: CreatedProperty? = null,
    val error: String? = null,
    val sessionExpired: Boolean = false,
)

class OwnerDashboardViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val resolveOwnerUseCase: ResolveOwnerUseCase,
    private val createOwnerUseCase: CreateOwnerUseCase,
    private val createPropertyUseCase: CreatePropertyUseCase,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(OwnerDashboardState())
    val state: StateFlow<OwnerDashboardState> = _state.asStateFlow()

    private var userId: String = ""

    init { onEnter() }

    /** STEP 1 — read profile, gate on verification, resolve/skip to the right step. */
    fun onEnter() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            checkSessionUseCase().fold(
                onSuccess = { id ->
                    userId = id
                    loadGate()
                },
                onFailure = {
                    _state.update { it.copy(isLoading = false, sessionExpired = true) }
                },
            )
        }
    }

    /** Re-fetch the profile row (Refresh / pull-to-refresh on the gate). */
    fun refresh() = onEnter()

    private suspend fun loadGate() {
        getProfileUseCase(userId).fold(
            onSuccess = { profile ->
                val verified = profile?.isVerified == true
                if (!verified) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            isVerified = false,
                            step = OwnerStep.Verify,
                        )
                    }
                    return
                }
                // Verified — resolve owner; existing owner skips Step 2.
                resolveOwnerUseCase(userId).fold(
                    onSuccess = { owner ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                profile = profile,
                                isVerified = true,
                                owner = owner,
                                step = if (owner != null) OwnerStep.PropertyForm else OwnerStep.OwnerInfo,
                            )
                        }
                    },
                    onFailure = { e ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                profile = profile,
                                isVerified = true,
                                step = OwnerStep.OwnerInfo,
                                error = e.message,
                            )
                        }
                    },
                )
            },
            onFailure = { e ->
                _state.update { it.copy(isLoading = false, error = e.message ?: "Could not load profile") }
            },
        )
    }

    /**
     * STEP 2 — create the owner row. [newProfilePicUri] (if any) is uploaded first;
     * otherwise [prefilledProfilePicUrl] is reused.
     */
    fun submitOwner(
        name: String,
        addressLine1: String,
        addressLine2: String?,
        tin: String?,
        prefilledProfilePicUrl: String?,
        newProfilePicUri: Uri?,
        context: Context,
    ) {
        if (userId.isBlank()) {
            _state.update { it.copy(sessionExpired = true) }
            return
        }
        _state.update { it.copy(isSubmittingOwner = true, tinError = null, error = null) }
        viewModelScope.launch {
            val profilePicUrl = if (newProfilePicUri != null) {
                val result = uploadImage(newProfilePicUri, context) { bytes, mime ->
                    storageRepository.uploadProfilePicture(userId, bytes, mime)
                        .map { storageRepository.getProfilePictureUrl(it) }
                }
                result.getOrElse { e ->
                    _state.update {
                        it.copy(
                            isSubmittingOwner = false,
                            error = e.message?.let { m -> "Could not upload profile picture: $m" }
                                ?: "Could not upload profile picture",
                        )
                    }
                    return@launch
                }
            } else {
                prefilledProfilePicUrl
            }

            when (val result = createOwnerUseCase(userId, name, addressLine1, addressLine2, tin, profilePicUrl)) {
                is OwnerResult.Created -> advanceToProperty(result.owner)
                is OwnerResult.AlreadyExists -> advanceToProperty(result.owner)
                OwnerResult.DuplicateTin ->
                    _state.update { it.copy(isSubmittingOwner = false, tinError = "This TIN is already registered") }
                is OwnerResult.Error ->
                    _state.update { it.copy(isSubmittingOwner = false, error = result.message) }
            }
        }
    }

    private fun advanceToProperty(owner: Owner) {
        _state.update {
            it.copy(isSubmittingOwner = false, owner = owner, step = OwnerStep.PropertyForm, tinError = null)
        }
    }

    /** STEP 3 — upload photos, then create the property + junction row. */
    fun submitProperty(draft: PropertyDraft, imageUris: List<Uri>, context: Context) {
        val owner = _state.value.owner
        if (owner == null) {
            _state.update { it.copy(error = "Owner not resolved. Please retry.") }
            return
        }
        _state.update {
            it.copy(isSubmittingProperty = true, error = null, uploadTotal = imageUris.size, uploadDone = 0)
        }
        viewModelScope.launch {
            val urls = mutableListOf<String>()
            // Sequential upload keeps us well under rate limits and gives per-image progress.
            for (uri in imageUris) {
                val result = uploadImage(uri, context) { bytes, mime ->
                    storageRepository.uploadPropertyImage(userId, bytes, mime)
                        .map { storageRepository.getPropertyImageUrl(it) }
                }
                val url = result.getOrElse { e ->
                    _state.update {
                        it.copy(
                            isSubmittingProperty = false,
                            error = e.message?.let { m -> "Image upload failed: $m" }
                                ?: "Image upload failed. Please retry.",
                        )
                    }
                    return@launch
                }
                urls += url
                _state.update { it.copy(uploadDone = it.uploadDone + 1) }
            }

            createPropertyUseCase(userId, owner.id, draft, urls).fold(
                onSuccess = { propertyId ->
                    _state.update {
                        it.copy(
                            isSubmittingProperty = false,
                            step = OwnerStep.Done,
                            createdProperty = CreatedProperty(
                                id = propertyId,
                                houseName = draft.houseName,
                                rent = draft.rent,
                                areaSqft = draft.areaSqft,
                                bedNo = draft.bedNo,
                                firstImageUrl = urls.firstOrNull(),
                            ),
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isSubmittingProperty = false, error = e.message ?: "Could not create listing")
                    }
                },
            )
        }
    }

    /** Reset Step 3 form to list another property (PropertyCreatedScreen → Add Another). */
    fun addAnother() {
        _state.update {
            it.copy(step = OwnerStep.PropertyForm, createdProperty = null, uploadTotal = 0, uploadDone = 0)
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private suspend fun uploadImage(
        uri: Uri,
        context: Context,
        upload: suspend (ByteArray, String) -> Result<String>,
    ): Result<String> {
        val bytes = runCatching {
            context.contentResolver.openInputStream(uri)?.readBytes()
        }.getOrNull() ?: return Result.failure(IllegalStateException("Could not read the selected image"))
        val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
        return upload(bytes, mime)
    }
}
