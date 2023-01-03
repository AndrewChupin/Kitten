package kotiln.com.vk.kitten.example

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.vk.kitten.*
import com.vk.kitten.android.viewModel

// IMPL
class ServiceRepoImpl(application: Application, net: Network) : ServiceRepo

///////
// FOO
class FooData
class FooRepo(val id: FooData, val net: Network)
class FooFeature(val repo: FooRepo, val serviceRepo: ServiceRepo) : ViewModel()

class FooFragment : ComponentLifecycle {
	fun onAttach() {
		val feature = ModInjector.injectWith(this) { provideFoo(FooData()) }
		// or short example
		val feature1 = ModInjector.inject { provideFoo(FooData()) }
	}

	fun onContent() {
		// val viewModel by kitten {
		//     ModInjector.inject { provideFoo(FooData()) }
		// }
	}
}

///////
// BAR
class BarFeature(val serviceRepo: ServiceRepo) : ViewModel()

class BarFragment : ComponentLifecycle {
	@Composable
	fun Content() {
		val feature = ModInjector.injectWith(this) { provideBar(FooData()) }
		// or short example
		val feature1 = ModInjector.inject { provideBar(FooData()) }
		// or viewModel short example
		val viewModel = ModInjector.viewModel { provideBar(FooData()) }
	}
}

///////
// DI
interface ManDelegate {
	fun provideFoo(data: FooData): FooFeature
	fun provideBar(data: FooData): BarFeature
}

object ModInjector : Injector<ManDelegate>()

class FooComponent(
	private val deps: Deps
) : Component {

	fun provideFoo() = FooFeature(deps.repo, deps.serviceDeps.serviceRepo)
	fun provideBar() = BarFeature(deps.serviceDeps.serviceRepo)

	interface Deps {
		val serviceDeps: AppComponent.ServiceDeps
		val repo: FooRepo
	}
}
