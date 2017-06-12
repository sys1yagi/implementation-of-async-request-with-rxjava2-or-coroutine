package com.sys1yagi.android.kotlin.coroutine

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sys1yagi.android.kotlin.coroutine.api.ShopApi
import com.sys1yagi.android.kotlin.coroutine.api.SubscriptionShopApi
import com.sys1yagi.android.kotlin.coroutine.api.UserApi
import com.sys1yagi.android.kotlin.coroutine.coroutine.async
import com.sys1yagi.android.kotlin.coroutine.coroutine.ui
import com.sys1yagi.android.kotlin.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.Job

class MainActivity : AppCompatActivity() {

    val shopApi = ShopApi()
    val userApi = UserApi()
    val subscriptionShopApi = SubscriptionShopApi()

    val binding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main) }

    var job: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.singleRequestButton.setOnClickListener {
            singleRequest()
        }
        binding.serializedRequestButton.setOnClickListener {
            serializedRequest()
        }

        binding.parallelRequestButton.setOnClickListener {
            parallelRequest()
        }
    }

    fun singleRequest() {
        job.cancel()
        binding.singleRequestResult.text = "loading..."
        job = ui {
            try {
                val shop = async { shopApi.getShop(10) }.await()
                binding.singleRequestResult.text = "success! shop id=${shop.id}"
            } catch(e: CancellationException) {
                binding.singleRequestResult.text = "cancel!"
            } catch (e: Exception) {
                e.printStackTrace()
                binding.singleRequestResult.text = "error!"
            }
        }

        // no extensions
//        job = launch(UI) {
//            try {
//                val shop = async(CommonPool) { shopApi.getShop(10) }.await()
//                binding.singleRequestResult.text = "success! shop id=${shop.id}"
//            } catch(e: CancellationException) {
//                binding.singleRequestResult.text = "cancel!"
//            } catch (e: Exception) {
//                binding.singleRequestResult.text = "error!"
//            }
//        }
    }

    fun serializedRequest() {
        job.cancel()
        binding.serializedRequestResult.text = "loading..."
        job = ui {
            try {
                val userJob = async { userApi.me() }
                val subscriptionShopsJob = async { subscriptionShopApi.getSubscriptionShops(userJob.await().id) }
                val subscriptionShops = subscriptionShopsJob.await()
                binding.serializedRequestResult.text = "success! sucscription shop count = ${subscriptionShops.size}"
            } catch(e: CancellationException) {
                binding.serializedRequestResult.text = "cancel!"
            } catch (e: Exception) {
                e.printStackTrace()
                binding.serializedRequestResult.text = "error!"
            }
        }
    }

    fun parallelRequest() {
        job.cancel()
        binding.parallelRequestResult.text = "loading..."
        job = ui {
            try {
                val userJob = async { userApi.me() }
                val shopJob = async { shopApi.getShop(10L) }
                binding.parallelRequestResult.text = "success! user id = ${userJob.await().id}, shop id = ${shopJob.await().id}"
            } catch(e: CancellationException) {
                binding.parallelRequestResult.text = "cancel!"
            } catch (e: Exception) {
                e.printStackTrace()
                binding.parallelRequestResult.text = "error!"
            }
        }
    }
}
