package com.vk.krang

open class Injector<Delegate : Any> {
    private lateinit var delegate: Delegate
    private lateinit var borrower: LifecycleBorrower

    @SingleThread
    fun init(delegate: Delegate, borrower: LifecycleBorrower) {
        this.delegate = delegate
        this.borrower = borrower
    }

    @SingleThread
    fun <Subject> injectWith(lifecycle: ComponentLifecycle, factory: Delegate.() -> Subject): Subject {
        return borrower.borrow(lifecycle) {
            factory.invoke(delegate)
        }
    }
}