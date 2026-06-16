package com.shopnobilash.app.data.verification.model

data class Verification(
    val id: String,
    val userId: String,
    val documentType: String,
    val fileId: String,
    val verificationStatus: String,
    val rejectReason: String? = null,
    val reviewedAt: String? = null,
    val reviewedBy: String? = null,
    val createdAt: String,
)

enum class DocumentType(val label: String, val value: String) {
    NID("National ID (NID)", "nid"),
    PASSPORT("Passport", "passport"),
    BIRTH_CERTIFICATE("Birth Certificate", "birth_certificate"),
}
