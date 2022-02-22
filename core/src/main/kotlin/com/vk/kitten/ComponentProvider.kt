package com.vk.kitten

open class ComponentProvider : LifecycleBorrower() {

    private var innerLifecycle: ComponentLifecycle? = null

    protected val lifecycle: ComponentLifecycle get() {
        return innerLifecycle ?: throw IllegalStateException("Component is not initialized!!")
    }

    override fun lock(lifecycle: ComponentLifecycle) {
        innerLifecycle = lifecycle
    }

    override fun unlock(lifecycle: ComponentLifecycle) {
        innerLifecycle = null
    }
}
