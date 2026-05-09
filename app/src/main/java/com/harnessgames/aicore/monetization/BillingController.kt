package com.harnessgames.aicore.monetization

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.harnessgames.aicore.game.ResourceWallet

enum class StoreProduct(
    val productId: String,
    val title: String,
    val designIntent: String,
    val fallbackPrice: String,
    val purchaseKind: PurchaseKind,
    val reward: ResourceWallet = ResourceWallet(),
    val progressReward: Double = 0.0,
    val productionBoostSeconds: Long = 0L,
) {
    REMOVE_ADS(
        productId = "remove_ads",
        title = "광고 제거",
        designIntent = "향후 강제 광고 제거, 보상형 광고는 선택 유지",
        fallbackPrice = "₩3,900",
        purchaseKind = PurchaseKind.NON_CONSUMABLE,
    ),
    STARTER_PACK(
        productId = "starter_pack",
        title = "스타터 패키지",
        designIntent = "초반 성장용 1회 자원 패키지",
        fallbackPrice = "₩2,900",
        purchaseKind = PurchaseKind.NON_CONSUMABLE,
        reward = ResourceWallet(energy = 1_200.0, biomass = 45.0, aiData = 18.0),
        progressReward = 160.0,
    ),
    AI_UPGRADE_PACK(
        productId = "ai_upgrade_pack",
        title = "AI 업그레이드 패키지",
        designIntent = "AI 외형 권한과 데이터 보너스",
        fallbackPrice = "₩5,500",
        purchaseKind = PurchaseKind.NON_CONSUMABLE,
        reward = ResourceWallet(aiData = 80.0),
    ),
    PRODUCTION_PACK(
        productId = "production_pack",
        title = "생산 가속 패키지",
        designIntent = "반복 구매 가능한 시간 단축 보상",
        fallbackPrice = "₩1,500",
        purchaseKind = PurchaseKind.CONSUMABLE,
        reward = ResourceWallet(energy = 2_500.0, biomass = 80.0, aiData = 30.0),
        productionBoostSeconds = 60L * 60L,
    ),
    PLANET_SKIN_PACK(
        productId = "planet_skin_pack",
        title = "행성 스킨 패키지",
        designIntent = "행성 외형 변경 권한",
        fallbackPrice = "₩3,900",
        purchaseKind = PurchaseKind.NON_CONSUMABLE,
    ),
    GALAXY_PASS(
        productId = "premium_galaxy_pass",
        title = "프리미엄 은하 패스",
        designIntent = "외형과 편의 보상",
        fallbackPrice = "₩6,900",
        purchaseKind = PurchaseKind.NON_CONSUMABLE,
        reward = ResourceWallet(energy = 4_000.0, biomass = 120.0, aiData = 70.0),
    ),
}

enum class PurchaseKind {
    CONSUMABLE,
    NON_CONSUMABLE,
}

interface BillingListener {
    fun onBillingCatalogUpdated()
    fun onPurchaseGranted(product: StoreProduct)
    fun onBillingMessage(message: String)
}

class BillingController(
    private val activity: Activity,
    private val entitlements: EntitlementRepository,
) : PurchasesUpdatedListener {
    val products: List<StoreProduct> = StoreProduct.entries
    var listener: BillingListener? = null

    private val productDetails = mutableMapOf<String, ProductDetails>()
    private var isConnecting = false

    private val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .enableAutoServiceReconnection()
        .build()

    fun start() {
        if (billingClient.isReady || isConnecting) return
        isConnecting = true
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                isConnecting = false
                if (result.responseCode == BillingResponseCode.OK) {
                    queryProducts()
                    restorePurchases()
                } else {
                    notifyMessage("결제를 사용할 수 없습니다: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnecting = false
                notifyMessage("결제 서비스 연결이 끊겼습니다. 필요 시 다시 연결됩니다.")
            }
        })
    }

    fun stop() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    fun isPurchaseAvailable(): Boolean = billingClient.isReady

    fun hasEntitlement(product: StoreProduct): Boolean = entitlements.hasProduct(product)

    fun priceLabel(product: StoreProduct): String {
        val details = productDetails[product.productId] ?: return product.fallbackPrice
        val offer = details.oneTimePurchaseOfferDetailsList?.firstOrNull()
            ?: details.oneTimePurchaseOfferDetails
        return offer?.formattedPrice ?: product.fallbackPrice
    }

    fun purchase(product: StoreProduct) {
        if (product.purchaseKind == PurchaseKind.NON_CONSUMABLE && entitlements.hasProduct(product)) {
            notifyMessage("${product.title}은 이미 보유 중입니다.")
            return
        }
        if (!billingClient.isReady) {
            start()
            notifyMessage("결제 서비스 연결 중입니다. 잠시 후 다시 시도하세요.")
            return
        }

        val details = productDetails[product.productId]
        if (details == null) {
            queryProducts()
            notifyMessage("${product.title}이 Play Console에 아직 등록되지 않았습니다.")
            return
        }

        val detailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
        val offer = details.oneTimePurchaseOfferDetailsList?.firstOrNull()
            ?: details.oneTimePurchaseOfferDetails
        offer?.offerToken?.let { detailsParamsBuilder.setOfferToken(it) }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(detailsParamsBuilder.build()))
            .build()
        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingResponseCode.OK) {
            notifyMessage("구매를 시작할 수 없습니다: ${result.debugMessage}")
        }
    }

    fun restorePurchases() {
        if (!billingClient.isReady) {
            start()
            return
        }
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode == BillingResponseCode.OK) {
                processPurchases(purchases, fromRestore = true)
            } else {
                notifyMessage("구매 복원 실패: ${result.debugMessage}")
            }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingResponseCode.OK -> processPurchases(purchases.orEmpty(), fromRestore = false)
            BillingResponseCode.USER_CANCELED -> notifyMessage("구매가 취소되었습니다.")
            BillingResponseCode.ITEM_ALREADY_OWNED -> restorePurchases()
            else -> notifyMessage("구매 실패: ${result.debugMessage}")
        }
    }

    private fun queryProducts() {
        val queryProducts = products.map { product ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(product.productId)
                .setProductType(ProductType.INAPP)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(queryProducts)
            .build()
        billingClient.queryProductDetailsAsync(params) { result, detailsResult ->
            if (result.responseCode == BillingResponseCode.OK) {
                productDetails.clear()
                detailsResult.productDetailsList.forEach { details ->
                    productDetails[details.productId] = details
                }
                listener?.onBillingCatalogUpdated()
            } else {
                notifyMessage("상품 조회 실패: ${result.debugMessage}")
            }
        }
    }

    private fun processPurchases(purchases: List<Purchase>, fromRestore: Boolean) {
        purchases.forEach { purchase ->
            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> processPurchased(purchase, fromRestore)
                Purchase.PurchaseState.PENDING -> notifyMessage("결제가 대기 중입니다. 결제 완료 후 보상이 지급됩니다.")
                else -> Unit
            }
        }
    }

    private fun processPurchased(purchase: Purchase, fromRestore: Boolean) {
        purchase.products.forEach { productId ->
            val product = products.firstOrNull { it.productId == productId } ?: return@forEach
            if (product.purchaseKind == PurchaseKind.CONSUMABLE) {
                consumePurchase(product, purchase)
            } else {
                grantNonConsumable(product, purchase, fromRestore)
            }
        }
    }

    private fun consumePurchase(product: StoreProduct, purchase: Purchase) {
        if (entitlements.isTokenProcessed(purchase.purchaseToken)) return
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(params) { result, token ->
            if (result.responseCode == BillingResponseCode.OK) {
                if (!entitlements.isTokenProcessed(token)) {
                    entitlements.markTokenProcessed(token)
                    listener?.onPurchaseGranted(product)
                }
            } else {
                notifyMessage("구매 보상 지급 지연: ${result.debugMessage}")
            }
        }
    }

    private fun grantNonConsumable(product: StoreProduct, purchase: Purchase, fromRestore: Boolean) {
        val wasOwned = entitlements.hasProduct(product)
        if (!wasOwned) {
            entitlements.markProductOwned(product)
            listener?.onPurchaseGranted(product)
        } else if (!fromRestore) {
            notifyMessage("${product.title}은 이미 보유 중입니다.")
        }

        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { result ->
                if (result.responseCode != BillingResponseCode.OK) {
                    notifyMessage("구매 승인 지연: ${result.debugMessage}")
                }
            }
        }
    }

    private fun notifyMessage(message: String) {
        listener?.onBillingMessage(message)
    }
}
