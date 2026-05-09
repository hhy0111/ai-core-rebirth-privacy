package com.harnessgames.aicore.monetization

import android.content.Context

class EntitlementRepository(context: Context) {
    private val prefs = context.getSharedPreferences("ai_core_entitlements", Context.MODE_PRIVATE)

    fun ownedProductIds(): Set<String> = prefs.getStringSet(KEY_OWNED_PRODUCTS, emptySet()) ?: emptySet()

    fun hasProduct(product: StoreProduct): Boolean = ownedProductIds().contains(product.productId)

    fun markProductOwned(product: StoreProduct) {
        val updated = ownedProductIds() + product.productId
        prefs.edit().putStringSet(KEY_OWNED_PRODUCTS, updated).apply()
    }

    fun isTokenProcessed(token: String): Boolean = processedTokens().contains(token)

    fun markTokenProcessed(token: String) {
        val updated = (processedTokens() + token).toList().takeLast(MAX_STORED_TOKENS).toSet()
        prefs.edit().putStringSet(KEY_PROCESSED_TOKENS, updated).apply()
    }

    private fun processedTokens(): Set<String> =
        prefs.getStringSet(KEY_PROCESSED_TOKENS, emptySet()) ?: emptySet()

    companion object {
        private const val KEY_OWNED_PRODUCTS = "owned_product_ids"
        private const val KEY_PROCESSED_TOKENS = "processed_purchase_tokens"
        private const val MAX_STORED_TOKENS = 120
    }
}
