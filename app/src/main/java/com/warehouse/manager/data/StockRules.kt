package com.warehouse.manager.data

/** Pure business rules shared by the repository and unit tests. */
object StockRules {
    fun validateProduct(product: Product) {
        require(product.name.isNotBlank()) { "Product name is required" }
        require(product.sku.isNotBlank()) { "SKU is required" }
        require(product.price.isFinite() && product.price >= 0.0) { "Price must be a valid non-negative number" }
        require(product.quantity >= 0 && product.minQuantity >= 0) {
            "Quantities cannot be negative"
        }
    }

    fun calculateNewQuantity(current: Int, type: String, amount: Int): Int {
        require(current >= 0) { "Current stock cannot be negative" }
        require(type == "IN" || type == "OUT") { "Operation must be IN or OUT" }
        require(amount > 0) { "Quantity must be greater than zero" }
        val result = if (type == "IN") Math.addExact(current, amount) else current - amount
        require(result >= 0) { "Not enough stock" }
        return result
    }
}
