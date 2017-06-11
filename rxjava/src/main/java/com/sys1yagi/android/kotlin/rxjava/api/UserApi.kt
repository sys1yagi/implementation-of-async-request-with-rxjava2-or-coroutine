package com.sys1yagi.android.kotlin.rxjava.api

import android.util.Log
import com.sys1yagi.android.kotlin.rxjava.entity.User
import io.reactivex.Single
import java.util.*

class UserApi {
    fun me(): Single<User> {
        return Single.create {
            try {
                Log.d("rxjava", "start me ${Thread.currentThread().id}")
                Thread.sleep(1000)
                val random = Random()
                if (random.nextInt(100) > 20) {
                    Log.d("rxjava", "success me ${Thread.currentThread().id}")
                    it.onSuccess(User(10))
                } else {
                    Log.d("rxjava", "error me ${Thread.currentThread().id}")
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