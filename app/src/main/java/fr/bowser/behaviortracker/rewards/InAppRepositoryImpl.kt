package fr.bowser.behaviortracker.rewards

import android.content.SharedPreferences
import android.support.annotation.VisibleForTesting
import com.android.billingclient.api.SkuDetails
import java.util.*

class InAppRepositoryImpl(private val sharedPreferences: SharedPreferences,
                          private val inAppConfiguration: InAppConfiguration) : InappRepository {

    private val detailsList: MutableList<InApp> = mutableListOf()

    init {
        restoreSubscriptionsDetailsFromStorage()
    }

    override fun getInApp(sku: String): InApp {
        for (inApp in detailsList) {
            if (inApp.sku == sku) {
                return inApp
            }
        }
        return inAppConfiguration.getInApp(sku)
    }

    override fun set(skuDetails: List<SkuDetails>) {
        val list = ArrayList<InApp>()
        for (skuDetail in skuDetails) {
            val sku = skuDetail.sku
            val name = skuDetail.title
            val priceAndCurrency = skuDetail.price
            list.add(InApp(sku, name, priceAndCurrency))
        }

        detailsList.clear()
        detailsList.addAll(list)

        saveSubscriptionDetailsListOnDisk()
    }

    private fun saveSubscriptionDetailsListOnDisk() {
        val edit = sharedPreferences.edit()
        val json = HashSet<String>()
        for (inApp in detailsList) {
            json.add(inApp.toJson())
        }
        edit.putStringSet(IN_APP_DETAILS_KEY, json)
        edit.apply()
    }

    private fun restoreSubscriptionsDetailsFromStorage() {
        detailsList.clear()
        val inAppSet = sharedPreferences.getStringSet(IN_APP_DETAILS_KEY,
                HashSet<String>())
        for (inAppJson in inAppSet!!) {
            val inApp = InApp.fromJson(inAppJson)
            detailsList.add(inApp)
        }
    }

    companion object {
        const val SHARED_PREF_KEY = "in-app-details-storage"
        @VisibleForTesting
        const val IN_APP_DETAILS_KEY = "in_app_details_repository.key.details"
        private const val DEFAULT_CURRENCY_CODE = "EUR"
    }

}