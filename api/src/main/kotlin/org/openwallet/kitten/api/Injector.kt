package org.openwallet.kitten.api

open class Injector<Provider : Any> {
    private lateinit var provider: Provider
    private lateinit var borrower: LifecycleBorrower

    @SingleThread
    fun init(delegate: Provider, borrower: LifecycleBorrower) {
        this.provider = delegate
        this.borrower = borrower
    }

    @SingleThread
    fun isInit(): Boolean {
        return ::provider.isInitialized && ::borrower.isInitialized
    }

    @SingleThread
    fun <Subject> injectWith(lifecycle: Any, factory: Provider.() -> Subject): Subject {
        return borrower.borrow(lifecycle) {
            factory.invoke(provider)
        }
    }
}
