package com.andrewemery.lifecycle

/**
 * Created by Andrew Emery on 2019-05-31.
 *
 * [ViewModel] is a class that is responsible for preparing and managing the data for view containers.
 */
expect abstract class ViewModel() {

    /**
     * Method will be called when the view model is no longer used and will be destroyed.
     */
    protected open fun onCleared()
}
