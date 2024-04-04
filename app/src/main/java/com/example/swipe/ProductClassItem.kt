package com.example.swipe

import com.google.gson.annotations.SerializedName

data class ProductClassItem(
    val image: String?,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double
)


data class AddProductResponse(
    val message: String,
    @SerializedName("product_details") val productDetails: ProductClassItem?,
    @SerializedName("product_id") val productId: Int,
    val success: Boolean
)