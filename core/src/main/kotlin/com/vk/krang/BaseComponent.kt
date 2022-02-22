package com.vk.krang

abstract class BaseComponent<Deps>(
    protected open val deps: Deps
) {
    @SingleThread
    fun delegate(): Deps = deps
}