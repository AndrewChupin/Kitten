package com.vk.kitten

abstract class LifecycleBorrower {

    @SingleThread
    internal fun <Subject> borrow(
        lifecycle: ComponentLifecycle,
        factory: () -> Subject
    ): Subject {
        lock(lifecycle)
        val obj = factory()
        unlock(lifecycle)
        return obj
    }

    @SingleThread
    protected abstract fun lock(lifecycle: ComponentLifecycle)

    @SingleThread
    protected abstract fun unlock(lifecycle: ComponentLifecycle)
}
