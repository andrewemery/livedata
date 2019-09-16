package com.andrewemery.lifecycle

/**
 * Created by Andrew Emery on 2019-05-31.
 *
 * [StockLiveData] is an implementation of [LiveData] using kotlin components. It is designed to be
 * shared between platforms that do not provide their own native solution (ie. Android).
 */
abstract class StockViewModel {
    protected open fun onCleared() {
        // do nothing
    }
}