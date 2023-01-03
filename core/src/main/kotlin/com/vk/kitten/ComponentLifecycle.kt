package com.vk.kitten

interface ComponentLifecycle {
	@SingleThread
	fun <Delegate : Any, Subject> Injector<Delegate>.inject(factory: Delegate.() -> Subject): Subject {
		return injectWith(this, factory)
	}
}
