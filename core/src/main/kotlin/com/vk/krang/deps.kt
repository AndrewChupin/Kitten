package com.vk.krang

import kotlin.reflect.KProperty

@SingleThread
operator fun <Dep : Any> LazyDep<Dep, out Any>.getValue(thisRef: Any?, property: KProperty<*>): Dep = value

@SingleThread
fun <Dep : Any> rcDeps(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepRc(initializer)

@SingleThread
fun <Dep : Any> lazyDeps(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepGod(initializer)

@SingleThread
fun <Dep : Any> newDeps(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepNew(initializer)
