package com.itscryo.hermes.app.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.itscryo.hermes.app.inbox.item_recycler_view.InboxRVAdapter
import com.itscryo.hermes.app.inbox.viewmodels.MessageModel
import com.itscryo.hermes.app.inbox.viewmodels.MessageViewModelFactory
import com.itscryo.hermes.databinding.FragmentInboxBinding
import com.itscryo.hermes.domain.IFirestoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InboxFragment : Fragment() {
	private lateinit var binding: FragmentInboxBinding
	private lateinit var viewModel: MessageModel;
	private lateinit var viewModelFactory: MessageViewModelFactory

	@Inject
	lateinit var firestoreService: IFirestoreRepository

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentInboxBinding.inflate(inflater)
		val adapter = InboxRVAdapter()
		viewModelFactory = MessageViewModelFactory(requireActivity().application)
		viewModel = ViewModelProvider(
			this,
			viewModelFactory
		)[MessageModel::class.java]


		lifecycleScope.launch {
			viewModel.initAsync()
			viewModel.messageList.collect {

				adapter.submitList(it)

			}

		}

		binding.messageList.adapter = adapter


		return binding.root
	}

/*	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val toolbar = view.findViewById<Toolbar>(R.id.inbox_toolbar)
		toolbar.inflateMenu(R.menu.inbox_menu)
	}*/


}