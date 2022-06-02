package com.vk.kitten

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
            memory = createMemory(dep)
            return dep
        }

    @SingleThread
    abstract fun getDependency(): Dep?

    @SingleThread
    abstract fun createMemory(dep: Dep): Holder
}

class LazyDepRc<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, WeakReference<Dep>>(initializer) {
    override fun getDependency(): Dep? = memory?.get()
    override fun createMemory(dep: Dep): WeakReference<Dep> = WeakReference(dep)
}

class LazyDepGod<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, Dep>(initializer) {
    override fun getDependency(): Dep? = memory
    override fun createMemory(dep: Dep): Dep = dep
}

class LazyDepNew<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, Dep>(initializer) {
    override fun getDependency(): Dep = initializer.invoke()
    override fun createMemory(dep: Dep): Dep = initializer.invoke() // never used
}
