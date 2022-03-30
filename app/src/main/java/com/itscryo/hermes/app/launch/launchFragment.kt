package com.itscryo.hermes.app.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.itscryo.hermes.R
import com.itscryo.hermes.app.launch.viewmodels.launchViewModel
import com.itscryo.hermes.app.launch.viewmodels.launchViewModelFactory
import com.itscryo.hermes.databinding.FragmentLaunchBinding
import com.itscryo.hermes.domain.IFirestoreRepository
import com.itscryo.hermes.domain.ILocalRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class launchFragment : Fragment() {
	private lateinit var viewModel: launchViewModel;
	private lateinit var viewModelFactory: launchViewModelFactory

	@Inject
	lateinit var localRepo: ILocalRepository

	@Inject
	lateinit var firestoreRepo: IFirestoreRepository

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val binding = FragmentLaunchBinding.inflate(inflater);
		viewModelFactory =
			launchViewModelFactory(requireActivity().application, localRepo, firestoreRepo)
		viewModel = ViewModelProvider(
			this,
			viewModelFactory
		)[launchViewModel::class.java]
		binding.version = viewModel.version

		return binding.root
	}

	override fun onStart() {
		super.onStart()
		viewLifecycleOwner.lifecycleScope.launch {
			viewModel.startupTasks()
			navigate()
		}
	}

	companion object {
		fun newInstance() = launchFragment()
	}
	private fun navigate() {
		if (viewModel.isLoggedIn)
			this.findNavController()
				.navigate(R.id.action_launchFragment_to_inboxFragment)
		else
			this.findNavController()
				.navigate(R.id.action_launchFragment_to_splashFragment)

	}
}
