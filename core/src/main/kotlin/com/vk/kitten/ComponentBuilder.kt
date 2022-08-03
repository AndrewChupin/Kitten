package com.vk.kitten

import java.util.*
import kotlin.collections.HashMap

class ComponentBuilder<Component> {

    private val componentsMultimap = HashMap<Any?, WeakHashMap<ComponentLifecycle, Component>>()

    @SingleThread
    fun getOrCreate(lifecycle: ComponentLifecycle, key: Any? = null, builder: () -> Component): Component {
        retrieveAll(key)

        val ref = componentsMultimap[key]?.let { componentsMap ->
            val componentEntry = componentsMap.entries.firstOrNull { (_, component) ->
                component != null
            }

            val existingLifecycle = componentEntry?.key
            val existingComponent = componentEntry?.value

            if (existingComponent != null && existingLifecycle != lifecycle) {
                componentsMap[lifecycle] = existingComponent
            }

            existingComponent
        }

        if (ref != null) {
            return ref
        }

        val newValue = builder.invoke()
        componentsMultimap[key] = WeakHashMap<ComponentLifecycle, Component>()
            .apply {
                put(lifecycle, newValue)
            }

        return newValue
    }

    private fun retrieveAll(except: Any? = null) {
        componentsMultimap.iterator()
            .apply {
                while (hasNext()) {
                    val item = next()
                    if (item.key == except) {
                        continue
                    }

                    if (item.value.values.isEmpty()) {
                        remove()
                    }
                }
            }
    }
}
