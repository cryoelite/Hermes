package com.itscryo.hermes.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.itscryo.hermes.R
import com.itscryo.hermes.databinding.FragmentLaunchBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class launchFragment : Fragment() {
	private lateinit var viewModel: launchViewModel;
	private lateinit var viewModelFactory: launchViewModelFactory

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val binding = FragmentLaunchBinding.inflate(inflater);
		viewModelFactory =
			launchViewModelFactory(requireActivity().application)
		viewModel = ViewModelProvider(
			this,
			viewModelFactory
		)[launchViewModel::class.java]
		binding.version = viewModel.version

		return binding.root
	}

	override fun onStart() {
		super.onStart()
		// Do all the startup tasks
		viewLifecycleOwner.lifecycleScope.launch {
			viewModel.startupTasks()
			navigate()
		}
	}

	companion object {
		fun newInstance() = launchFragment()
	}
//TODO REVERT boolean check
	private fun navigate() {
		if (!viewModel.isLoggedIn)
			this.findNavController()
				.navigate(R.id.action_launchFragment_to_inboxFragment)
		else
			this.findNavController()
				.navigate(R.id.action_launchFragment_to_splashFragment)

	}
}
