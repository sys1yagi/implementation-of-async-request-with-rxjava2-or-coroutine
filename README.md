# Implementation of async request with Rxjava2 or Coroutine.

Implement the following pattern in each environment.

- Single request
- Sequential request
- Parallel request
- Error Handling
- Cancellation

# RxJava2

## Set Up

```groovy
dependencies {
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
    implementation 'io.reactivex.rxjava2:rxkotlin:2.0.3'
}
```

## Single Request

```kotlin
disposable = shopApi.getShop(10)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(
            {
                // success
            },
            {
                // error
            }
    )
```

## Sequential request

use `flatMap()`

```kotlin
disposable = userApi.me()
    .flatMap { user ->
        subscriptionShopApi.getSubscriptionShops(user.id)
    }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(
            { subscriptionShops ->
                // success
            },
            {
                // error
            }
    )
```

## Parallel request

use `zip()`

```kotlin
 disposable = Single.zip<User, Shop, Pair<User, Shop>>(
        userApi.me().subscribeOn(Schedulers.io()),
        shopApi.getShop(10L).subscribeOn(Schedulers.io()),
        BiFunction { user, shop ->
            Pair(user, shop)
        }
    )
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                { (user, shop) ->
                    // success
                },
                {
                    // error
                }
        )
```

## Cancellation

__cancell__

Just call `dispose()`

```kotlin
disposable.dispose()
```

__cancellation handling__

use `doOnDispose()`. But Disposable's handling is confusing.

```kotlin
userApi.me()
    .doOnDispose {
        // disposed
    }
```

Even if Single is successful, doOnDisposable remains alive.
If you call `dispose()` it will be executed.
So, I implemented the following function.


```kotlin
fun <T> Single<T>.handleDisposed(textView: TextView, activity: MainActivity): Single<T> =
    doOnDispose {
        textView.text = "disposed."
    }.doFinally {
        activity.disposable = Disposables.disposed()
    }
```

I do not know if this is a good idea ¯\\\_(ツ)_/¯

```kotlin
shopApi.getShop(10)
    .handleDisposed(binding.singleRequestResult, this)
    .subscribe(...
```

# Coroutine

## Set Up


```groovy
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jre7:1.1.2-4"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:0.16"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:0.16"
}
```

## Single Request

```kotlin
job = launch(UI) {
    try {
        val shop = async(CommonPool) { shopApi.getShop(10) }.await()
        // success
    } catch (e: Exception) {
        // error
    }
}
```

`launch(UI)` and `async(CommonPool)` are somewhat verbose.
 So I implemented the following function.

```kotlin
fun <T> async(context: CoroutineContext = CommonPool, start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend CoroutineScope.() -> T)
        = kotlinx.coroutines.experimental.async(context, start, block)

fun ui(start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend CoroutineScope.() -> Unit)
        = launch(UI, start, block)
```

You can write it more simply.

```kotlin
job = ui {
    try {
        val shop = async { shopApi.getShop(10) }.await()
        // success
    } catch (e: Exception) {
        // error
    }
}
```

## Sequential request

```kotlin
job = ui {
    try {
        val userJob = async { userApi.me() }
        val subscriptionShopsJob = async { subscriptionShopApi.getSubscriptionShops(userJob.await().id) }
        val subscriptionShops = subscriptionShopsJob.await()
        // success
    } catch (e: Exception) {
        // error
    }
}
```

## Parallel request

Coroutine function can set [CoroutineStart](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-coroutine-start/index.html).
`CoroutineStart.DEFAULT` immediately schedules coroutine for execution according to its context.
So, parallel execution can be written as follows.

```kotlin
job = ui {
    try {
        val userJob = async { userApi.me() } // start immediately
        val shopJob = async { shopApi.getShop(10L) } // start immediately
        val user = userJob.await()
        val shop = shopJob.await()
        // success
    } catch (e: Exception) {
        // error
    }
}
```

## Cancellation

__cancell__

Just call `cancel()`

```kotlin
job.cancel()
```

__cancellation handling__

When you call `job.cancel()`, throw CancellationException to coroutine block. 
You can catch it.

```kotlin
job = ui {
    try {
        val shop = async { shopApi.getShop(10) }.await()
        // success
    } catch(e: CancellationException) {
        // cancel
    } catch (e: Exception) {
        // error
    }
}
```
