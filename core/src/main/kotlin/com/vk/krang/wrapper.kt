package com.vk.krang

@SingleThread
fun <Component : Any> componentWrapper() = lazy(LazyThreadSafetyMode.NONE) { ComponentBuilder<Component>() }
