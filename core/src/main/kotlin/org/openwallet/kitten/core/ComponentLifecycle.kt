package org.openwallet.kitten.core

import org.openwallet.kitten.api.Injector
import org.openwallet.kitten.api.SingleThread

interface ComponentLifecycle {
	@SingleThread
	fun <Delegate : Any, Subject> Injector<Delegate>.inject(factory: Delegate.() -> Subject): Subject {
		return injectWith(this, factory)
	}
}
