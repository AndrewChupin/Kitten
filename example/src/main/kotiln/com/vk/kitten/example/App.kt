package kotiln.com.vk.kitten.example

import com.vk.kitten.*

// Activity recreate -
// Multi Deps within single component -
// Compose inject -
// ViewModel wrapper - val vm by viewModel { ModInjector.inject { provideBar(FooData()) } }
// provide?

///////
// APP
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

// DI PROVIDER
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
