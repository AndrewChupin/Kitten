package com.vk.kitten

interface Component

abstract class BaseComponent<Deps>(
    protected open val deps: Deps
) : Component {
    @SingleThread
    fun delegate(): Deps = deps
}