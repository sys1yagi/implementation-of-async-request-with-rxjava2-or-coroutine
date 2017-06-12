package com.sys1yagi.android.kotlin.rxjava

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.sys1yagi.android.kotlin.rxjava.api.ShopApi
import com.sys1yagi.android.kotlin.rxjava.api.SubscriptionShopApi
import com.sys1yagi.android.kotlin.rxjava.api.UserApi
import com.sys1yagi.android.kotlin.rxjava.databinding.ActivityMainBinding
import com.sys1yagi.android.kotlin.rxjava.entity.Shop
import com.sys1yagi.android.kotlin.rxjava.entity.User
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    val shopApi = ShopApi()
    val userApi = UserApi()
    val subscriptionShopApi = SubscriptionShopApi()

    var disposable = Disposables.disposed()

    val binding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main) }

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
        disposable.dispose()
        binding.singleRequestResult.text = "loading..."
        val disposable = shopApi.getShop(10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .handleDisposed(binding.singleRequestResult, this)
                .subscribe(
                        {
                            binding.singleRequestResult.text = "success!"
                        },
                        {
                            binding.singleRequestResult.text = "error!"
                            it.printStackTrace()
                        }
                )
    }

    fun serializedRequest() {
        disposable.dispose()
        binding.serializedRequestResult.text = "loading..."
        disposable =
                userApi.me()
                        .flatMap { user ->
                            subscriptionShopApi.getSubscriptionShops(user.id)
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .handleDisposed(binding.serializedRequestResult, this)
                        .subscribe(
                                {
                                    binding.serializedRequestResult.text = "success! sucscription shop count = ${it.size}"
                                },
                                {
                                    binding.serializedRequestResult.text = "error!"
                                    it.printStackTrace()
                                }
                        )
    }

    fun parallelRequest() {
        disposable.dispose()
        binding.parallelRequestResult.text = "loading..."
        disposable = Single.zip<User, Shop, Pair<User, Shop>>(
                userApi.me().subscribeOn(Schedulers.io()),
                shopApi.getShop(10L).subscribeOn(Schedulers.io()),
                BiFunction { user, shop ->
                    Pair(user, shop)
                }
        )
                .observeOn(AndroidSchedulers.mainThread())
                .handleDisposed(binding.parallelRequestResult, this)
                .subscribe(
                        { (user, shop) ->
                            binding.parallelRequestResult.text = "success! user id = ${user.id}, shop id = ${shop.id}"
                        },
                        {
                            binding.parallelRequestResult.text = "error!"
                            it.printStackTrace()
                        }
                )
    }

    fun <T> Single<T>.handleDisposed(textView: TextView, activity: MainActivity): Single<T> =
            doOnDispose {
                textView.text = "disposed."
            }.doFinally {
                activity.disposable = Disposables.disposed()
            }
}
