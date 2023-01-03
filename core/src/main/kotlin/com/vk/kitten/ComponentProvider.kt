package com.vk.kitten

open class ComponentProvider : LifecycleBorrower() {

    protected val lifecycle: Any
	    get() {
        return innerLifecycle ?: throw IllegalStateException("Component is not initialized!!")
    }

    protected val provider = ComponentProviderProducer()

    private var innerLifecycle: Any? = null

    override fun lock(lifecycle: Any) {
        innerLifecycle = lifecycle
    }

    override fun unlock(lifecycle: Any) {
        innerLifecycle = null
    }

    protected inline fun <reified T : Component> getOrCreate(
        key: Any? = null,
        noinline builder: () -> T
    ) : T  {
        return provider.produceBuilder<T>(T::class.java)
            .getOrCreate(lifecycle, key, builder)
    }
}
