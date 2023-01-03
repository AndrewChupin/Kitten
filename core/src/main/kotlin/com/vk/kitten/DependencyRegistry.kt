package com.vk.kitten

class DependencyRegistry<Provider : ComponentProvider>(
    private val provider: Provider
) {

    init {
        GlobalInjector.init(Unit, provider)
    }

    @SingleThread
    fun <Cmp : Component> create(component: Any, reg: (Provider) -> Cmp) {
        GlobalInjector.injectWith(component) { reg(provider) }
    }

    @SingleThread
    fun <Deps : Any> register(initializer: Injector<Deps>, reg: (Provider) -> Deps) {
        initializer.init(reg.invoke(provider), provider)
    }
}
