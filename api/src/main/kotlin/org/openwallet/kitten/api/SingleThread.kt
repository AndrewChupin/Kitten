package org.openwallet.kitten.api

@Target(
    allowedTargets = [
        AnnotationTarget.CLASS,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER
    ]
)
@Retention(value = AnnotationRetention.SOURCE)
annotation class SingleThread
