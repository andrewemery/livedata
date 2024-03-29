package com.andrewemery.lifecycle

import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Created by Andrew Emery on 2019-06-04.
 *
 * The implementation of [LiveData] components for iOS applications.
 */
actual typealias LifecycleOwner = StockLifecycleOwner // interface

actual typealias Observer<T> = StockObserver<T> // interface
actual typealias LiveData<T> = StockLiveData<T> // abstract class

actual open class MutableLiveData<T>(value: T?, postman: Postman<T>?) : StockLiveData<T>(value, postman) {
    actual fun set(value: T?) {
        super.setValue(value)
    }

    actual fun post(value: T?) {
        super.postValue(value)
    }
}

actual fun <T> MutableLiveData(value: T?): MutableLiveData<T> = MutableLiveData(value, DarwinPostman()) // main event loop

private class DarwinPostman<T> : Postman<T> {
    override fun postValue(value: T?, block: (T?) -> Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            block(value)
        }
    }
}