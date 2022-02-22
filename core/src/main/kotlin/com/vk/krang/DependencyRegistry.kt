package com.vk.krang

class DependencyRegistry<Provider : ComponentProvider>(
    private val provider: Provider
) {

    init {
        GlobalInjector.init(Unit, provider)
    }

    @SingleThread
    fun <Component : BaseComponent<out Any>> create(component: ComponentLifecycle, reg: (Provider) -> Component) {
        GlobalInjector.injectWith(component) { reg(provider) }
    }

    @SingleThread
    fun <Deps : Any> register(initializer: Injector<Deps>, reg: (Provider) -> Deps) {
        initializer.init(reg.invoke(provider), provider)
    }
}
