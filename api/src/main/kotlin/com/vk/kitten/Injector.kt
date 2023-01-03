package com.vk.kitten

open class Injector<Delegate : Any> {
    private lateinit var delegate: Delegate
    private lateinit var borrower: LifecycleBorrower

    @SingleThread
    fun init(delegate: Delegate, borrower: LifecycleBorrower) {
        this.delegate = delegate
        this.borrower = borrower
    }

    @SingleThread
    fun isInit(): Boolean {
        return ::delegate.isInitialized && ::borrower.isInitialized
    }

    @SingleThread
    fun <Subject> injectWith(lifecycle: Any, factory: Delegate.() -> Subject): Subject {
        return borrower.borrow(lifecycle) {
            factory.invoke(delegate)
        }
    }
}
