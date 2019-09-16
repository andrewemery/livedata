// used to prevent jvm conflict cause by inclusion of extension function
@file:JvmName("AndroidLiveData")

package com.andrewemery.lifecycle

/**
 * Created by Andrew Emery on 2019-06-04.
 *
 * The implementation of [ViewModel] components for Android applications.
 */
actual abstract class ViewModel : androidx.lifecycle.ViewModel() {
    actual override fun onCleared() {
        super.onCleared()
    }
}
