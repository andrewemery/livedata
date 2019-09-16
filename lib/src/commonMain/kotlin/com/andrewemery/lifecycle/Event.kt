package com.andrewemery.lifecycle

/**
 * Created by Andrew Emery on 2019-05-31.
 *
 * The [Event] is an object containing a value that is to be consumed once and only once.
 *
 * Example usage:
 * ```
 * model.onNavigateHome.consume(this) {
 *     // handle event
 * }
 * ```
 *
 * This class is not thread-safe.
 */
class Event<out T : Any>(private val value: T) {
    var consumed = false
        private set

    /**
     * Consume the non-null event.
     *
     * @return The event (to consume) or null if already consumed.
     */
    fun consume(): T? {
        return if (consumed) null
        else {
            consumed = true
            value
        }
    }

    /**
     * Peek at the value of the event, not consuming.
     *
     * @return The event value.
     */
    fun peek(): T = value
}

/**
 * Consume the event as dispatched from the data object.
 *
 * @param owner The owner to observe the data object for.
 * @param block The block to execute on consumption.
 *
 * @return The observer.
 */
fun <T : Any> LiveData<Event<T>>.consume(owner: LifecycleOwner, block: (T) -> Unit): Observer<Event<T>> {
    return object : Observer<Event<T>> {
        override fun onChanged(value: Event<T>?) {
            value?.consume()?.let(block)
        }
    }.apply { observe(owner, this) }
}