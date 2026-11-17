package com.warehouse.manager.data

/** RFC-4180-style CSV output with quoting for commas, quotes and line breaks. */
object CsvFormatter {
    private fun escape(value: String): String =
        if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' })
            "\"${value.replace("\"", "\"\"")}\""
        else value

    fun products(products: List<Product>): String = buildString {
        appendLine("id,name,sku,barcode,category,price,quantity,minQuantity")
        products.forEach { p ->
            append(listOf(
                p.id.toString(), escape(p.name), escape(p.sku), escape(p.barcode),
                escape(p.category), p.price.toString(), p.quantity.toString(), p.minQuantity.toString()
            ).joinToString(","))
            append('\n')
        }
    }
}
