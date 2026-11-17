package com.warehouse.manager.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class StockRulesTest {
    @Test fun receiveIncreasesStock() {
        assertEquals(20, StockRules.calculateNewQuantity(15, "IN", 5))
    }

    @Test fun issueDecreasesStock() {
        assertEquals(12, StockRules.calculateNewQuantity(15, "OUT", 3))
    }

    @Test fun issueCannotMakeStockNegative() {
        assertThrows(IllegalArgumentException::class.java) {
            StockRules.calculateNewQuantity(2, "OUT", 3)
        }
    }

    @Test fun quantityMustBePositive() {
        assertThrows(IllegalArgumentException::class.java) {
            StockRules.calculateNewQuantity(2, "IN", 0)
        }
    }

    @Test fun operationTypeMustBeKnown() {
        assertThrows(IllegalArgumentException::class.java) {
            StockRules.calculateNewQuantity(2, "TRANSFER", 1)
        }
    }

    @Test fun productRequiresNameAndSku() {
        val product = Product(name = " ", sku = "", price = 1.0)
        assertThrows(IllegalArgumentException::class.java) { StockRules.validateProduct(product) }
    }

    @Test fun productRejectsNonFinitePrice() {
        val product = Product(name = "Test", sku = "T-1", price = Double.NaN)
        assertThrows(IllegalArgumentException::class.java) { StockRules.validateProduct(product) }
    }
}
