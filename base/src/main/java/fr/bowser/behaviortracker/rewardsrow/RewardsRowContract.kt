package fr.bowser.behaviortracker.rewardsrow

import android.app.Activity
import com.android.billingclient.api.SkuDetails

interface RewardsRowContract {

    interface Presenter {

        fun onItemClicked(skuDetails: SkuDetails?)
    }

    interface Screen {

        fun getActivity(): Activity

        fun displayStoreConnectionError()
    }
}
