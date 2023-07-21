package org.openwallet.kitten.core

import org.openwallet.kitten.api.Component
import org.openwallet.kitten.api.Injector
import org.openwallet.kitten.api.SingleThread

class DependencyRegistry<Provider : ComponentProvider>(
    private val provider: Provider
) {

    @SingleThread
    fun <Cmp : Component> create(reg: () -> Cmp) {
        GlobalInjector.injectWith(this) { reg() }
    }

    @SingleThread
    fun <Deps : Any> register(initializer: Injector<Deps>, reg: () -> Deps) {
        initializer.init(reg.invoke(), provider)
    }
}
