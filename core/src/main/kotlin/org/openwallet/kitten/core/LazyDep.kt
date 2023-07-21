package org.openwallet.kitten.core

import org.openwallet.kitten.api.SingleThread
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

abstract class LazyDep<Dep : Any, Holder : Any>(
    private val initializer: () -> Dep,
) {

    protected var memory: Holder? = null

    @SingleThread
    abstract fun getCachedDependency(): Dep?

    @SingleThread
    abstract fun createDependency(dep: Dep): Holder

    @SingleThread
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Dep {
        val cachedValue = getCachedDependency()
        if (cachedValue != null) {
            return cachedValue
        }

        val dep = initializer.invoke()
        memory = createDependency(dep)
        return dep
    }
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
