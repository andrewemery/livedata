package com.andrewemery.lifecycle

/**
 * Created by Andrew Emery on 2019-05-31.
 *
 * [LiveData] is an abstract class describing a data holder class that is observed within a lifetime.
 *
 * The data object will notify its [Observer] in the following scenarios:
 * 1. When the observation starts: [LiveData.observe].
 * 2. When the [Observer] moves from not observing to observing.
 * 3. When the value changes: [LiveData.getValue].
 *
 * The [Observer] will be automatically removed upon termination of the [LifecycleOwner].
 *
 * [LiveData] instances are not thread-safe and assume that all interaction occurs on a single thread.
 */
expect abstract class LiveData<T>() {

    /**
     * Observe the data object.
     *
     * The data object will notify its [Observer] in the following scenarios:
     * 1. When the observation starts: [LiveData.observe].
     * 2. When the [Observer] moves from not observing to observing.
     * 3. When the value changes: [LiveData.getValue].
     *
     * @param owner The lifetime for which to observe the data object, after which it will be removed.
     * @param observer The object observing the change.
     * @see [remove]
     */
    open fun observe(owner: LifecycleOwner, observer: Observer<in T>)

    /**
     * Observe the data object forever.
     *
     * The data object will notify its [Observer] in the following scenarios:
     * 1. When the observation starts: [LiveData.observe].
     * 2. When the [Observer] moves from not observing to observing.
     * 3. When the value changes: [LiveData.getValue].
     *
     * @param observer The object observing the change.
     * @see [remove]
     */
    open fun observeForever(observer: Observer<in T>)

    /**
     * Remove the given observer, preventing further changes from being dispatched.
     *
     * @param observer The observer to remove.
     */
    open fun removeObserver(observer: Observer<in T>)

    /**
     * Get the current value of the data.
     *
     * @return The current value.
     */
    open fun getValue(): T?
}

/**
 * The [MutableLiveData] is an extension of [LiveData] that can have its value changed.
 *
 * Instances of [MutableLiveData] should remain as values within data sources and expose
 * [LiveData] instances to ensure data cannot be externally modified.
 *
 * [MutableLiveData] instances are not thread-safe and assume that all interaction occurs on a single
 * thread.
 */
expect open class MutableLiveData<T> : LiveData<T> {

    /**
     * Set the value of the data.
     *
     * @param value The value to set.
     */
    @Deprecated("Use #setValue instead", replaceWith = ReplaceWith("setValue"))
    fun set(value: T?)

    /**
     * Post the value of the data on the next event loop cycle (if possible).
     *
     * @param value The value to set.
     */
    @Deprecated("Use #postValue instead", replaceWith = ReplaceWith("postValue"))
    fun post(value: T?)
}

/**
 * [MutableLiveData] setValue.
 *
 * Included to provide a consistent interface with Android LiveData components. Extension function
 * used to avoid actual override conflict.
 */
fun <T> MutableLiveData<T>.setValue(value: T?) = set(value)

/**
 * [MutableLiveData] postValue.
 *
 * Included to provide a consistent interface with Android LiveData components. Extension function
 * used to avoid actual override conflict.
 */
fun <T> MutableLiveData<T>.postValue(value: T?) = post(value)

/**
 * The [Observer] is an interface that describes a component that is notified when a watched value
 * is changed.
 *
 * @param T The type of data being observed.
 * @see LiveData.observe
 */
expect interface Observer<T> {

    /**
     * Called when the data is changed.
     *
     * @param value The current data value.
     */
    fun onChanged(value: T?)
}

/**
 * The [LifecycleOwner] is an interface that describes a component with an lifetime that
 * makes observations for a period of time and and is eventually terminated.
 */
expect interface LifecycleOwner

/**
 * The [MutableLiveData] constructor.
 *
 * @param value The initial value of the [LiveData] object.
 */
expect fun <T> MutableLiveData(value: T?): MutableLiveData<T>