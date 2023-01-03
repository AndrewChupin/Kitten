package com.vk.kitten

abstract class LifecycleBorrower {

    @SingleThread
    internal fun <Subject> borrow(
        lifecycle: Any,
        factory: () -> Subject
    ): Subject {
        lock(lifecycle)
        val obj = factory()
        unlock(lifecycle)
        return obj
    }

    @SingleThread
    protected abstract fun lock(lifecycle: Any)

    @SingleThread
    protected abstract fun unlock(lifecycle: Any)
}
