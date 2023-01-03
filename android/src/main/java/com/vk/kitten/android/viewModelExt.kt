package com.vk.kitten.android

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vk.kitten.Injector

@Composable
inline fun <Delegate : Any, reified Subject : ViewModel> Injector<Delegate>.viewModel(
	viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
	{ "No LifecycleOwner was provided via LocalLifecycleOwner" },
	key: String? = null,
	noinline factory: Delegate.(CreationExtras) -> Subject
): Subject {
	return viewModel(
		viewModelStoreOwner = viewModelStoreOwner,
		key = key,
		initializer = initializer@ {
			injectWith(viewModelStoreOwner) { factory(this, this@initializer) }
		}
	)
}

context(ViewModelStoreOwner)
inline fun <Delegate : Any, reified Subject : ViewModel> Injector<Delegate>.viewModel(
	key: String? = null,
	noinline factory: Delegate.(CreationExtras) -> Subject
): Subject {
	val viewModelStoreOwner = this@ViewModelStoreOwner
	val clazz = Subject::class.java

	val provider = ViewModelProvider(
		viewModelStore,
		viewModelFactory {
			initializer {
				injectWith(viewModelStoreOwner) {
					factory(this, this@initializer)
				}
			}
	    },
		if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
			viewModelStoreOwner.defaultViewModelCreationExtras
		} else {
			CreationExtras.Empty
		}
	)

	if (key != null) {
		return provider[key, clazz]
	}

	return provider[clazz]
}
