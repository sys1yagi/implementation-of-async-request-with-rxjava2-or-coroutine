package com.sys1yagi.android.kotlin.coroutine.api

import android.util.Log
import com.sys1yagi.android.kotlin.coroutine.entity.Shop
import io.reactivex.Single
import java.util.*

class ShopApi {
    fun getShop(id: Long): Single<Shop> {
        return Single.create {
            try {
                Log.d("rxjava", "start getShop ${Thread.currentThread().id}")
                Thread.sleep(2000)
                val random = Random()
                if (random.nextBoolean()) {
                    Log.d("rxjava", "success getShop ${Thread.currentThread().id}")
                    it.onSuccess(Shop(id))
                } else {
                    Log.d("rxjava", "error getShop ${Thread.currentThread().id}")
                    it.onError(Exception())
                }
            } catch(e: InterruptedException) {
                // no op
            }
        }
    }
}