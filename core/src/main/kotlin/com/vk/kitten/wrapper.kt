package com.vk.kitten

@SingleThread
fun <Component : Any> componentWrapper() = lazy(LazyThreadSafetyMode.NONE) { ComponentBuilder<Component>() }
