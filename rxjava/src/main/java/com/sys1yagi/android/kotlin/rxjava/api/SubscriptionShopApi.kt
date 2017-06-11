package com.sys1yagi.android.kotlin.rxjava.api

import android.util.Log
import com.sys1yagi.android.kotlin.rxjava.entity.Shop
import io.reactivex.Single
import java.util.*

class SubscriptionShopApi {

    fun getSubscriptionShops(userId: Long): Single<List<Shop>> {
        return Single.create {
            try {
                Log.d("rxjava", "start getSubscriptionShops ${Thread.currentThread().id}")
                Thread.sleep(500)
                val random = Random()
                if (random.nextInt(100) > 20) {
                    Log.d("rxjava", "success getSubscriptionShops ${Thread.currentThread().id}")
                    it.onSuccess(List(random.nextInt(20), { Shop(random.nextLong()) }))
                } else {
                    Log.d("rxjava", "error getSubscriptionShops ${Thread.currentThread().id}")
                    if (!it.isDisposed) {
                        it.onError(Exception())
                    }
                }
            } catch(e: InterruptedException) {
                // no op
            }
        }
    }
}