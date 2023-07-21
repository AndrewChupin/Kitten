package org.openwallet.kitten.example

import org.openwallet.kitten.core.ComponentProvider
import org.openwallet.kitten.core.Kitten
import org.openwallet.kitten.core.depLazy
import org.openwallet.kitten.core.depRc

///////
// APP
class Application {

	fun onCreate() {
		Kitten.init(
			provider = AppComponentProvider(this)
		) {  deps ->
			// Create deps and component immediately
			create { deps.app }

			// Init delegate without deps and components
			register(ModInjector) {
				object : ManDelegate {
					private fun component(data: FooData) = deps.getFoo(data)
					override fun provideFoo(data: FooData) = FooFeature(component(data).repo, deps.app.service.repo)
					override fun provideBar(data: FooData): BarFeature = BarFeature(component(data).service.repo)
				}
			}
		}
	}
}


// DI PROVIDER
class AppComponentProvider(
	private val application: Application
) : ComponentProvider() {

	fun getFoo(id: FooData): FooComponent {
		return getOrCreate(id) {
			object : FooComponent {
				override val service = app.service
				override val repo by depRc { FooRepo(id, service.net.network) }
			}
		}
	}

	val app get() = getOrCreate {
		AppComponent(
			object : AppComponent.Service {
				override val net = object : AppComponent.Net {
					private val num = 12 // with component
					override val seed by depLazy { Seed(num) } // each time new
					override val network by depLazy { Network(seed) } // first call
				}

				override val repo by depRc { ServiceRepoImpl(application, net.network) } // ref-counter
			}
		)
	}
}
