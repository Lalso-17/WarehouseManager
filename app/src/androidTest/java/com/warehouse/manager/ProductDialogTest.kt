package com.warehouse.manager

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.warehouse.manager.data.Product
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProductDialogTest {
    @get:Rule val compose = createComposeRule()

    @Test fun emptyRequiredFieldsShowValidationMessage() {
        compose.setContent { ProductDialog(initial = null, onDismiss = {}, onSave = {}) }
        compose.onNodeWithText("Save").performClick()
        compose.onNodeWithText("Name and SKU are required").assertIsDisplayed()
    }

    @Test fun validProductCanBeSubmitted() {
        var saved: Product? = null
        compose.setContent { ProductDialog(initial = null, onDismiss = {}, onSave = { saved = it }) }
        compose.onNodeWithText("Name *").performTextInput("Test product")
        compose.onNodeWithText("SKU *").performTextInput("TEST-001")
        compose.onNodeWithText("Save").performClick()
        compose.runOnIdle {
            assertEquals("Test product", saved?.name)
            assertEquals("TEST-001", saved?.sku)
        }
    }
}
