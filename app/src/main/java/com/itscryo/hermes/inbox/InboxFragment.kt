package com.itscryo.hermes.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.itscryo.hermes.R
import com.itscryo.hermes.databinding.FragmentInboxBinding
import com.itscryo.hermes.inbox.viewmodels.MessageModel
import com.itscryo.hermes.inbox.viewmodels.MessageViewModelFactory

class InboxFragment : Fragment() {
	private lateinit var binding: FragmentInboxBinding
	private lateinit var viewModel: MessageModel;
	private lateinit var viewModelFactory: MessageViewModelFactory
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentInboxBinding.inflate(inflater)
		val adapter= InboxRVAdapter()
		viewModelFactory= MessageViewModelFactory()
		viewModel = ViewModelProvider(
			this,
			viewModelFactory
		)[MessageModel::class.java]

		viewModel.messageList.observe(viewLifecycleOwner, Observer {
			it?.let {
				adapter.submitList(it)
			}
		})


		binding.messageList.adapter= adapter

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val toolbar = view.findViewById<Toolbar>(R.id.inbox_toolbar)
		toolbar.inflateMenu(R.menu.inbox_menu)
	}


}