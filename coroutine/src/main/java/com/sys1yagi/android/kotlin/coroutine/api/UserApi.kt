package com.sys1yagi.android.kotlin.coroutine.api

import android.util.Log
import com.sys1yagi.android.kotlin.coroutine.entity.User
import java.util.*

class UserApi {
    fun me(): User {
        Log.d("coroutine", "start me ${Thread.currentThread().id}")
        Thread.sleep(1000)
        val random = Random()
        if (random.nextInt(100) > 20) {
            Log.d("coroutine", "success me ${Thread.currentThread().id}")
            return User(10)
        } else {
            Log.d("coroutine", "error me ${Thread.currentThread().id}")
            throw Exception()
        }
    }
}
