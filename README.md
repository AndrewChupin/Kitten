# Krang
Kotlin DI fast and safe library

<img src="https://www.meme-arsenal.com/memes/7cbf0cd524871538f80199dd69325ce0.jpg" width="100%" height="500">

## Why?
This library fit to small and espesially **huge** projects. You can use it for multimodule Kotlin/Java project.

Advantages of this libraries:
- **Lightweight** - the entire library takes only 5 KB space
- **Fast** - this library **doesn't use Codegen or Reflection**, only your code
- **Api/Core Modules** - you can add super-lightweight **api** module to libraries, and **core** module for main library
- **Safe** - unlike Dagger 2, kodin or koin you have to write all implmentation of objects, but API of this library really short
- **Simple** - it's probably takes less code than Dagger 2
- **Lifecycle Management** - there is a lot of helpers in library to mange lifecyle of **components/deps set/dep**


## Guide
### 1. Add ":core" dependency to your Main Library (Application Entrypoint)
``` kotlin
implementation("com.vk.krang:core:1.0.0")
```
### 2. Add ":api" dependency to your Secondary Modules (Feature Entrypoint)
``` kotlin
implementation("com.vk.krang:api:1.0.0")
```
### 3. Create some dependecies somewhere
``` kotlin
// Dependencies
class Seed(num: Int)
class Network(seed: Seed)

// Dependency with interface
interface ServiceRepo
class ServiceRepoImpl(application: Application, net: Network) : ServiceRepo

// Feature deps
class FooData
class FooRepo(val id: FooData, val net: Network)
class FooFeature(val repo: FooRepo, val serviceRepo: ServiceRepo)
```
### 4. Create some components in Main Library
``` kotlin
// Main Component
class AppComponent(
    deps: Deps
): BaseComponent<AppComponent.Deps>(deps) {
    interface Deps {
        val seed: Seed
        val network: Network
        val serviceRepo: ServiceRepo
    }
}

// Feature Component (have to provide somethink)
class FooComponent(
    deps: Deps
) : ProviderComponent<FooComponent.Deps, FooFeature>(deps) {
    override fun provide() = FooFeature(deps.repo, deps.serviceRepo)
    
    // If compoents depends on another module you have to inherit it deps
    interface Deps : AppComponent.Deps {
        val repo: FooRepo
    }
}
```

### 5. Create Component Provider in Main Library
``` kotlin
class AppComponentProvider(
    private val application: Application
) : ComponentProvider() {

    private val wrapperApp by componentWrapper<AppComponent>()
    private val wrapperFoo by componentWrapper<FooComponent>()

    fun getApp(): AppComponent {
        return wrapperApp.getOrCreate(lifecycle) {
            AppComponent(
                object : AppComponent.Deps {
                    // with component
                    private val num = 12

                    // each time new
                    override val seed: Seed by newDeps { Seed(num) }

                    // first call
                    override val network by lazyDeps { Network(seed) }

                    // ref-counter
                    override val serviceRepo: ServiceRepo by rcDeps { ServiceRepoImpl(application, network) }
                }
            )
        }
    }

    fun getFoo(id: FooData): FooComponent {
        return wrapperFoo.getOrCreate(lifecycle) {
            FooComponent(
                object : FooComponent.Deps, AppComponent.Deps by getApp().delegate() {
                    override val repo by rcDeps { FooRepo(id, network) }
                }
            )
        }
    }
}
```

### 6. Create Injector in each Secondary Module
``` kotlin
interface ManDelegate {
    fun provideFoo(data: FooData): FooFeature
    fun provideBar(data: FooData): BarFeature
}

object ModInjector : Injector<ManDelegate>()
```

### 7. Init Injector in Main Module

``` kotlin
class Application : ComponentLifecycle {

    fun onCreate() {
        val app = this

        DependencyRegistry(
            provider = AppComponentProvider(app)
        ).apply {
            // Create deps and component immediately
            create(app, AppComponentProvider::getApp)

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
```


### 8. Get your dependencies in each Secondary Module
``` kotlin
class FooFragment : ComponentLifecycle {
    fun onAttach() {
        val feature = ModInjector.injectWith(this) { provideFoo(FooData()) }
    }
}
```
