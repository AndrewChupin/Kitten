package org.openwallet.kitten.core

import org.openwallet.kitten.api.Component

class ComponentProviderProducer {

	private val builders = HashMap<Any, ComponentBuilder<out Component>>()

	@Suppress("UNCHECKED_CAST")
	fun <T : Component> produceBuilder(clazz: Any) : ComponentBuilder<T> {
		val component = builders[clazz] as? ComponentBuilder<T>

		if (component != null) {
			return component
		}

		val componentBuilder = ComponentBuilder<T>()
		builders[clazz] = componentBuilder
		return componentBuilder
	}
}
