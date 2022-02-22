package com.vk.krang

abstract class ProviderComponent<Deps, Subject>(
    deps: Deps
) : BaseComponent<Deps>(deps) {
    @SingleThread
    abstract fun provide(): Subject
}
