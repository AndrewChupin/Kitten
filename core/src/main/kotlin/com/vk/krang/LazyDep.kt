package com.vk.krang

import java.lang.ref.WeakReference

abstract class LazyDep<Dep : Any, Holder : Any>(
    protected val initializer: () -> Dep,

    ) {
    protected var memory: Holder? = null

    val value: Dep
        @SingleThread
        get() {
            val cachedValue = getDependency()
            if (cachedValue != null) {
                return cachedValue
            }

            val dep = initializer.invoke()
            memory = createMemory()
            return dep
        }

    @SingleThread
    abstract fun getDependency(): Dep?

    @SingleThread
    abstract fun createMemory(): Holder
}

class LazyDepRc<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, WeakReference<Dep>>(initializer) {
    override fun getDependency(): Dep? = memory?.get()
    override fun createMemory(): WeakReference<Dep> = WeakReference(initializer.invoke())
}

class LazyDepGod<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, Dep>(initializer) {
    override fun getDependency(): Dep? = memory
    override fun createMemory(): Dep = initializer.invoke()
}

class LazyDepNew<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, Dep>(initializer) {
    override fun getDependency(): Dep = initializer.invoke()
    override fun createMemory(): Dep = initializer.invoke() // never used
}
