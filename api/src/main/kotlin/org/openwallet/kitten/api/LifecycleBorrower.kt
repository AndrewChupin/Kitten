package org.openwallet.kitten.api

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class LifecycleBorrower {

    private val lock = ReentrantLock()

    @SingleThread
    internal fun <Subject> borrow(
        lifecycle: Any,
        factory: () -> Subject
    ): Subject {
        lock.withLock { // TODO
            lock(lifecycle)
            val obj = factory()
            unlock(lifecycle)
            return obj
        }
    }

    @SingleThread
    protected abstract fun lock(lifecycle: Any)

    @SingleThread
    protected abstract fun unlock(lifecycle: Any)
}
