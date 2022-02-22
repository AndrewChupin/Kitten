package com.vk.krang

interface ComponentLifecycle {
    @SingleThread
    fun <Delegate : Any, Subject> Injector<Delegate>.inject(factory: Delegate.() -> Subject): Subject {
        return injectWith(this@ComponentLifecycle, factory)
    }
}
