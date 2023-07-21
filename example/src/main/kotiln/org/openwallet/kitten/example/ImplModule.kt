package org.openwallet.kitten.example

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.openwallet.kitten.android.viewModel
import org.openwallet.kitten.android.viewModelLegacy
import org.openwallet.kitten.api.Component
import org.openwallet.kitten.api.Injector
import org.openwallet.kitten.core.ComponentLifecycle

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
		// or viewModel short example
		val viewModel = ModInjector.viewModelLegacy { provideBar(FooData()) }
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

interface FooComponent : Component {
	val service: AppComponent.Service
	val repo: FooRepo
}
