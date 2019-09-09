// used to prevent jvm conflict cause by inclusion of extension function
@file:JvmName("AndroidLiveData")

package com.andrewemery.livedata

/**
 * Created by Andrew Emery on 2019-06-04.
 *
 * The implementation of [LiveData] components for Android applications.
 */
actual typealias LifecycleOwner = androidx.lifecycle.LifecycleOwner // interface

actual typealias Observer<T> = androidx.lifecycle.Observer<T> // interface
actual typealias LiveData<T> = androidx.lifecycle.LiveData<T> // abstract class

actual open class MutableLiveData<T> : LiveData<T>() {
    actual fun set(value: T?) {
        super.setValue(value)
    }

    actual fun post(value: T?) {
        super.postValue(value)
    }
}

actual fun <T> MutableLiveData(value: T?): MutableLiveData<T> = MutableLiveData()