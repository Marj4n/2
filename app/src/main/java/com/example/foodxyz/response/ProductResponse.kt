package com.example.foodxyz.response

data class ProductResponse(
    val status: Int?,
    val message: String?,
    val data: List<Product>?
)

data class Product(
    val id: Int?,
    val name: String?,
    val price: Int?,
    val image: String?,
    val rating: Double?
)
