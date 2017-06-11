package com.sys1yagi.android.kotlin.coroutine.coroutine

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

fun <T> async(context: CoroutineContext = CommonPool, start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend CoroutineScope.() -> T)
        = kotlinx.coroutines.experimental.async(context, start, block)

fun ui(start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend CoroutineScope.() -> Unit)
        = launch(UI, start, block)
