package com.example.foodxyz.request

data class TransactionRequest(
    val qty_total: Int,
    val price_total: Int,
    val items: Array<TransactionItems>
)

data class TransactionItems(
    val product_id: Int,
    val qty: Int,
    val subtotal: Int
)
