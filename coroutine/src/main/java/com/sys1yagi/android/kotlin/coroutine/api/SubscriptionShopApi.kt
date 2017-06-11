package com.sys1yagi.android.kotlin.coroutine.api

import android.util.Log
import com.sys1yagi.android.kotlin.coroutine.entity.Shop
import java.util.*

class SubscriptionShopApi {
    fun getSubscriptionShops(userId: Long): List<Shop> {
        Log.d("coroutine", "start getSubscriptionShops ${Thread.currentThread().id}")
        Thread.sleep(500)
        val random = Random()
        if (random.nextInt(100) > 20) {
            Log.d("coroutine", "success getSubscriptionShops ${Thread.currentThread().id}")
            return List(random.nextInt(20), { Shop(random.nextLong()) })
        } else {
            Log.d("coroutine", "error getSubscriptionShops ${Thread.currentThread().id}")
            throw Exception()

        }
    }
}
