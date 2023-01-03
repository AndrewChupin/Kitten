package com.vk.kitten

import java.lang.ref.WeakReference

abstract class LazyDep<Dep : Any, Holder : Any>(
    protected val initializer: () -> Dep,
) {

    protected var memory: Holder? = null

    val value: Dep
        @SingleThread
        get() {
            val cachedValue = getCachedDependency()
            if (cachedValue != null) {
                return cachedValue
            }

            val dep = initializer.invoke()
            memory = createDependency(dep)
            return dep
        }

    @SingleThread
    abstract fun getCachedDependency(): Dep?

    @SingleThread
    abstract fun createDependency(dep: Dep): Holder
}

class LazyDepRc<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, WeakReference<Dep>>(initializer) {
    override fun getCachedDependency(): Dep? = memory?.get()
    override fun createDependency(dep: Dep): WeakReference<Dep> = WeakReference(dep)
}

class LazyDepGod<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, Dep>(initializer) {
    override fun getCachedDependency(): Dep? = memory
    override fun createDependency(dep: Dep): Dep = dep
}

class LazyDepNew<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, Dep>(initializer) {
    override fun getCachedDependency(): Dep = initializer.invoke()
    override fun createDependency(dep: Dep): Dep = initializer.invoke() // never used
}
