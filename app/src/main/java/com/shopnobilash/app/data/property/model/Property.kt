package com.shopnobilash.app.data.property.model

data class Property(
    val id: String,
    val title: String,
    val type: String,
    val city: String,
    val address: String,
    val price: Int,
    val period: String,
    val beds: Int,
    val baths: Int,
    val sqft: Int,
    val rating: Double,
    val ownerName: String,
    val ownerRole: String,
    val description: String,
    val imageUrl: String,
    val imageUrls: List<String> = emptyList(),
)

fun formatPrice(price: Int): String = "\$${"%,d".format(price)}"
