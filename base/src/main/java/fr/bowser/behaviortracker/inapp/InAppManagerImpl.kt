package fr.bowser.behaviortracker.inapp

import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener

class InAppManagerImpl(
    private val playBillingManager: PlayBillingManager,
    private val inAppConfiguration: InAppConfiguration,
    private val inAppRepository: InAppRepository
) : InAppManager {

    private val ownedSku = mutableListOf<String>()

    private val listeners: MutableList<InAppManager.Listener> = mutableListOf()

    override fun initialize() {
        playBillingManager.setUpPlayBilling()
        playBillingManager.setPlayBillingManagerListener(createPlayBillingManagerListener())

        querySkuDetailsAsync()
    }

    override fun purchase(
        skuDetails: SkuDetails,
        activityContainer: InAppManager.ActivityContainer
    ) {
        val purchaseFlowRequest = Runnable {
            val builder = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuDetails)
            val billingResult = playBillingManager.launchBillingFlow(
                activityContainer.get(),
                builder.build()
            )
            if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Log.e(TAG, "User already owns this in-app : ${skuDetails.sku}")
                notifyPurchaseFailed()
            } else if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(
                    TAG,
                    "An error occurred during purchase, error code : ${billingResult.responseCode}"
                )
                notifyPurchaseFailed()
            }
        }
        playBillingManager.executeServiceRequest(purchaseFlowRequest)
    }

    private fun querySkuDetailsAsync() {
        val queryRequest = Runnable {
            val skuList = getListOfAvailableSku()
            val inAppSkuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            playBillingManager.querySkuDetailsAsync(
                inAppSkuDetailsParams,
                createSkuDetailsResponseListener()
            )
        }
        playBillingManager.executeServiceRequest(queryRequest)
    }

    private fun updatePurchasedInApp() {
        val queryBoughtInApp = Runnable {
            // legacy in-apps
            val purchases = playBillingManager.queryPurchases(BillingClient.SkuType.INAPP)
            val purchasesInAppList = purchases.purchasesList!!
            for (purchase in purchasesInAppList) {
                ownedSku.add(purchase.sku)
            }
        }
        playBillingManager.executeServiceRequest(queryBoughtInApp)
    }

    private fun getListOfAvailableSku(): List<String> {
        val inApps = inAppConfiguration.getInApps()
        val list = mutableListOf<String>()
        for (inApp in inApps) {
            list.add(inApp.sku)
        }
        return list
    }

    private fun acknowledgeIfNeeded(purchases: List<Purchase>?) {
        if (purchases == null) {
            return
        }
        for (purchase in purchases) {
            acknowledgeIfNeeded(purchase)
        }
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            return
        }
        if (purchase.isAcknowledged) {
            return
        }
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val acknowledgePurchaseResponseListener =
            AcknowledgePurchaseResponseListener { billingResult ->
                val responseCode = billingResult.responseCode
                if (responseCode != BillingClient.BillingResponseCode.OK) {
                    notifyPurchaseFailed()
                }
            }
        playBillingManager.acknowledgePurchase(
            acknowledgePurchaseParams,
            acknowledgePurchaseResponseListener
        )
    }

    override fun addListener(listener: InAppManager.Listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    override fun removeListener(listener: InAppManager.Listener) {
        listeners.remove(listener)
    }

    private fun notifyPurchaseFailed() {
        for (listener in listeners) {
            listener.onPurchaseFailed()
        }
    }

    private fun notifyPurchaseSucceed(purchases: List<Purchase>) {
        for (listener in listeners) {
            listener.onPurchaseSucceed(purchases)
        }
    }

    private fun createPlayBillingManagerListener(): PlayBillingManager.Listener {
        return object : PlayBillingManager.Listener {
            override fun onPurchasesUpdated(purchases: List<Purchase>?) {
                acknowledgeIfNeeded(purchases)
                if (purchases != null) {
                    notifyPurchaseSucceed(purchases)
                }
            }

            override fun connectionToServiceFailed() {
                Log.e(TAG, "Connection to play store has failed.")
                notifyPurchaseFailed()
            }
        }
    }

    private fun createSkuDetailsResponseListener(): SkuDetailsResponseListener {
        return SkuDetailsResponseListener { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (skuDetailsList != null && skuDetailsList.size != 0) {
                    inAppRepository.set(skuDetailsList)
                }
                updatePurchasedInApp()
            }
        }
    }

    companion object {
        private const val TAG = "InAppManagerImpl"
    }
}
