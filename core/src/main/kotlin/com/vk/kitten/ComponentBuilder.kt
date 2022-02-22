package com.vk.kitten

import java.util.*
import kotlin.collections.HashMap

class ComponentBuilder<Component> {

    private val componentsMultimap = HashMap<Any?, WeakHashMap<ComponentLifecycle, Component>>()

    @SingleThread
    fun getOrCreate(lifecycle: ComponentLifecycle, key: Any? = null, builder: () -> Component): Component {
        retrieveAll(key)

        val map = componentsMultimap
            .getOrPut(key) { WeakHashMap<ComponentLifecycle, Component>() }

        val ref = map.values.firstOrNull()
        if (ref != null) {
            return ref
        }

        val newValue = builder.invoke()
        map[lifecycle] = newValue

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
