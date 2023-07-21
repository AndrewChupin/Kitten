package org.openwallet.kitten.core

import org.openwallet.kitten.api.SingleThread

@SingleThread
fun <Dep : Any> depRc(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepRc(initializer)

@SingleThread
fun <Dep : Any> depLazy(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepGod(initializer)
