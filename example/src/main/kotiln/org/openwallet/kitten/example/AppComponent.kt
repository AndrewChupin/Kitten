package org.openwallet.kitten.example

import org.openwallet.kitten.api.Component

class AppComponent(
    val service: Service,
): Component {
    interface Net {
        val seed: Seed
        val network: Network
    }

    interface Service {
        val net: Net
        val repo: ServiceRepo
    }
}
