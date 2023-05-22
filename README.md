# Kitten
Kotlin DI fast and safe library

<img src="https://i.pinimg.com/236x/ae/a3/5a/aea35a7874af4c09d2ee73998d8f8b6d.jpg">

## Why?
This library fit to small and espesially **huge** projects. You can use it for multimodule Kotlin/Java project.

Advantages of this libraries:
- **Lightweight** - the entire library takes only 5 KB space
- **Fast** - this library **doesn't use Codegen or Reflection**, only your code
- **Api/Core Modules** - you can connect super-lightweight **api** module to feature libraries, and **core** module for main library
- **Safe** - unlike Dagger 2, kodin or koin you have to write all implmentation of objects, but API of this library really short
- **Simple** - it's probably takes less code than Dagger 2
- **Lifecycle Management** - there are a lot of helpers in library to mange lifecyle of **components/deps set/dep**

## How to make module system?
Commonly you don't have to create a lot of modules in your application, especially if you are using Gradle.
</br>
Try to create modules like a group of features. If some screen/parts are using in several modules, you can move it to common module.
</br>
Eventially your module system should looks like this.
![image](https://user-images.githubusercontent.com/15245196/155395076-9c6e679d-3444-4455-9c8c-2d9e1903e480.png)


## Guide
### 1. Add ":core" dependency to your Main Library (Application Entrypoint)
``` kotlin
implementation("com.vk.kitten:core:1.0.0")
```
### 2. Add ":api" dependency to your Secondary Modules (Feature Entrypoint)
``` kotlin
implementation("com.vk.kitten:api:1.0.0")
```
### 3. Create some dependecies
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
### 4. Create some components in Main Module
``` kotlin
// Main Component
class AppComponent(
	val serviceDeps: ServiceDeps,
): Component {
	interface NetDeps {
		val seed: Seed
		val network: Network
	}

	interface ServiceDeps {
		val netDeps: NetDeps
		val serviceRepo: ServiceRepo
	}
}

// Feature Component (have to provide something)
class FooComponent(
	private val deps: Deps
) : Component {

	fun provideFoo() = FooFeature(deps.repo, deps.serviceDeps.serviceRepo)
	fun provideBar() = BarFeature(deps.serviceDeps.serviceRepo)

    // If component depends on another module you have to inherit it deps
	interface Deps {
		val serviceDeps: AppComponent.ServiceDeps
		val repo: FooRepo
	}
}
```

### 5. Create Component Provider in Main Module
``` kotlin
class AppComponentProvider(
	private val application: Application
) : ComponentProvider() {

	fun getApp(): AppComponent {
		return getOrCreate {
			AppComponent(
				object : AppComponent.ServiceDeps {
					override val netDeps = object : AppComponent.NetDeps {
						private val num = 12 // with component
						override val seed by depNew { Seed(num) } // each time new
						override val network by depLazy { Network(seed) } // first call
					}

					override val serviceRepo by depRc { ServiceRepoImpl(application, netDeps.network) } // ref-counter
				}
			)
		}
	}

	fun getFoo(id: FooData): FooComponent {
        // you can pass key like id to componentWrapper to identify specific component
        // that means you will get different components for different keys
        // by default key is null
		return getOrCreate(id) {
			FooComponent(
				object : FooComponent.Deps {
					override val serviceDeps = getApp().serviceDeps
					override val repo by depRc { FooRepo(id, serviceDeps.netDeps.network) }
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
class Application {

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
					private fun component(data: FooData) = provider.getFoo(data)
					override fun provideFoo(data: FooData) = component(data).provideFoo()
					override fun provideBar(data: FooData): BarFeature = component(data).provideBar()
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
		val feature = ModInjector.injectWith(this) { provideBar(FooData()) }
		// or short example
		val feature1 = ModInjector.inject { provideBar(FooData()) }
		// or viewModel short example
		val viewModel = ModInjector.viewModel { provideBar(FooData()) }
    }
}
```
