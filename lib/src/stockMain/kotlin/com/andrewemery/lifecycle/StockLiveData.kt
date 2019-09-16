package com.andrewemery.lifecycle

/**
 * Created by Andrew Emery on 2019-05-31.
 *
 * [StockLiveData] is an implementation of [LiveData] using kotlin components. It is designed to be
 * shared between platforms that do not provide their own native solution (ie. Android).
 */
abstract class StockLiveData<T>(private var value: T?, private val postman: Postman<T>?) {

    @Suppress("unused")
    constructor() : this(null, null)

    // the mapping of an observer to its wrapper
    private val observers: MutableMap<StockObserver<in T>, ObserverWrapper> = mutableMapOf()

    // the version of the event
    private var version = -1

    // whether or not the object is currently dispatching
    private var dispatching: Boolean = false

    // whether or not dispatching was invalidated (and needs to be repeated) mid-dispatch
    private var dispatchingInvalidated: Boolean = false

    private fun dispatch(source: ObserverWrapper?) {
        var initiator = source
        if (dispatching) {
            dispatchingInvalidated = true
            return
        }

        dispatching = true
        do {
            dispatchingInvalidated = false
            if (initiator != null) {
                initiator.onChanged()
                initiator = null
            } else {
                for (wrapper in observers.values.toList()) {
                    wrapper.onChanged()
                    if (dispatchingInvalidated) break
                }
            }
        } while (dispatchingInvalidated)
        dispatching = false
    }

    private inner class ObserverWrapper(val owner: StockLifecycleOwner, val observer: StockObserver<in T>) : StockLifecycleListener {
        private var observing: Boolean = false
        var lastVersion = -1

        override fun onLifetimeChange() {
            if (owner.isTerminated()) {
                removeObserver(observer)
                return
            }
            val now = owner.isObserving()
            if (observing == now) return
            observing = now
            if (now) dispatch(this)
        }

        fun onChanged() {
            if (!owner.isObserving()) return
            if (lastVersion >= version) return
            lastVersion = version
            observer.onChanged(value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    open fun observe(owner: StockLifecycleOwner, observer: StockObserver<in T>) {
        if (owner.isTerminated()) return
        val wrapper = ObserverWrapper(owner, observer)
        val existing = observers.putIfAbsent(observer, wrapper)
        if (existing != null && existing.owner != owner)
            throw IllegalArgumentException("cannot add same observer with different lifecycles")
        if (existing != null) return
        owner.addListener(wrapper)
        wrapper.onLifetimeChange()
    }

    @Suppress("UNCHECKED_CAST")
    open fun observeForever(observer: StockObserver<in T>) {
        observe(FOREVER, observer)
    }

    @Suppress("UNCHECKED_CAST")
    open fun removeObserver(observer: StockObserver<in T>) {
        val removed: ObserverWrapper = observers.remove(observer) ?: return
        removed.owner.removeListener(removed)
    }

    open fun getValue(): T? {
        return value
    }

    fun setValue(value: T?) {
        version++
        this.value = value
        dispatch(null)
    }

    fun postValue(value: T?) {
        if (postman != null) postman.postValue(value) { setValue(it) }
        else setValue(value)
    }
}

/**
 * The [Observer] is an interface that describes a component that is notified when a watched value
 * is changed.
 *
 * @param T The type of data being observed.
 * @see StockLiveData.observe
 */
interface StockObserver<T> {

    /**
     * Called when the data is changed.
     *
     * @param value The current data value.
     */
    fun onChanged(value: T?)
}

/**
 * The [StockLifecycleOwner] is an interface that describes a component with an lifetime that
 * makes observations for a period of time and and is eventually terminated.
 *
 * A view is an example of a component with such a lifetime whereby:
 * 1. The view is created.
 * 2. The view vacillates between observing and not observing.
 * 3. The view is terminated.
 *
 * Lifetime components adhere to the following flow:
 *
 *   ---------------         -----------------
 *   |  observing  |  <--->  | not observing |
 *   ---------------         -----------------
 *         |                       |
 *         -------------------------
 *                     |
 *                     v
 *               --------------
 *               | terminated |
 *               --------------
 */
interface StockLifecycleOwner {

    /**
     * Get whether or not the observer should currently be observing.
     *
     * @return True if observing.
     */
    fun isObserving(): Boolean

    /**
     * Get whether or not the observer has been terminated (and will never observe again).
     *
     * @return True if terminated.
     */
    fun isTerminated(): Boolean

    /**
     * Listen the changing state of the lifetime.
     *
     * @param listener The listener.
     */
    fun addListener(listener: StockLifecycleListener)

    /**
     * Remove the listener from the lifetime.
     *
     * @param listener The listener to remove.
     */
    fun removeListener(listener: StockLifecycleListener)
}

/**
 * The [StockLifecycleListener] is a component that listens to changes in the lifetime of a
 * [StockLifecycleOwner].
 */
interface StockLifecycleListener {

    /**
     * Method called when the state of the lifetime has changed.
     *
     * @see StockLifecycleOwner.isObserving
     * @see StockLifecycleOwner.isTerminated
     */
    fun onLifetimeChange()
}

/**
 * An instance of [StockLifecycleOwner] that is always observing (and never terminates).
 *
 * Instances of [Observer] that observe in such a fashion should be manually removed:
 * [StockLiveData.removeObserver].
 */
object FOREVER : StockLifecycleOwner {
    override fun isObserving(): Boolean = true
    override fun isTerminated(): Boolean = false
    override fun addListener(listener: StockLifecycleListener) {}
    override fun removeListener(listener: StockLifecycleListener) {}
}

/**
 * The [Postman] is an interface used to post a value to a [LiveData] object on the next event loop
 * of the main thread.
 */
interface Postman<T> {

    fun postValue(value: T?, block: (T?) -> Unit)

}

/**
 * @return The previous value associated with the specified key.
 */
internal fun <K, V> MutableMap<K, V>.putIfAbsent(key: K, value: V): V? {
    return if (!containsKey(key)) put(key, value)
    else this[key]
}