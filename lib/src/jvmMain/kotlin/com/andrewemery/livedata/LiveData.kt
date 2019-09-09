// used to prevent jvm conflict cause by inclusion of extension function
@file:JvmName("AndroidLiveData")

package com.andrewemery.livedata

/**
 * Created by Andrew Emery on 2019-06-04.
 *
 * The implementation of [LiveData] components for Java applications.
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

actual fun <T> MutableLiveData(value: T?): MutableLiveData<T> = MutableLiveData(null, null) // no event loop