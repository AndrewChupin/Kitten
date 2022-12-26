package com.vk.kitten.example

import com.vk.kitten.*


// /// //
// API
interface ManDelegate {
    fun provideFoo(data: FooData): FooFeature
    fun provideBar(data: FooData): BarFeature
}

object ModInjector : Injector<ManDelegate>()

// IMPL
class AppComponentProvider(
    private val application: Application
) : ComponentProvider() {

    fun getApp(): AppComponent {
        return getOrCreate {
            AppComponent(
                object : AppComponent.Deps {
                    // with component
                    private val num = 12

                    // each time new
                    override val seed: Seed by newDep { Seed(num) }

                    // first call
                    override val network by lazyDep { Network(seed) }

                    // ref-counter
                    override val serviceRepo: ServiceRepo by rcDep { ServiceRepoImpl(application, network) }
                }
            )
        }
    }

    fun getFoo(id: FooData): FooComponent {
        return getOrCreate(id) {
            FooComponent(
                object : FooComponent.Deps, AppComponent.Deps by getApp().delegate() {
                    override val repo by rcDep { FooRepo(id, network) }
                }
            )
        }
    }

    fun getBar(id: FooData): BarComponent {
        return getOrCreate {
            BarComponent(
                object : BarComponent.Deps, FooComponent.Deps by getFoo(id).delegate() {}
            )
        }
    }
}

// /// //
// APP
class Seed(num: Int)
class Network(seed: Seed)
interface ServiceRepo
class ServiceRepoImpl(application: Application, net: Network) : ServiceRepo

class AppComponent(
    deps: Deps
): BaseComponent<AppComponent.Deps>(deps) {
    interface Deps {
        val seed: Seed
        val network: Network
        val serviceRepo: ServiceRepo
    }
}

class Application : ComponentLifecycle {

    fun onCreate() {
        val app = this

        DependencyRegistry(
            provider = AppComponentProvider(app)
        ).apply {
            // Create deps and component immediately
            create(app) { provider ->
                provider.getApp()
            }

            // Init delegate without deps and components
            register(ModInjector) { provider ->
                object : ManDelegate {
                    override fun provideFoo(data: FooData): FooFeature {
                        return provider.getFoo(data)
                            .provide()
                    }

                    override fun provideBar(data: FooData): BarFeature {
                        return provider.getBar(data)
                            .provide()
                    }
                }
            }
        }
    }
}


// /// //
// FOO
class FooData
class FooRepo(val id: FooData, val net: Network)
class FooFeature(val repo: FooRepo, val serviceRepo: ServiceRepo)

class FooComponent(
    deps: Deps
) : ProviderComponent<FooComponent.Deps, FooFeature>(deps) {
    override fun provide() = FooFeature(deps.repo, deps.serviceRepo)
    interface Deps : AppComponent.Deps {
        val repo: FooRepo
    }
}

class FooFragment : ComponentLifecycle {
    fun onAttach() {
        val feature = ModInjector.injectWith(this) { provideFoo(FooData()) }
    }
}


// /// //
// BAR
class BarComponent(
    deps: Deps
) : ProviderComponent<BarComponent.Deps, BarFeature>(deps) {
    override fun provide() = BarFeature(deps.serviceRepo)
    interface Deps : FooComponent.Deps
}

class BarFeature(
    val serviceRepo: ServiceRepo
)

class BarFragment : ComponentLifecycle {
    fun onAttach() {
        val feature = ModInjector.injectWith(this) { provideBar(FooData()) }
        // or short example
        val feature1 = ModInjector.inject { provideBar(FooData()) }
    }
}