package com.itscryo.hermes.app.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.itscryo.hermes.R
import com.itscryo.hermes.app.inbox.item_recycler_view.InboxRVAdapter
import com.itscryo.hermes.app.inbox.viewmodels.InboxViewModel
import com.itscryo.hermes.app.inbox.viewmodels.InboxViewModelFactory
import com.itscryo.hermes.databinding.FragmentInboxBinding
import com.itscryo.hermes.domain.IFirestoreRepository
import com.itscryo.hermes.repository.MessageDBRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InboxFragment : Fragment() {
	private lateinit var binding: FragmentInboxBinding
	private lateinit var viewModel: InboxViewModel;
	private lateinit var viewModelFactory: InboxViewModelFactory


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentInboxBinding.inflate(inflater)
		val adapter = InboxRVAdapter()
		viewModelFactory = InboxViewModelFactory(requireActivity().application)
		viewModel = ViewModelProvider(
			this,
			viewModelFactory
		)[InboxViewModel::class.java]


		lifecycleScope.launch {
			viewModel.initAsync()
		viewModel.messageList.collect {

				adapter.submitList(it)

			}

		}

		binding.messageList.adapter = adapter

//		this.findNavController().navigate(R.id.action_inboxFragment_to_messageFragment)

		return binding.root
	}

/*	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val toolbar = view.findViewById<Toolbar>(R.id.inbox_toolbar)
		toolbar.inflateMenu(R.menu.inbox_menu)
	}*/


}