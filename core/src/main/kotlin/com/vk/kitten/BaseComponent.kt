package com.vk.kitten

abstract class BaseComponent<Deps>(
    protected open val deps: Deps
) {
    @SingleThread
    fun delegate(): Deps = deps
}