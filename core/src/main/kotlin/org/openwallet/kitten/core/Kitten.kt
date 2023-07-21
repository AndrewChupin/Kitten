package org.openwallet.kitten.core

object Kitten {

    private var registry: DependencyRegistry<*>? = null

    fun <Provider : ComponentProvider> init(
        provider: Provider,
        applier: DependencyRegistry<Provider>.(Provider) -> Unit,
    ) {
        if (registry != null) {
            return
        }

        registry = DependencyRegistry(provider = provider)
            .apply {
                applier(provider)
            }
    }
}
