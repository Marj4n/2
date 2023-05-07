package com.example.foodxyz.model

data class Product(
    var id: String?,
    val image: String?,
    val price: Long?,
    val productName: String?,
    val rating: Float?,
    val stock: Long?
) {
    constructor() : this(null, null, 0, null, 0.0f, 0L)
}
