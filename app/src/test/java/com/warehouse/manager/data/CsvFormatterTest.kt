package com.warehouse.manager.data

import org.junit.Assert.assertTrue
import org.junit.Test

class CsvFormatterTest {
    @Test fun escapesCommaQuotesAndLineBreaks() {
        val csv = CsvFormatter.products(listOf(Product(name = "Tea, green", sku = "SKU-\"1\"", category = "Hot\nDrinks")))
        assertTrue(csv.contains("\"Tea, green\""))
        assertTrue(csv.contains("\"SKU-\"\"1\"\"\""))
        assertTrue(csv.contains("\"Hot\nDrinks\""))
    }
}
