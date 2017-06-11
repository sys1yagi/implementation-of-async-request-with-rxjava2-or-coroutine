package com.sys1yagi.android.kotlin.coroutine.api

import android.util.Log
import com.sys1yagi.android.kotlin.coroutine.entity.Shop
import java.util.*

class ShopApi {
    fun getShop(id: Long): Shop {
        Log.d("coroutine", "start getShop ${Thread.currentThread().id}")
        Thread.sleep(2000)
        val random = Random()
        if (random.nextInt(100) > 20) {
            Log.d("coroutine", "success getShop ${Thread.currentThread().id}")
            return Shop(id)
        } else {
            Log.d("coroutine", "error getShop ${Thread.currentThread().id}")
            throw Exception()
        }

    }
}
