package com.warehouse.manager.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

class WarehouseRepository(private val db: WarehouseDatabase) {
    private val dao = db.dao()
    val products: Flow<List<Product>> = dao.observeProducts()
    val movements: Flow<List<StockMovement>> = dao.observeMovements()
    val productCount: Flow<Int> = dao.observeProductCount()

    suspend fun saveProduct(product: Product) {
        StockRules.validateProduct(product)
        if (product.id == 0L) dao.insertProduct(product) else dao.updateProduct(product)
    }

    suspend fun removeProduct(product: Product) = dao.deleteProduct(product)

    suspend fun moveStock(productId: Long, type: String, amount: Int, note: String) {
        require(type == "IN" || type == "OUT")
        require(amount > 0) { "Quantity must be greater than zero" }
        db.withTransaction {
            val product = dao.getProduct(productId) ?: error("Product not found")
            val newQty = if (type == "IN") product.quantity + amount else product.quantity - amount
            require(newQty >= 0) { "Not enough stock" }
            dao.updateProduct(product.copy(quantity = newQty))
            dao.insertMovement(
                StockMovement(
                    productId = product.id,
                    productName = product.name,
                    type = type,
                    quantity = amount,
                    note = note
                )
            )
        }
    }
}
