package com.warehouse.manager.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "products", indices = [Index(value = ["sku"], unique = true)])
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String,
    val barcode: String = "",
    val category: String = "General",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val minQuantity: Int = 0
)

@Entity(tableName = "movements")
data class StockMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String,
    val quantity: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Dao
interface WarehouseDao {
    @Query("SELECT * FROM products ORDER BY name COLLATE NOCASE")
    fun observeProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProduct(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM movements ORDER BY createdAt DESC")
    fun observeMovements(): Flow<List<StockMovement>>

    @Insert
    suspend fun insertMovement(movement: StockMovement)

    @Query("SELECT COUNT(*) FROM products")
    fun observeProductCount(): Flow<Int>
}

@Database(entities = [Product::class, StockMovement::class], version = 1, exportSchema = false)
abstract class WarehouseDatabase : RoomDatabase() {
    abstract fun dao(): WarehouseDao

    companion object {
        @Volatile private var INSTANCE: WarehouseDatabase? = null
        fun get(context: Context): WarehouseDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WarehouseDatabase::class.java,
                    "warehouse.db"
                ).build().also { INSTANCE = it }
            }
    }
}
