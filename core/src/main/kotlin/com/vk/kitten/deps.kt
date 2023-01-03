package com.vk.kitten

import kotlin.reflect.KProperty

@SingleThread
operator fun <Dep : Any> LazyDep<Dep, out Any>.getValue(thisRef: Any?, property: KProperty<*>): Dep = value

@SingleThread
fun <Dep : Any> depRc(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepRc(initializer)

@SingleThread
fun <Dep : Any> depLazy(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepGod(initializer)

@SingleThread
fun <Dep : Any> depNew(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepNew(initializer)
