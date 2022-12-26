package com.vk.kitten

open class ComponentProvider : LifecycleBorrower() {

    private var innerLifecycle: ComponentLifecycle? = null

    protected val lifecycle: ComponentLifecycle get() {
        return innerLifecycle ?: throw IllegalStateException("Component is not initialized!!")
    }

    protected val provider = ComponentProviderProducer()

    override fun lock(lifecycle: ComponentLifecycle) {
        innerLifecycle = lifecycle
    }

    override fun unlock(lifecycle: ComponentLifecycle) {
        innerLifecycle = null
    }

    protected inline fun <reified T : Component> getOrCreate(
        key: Any? = null,
        noinline builder: () -> T
    ) : T  {
        return provider.produceProvider<T>(T::class.java)
            .getOrCreate(lifecycle, key, builder)
    }
}
