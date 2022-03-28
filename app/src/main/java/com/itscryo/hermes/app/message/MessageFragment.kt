package com.itscryo.hermes.app.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.itscryo.hermes.app.message.viewmodels.MessageViewModel
import com.itscryo.hermes.app.message.viewmodels.MessageViewModelFactory
import com.itscryo.hermes.databinding.FragmentMessageBinding


class MessageFragment : Fragment() {
	private lateinit var binding: FragmentMessageBinding
	private lateinit var viewModel: MessageViewModel
	private lateinit var viewModelFactory: MessageViewModelFactory

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentMessageBinding.inflate(inflater)
		viewModelFactory = MessageViewModelFactory()
		viewModel = ViewModelProvider(
			this, viewModelFactory
		)[MessageViewModel::class.java]
		viewModel.userName.value = "Monte Carlo"
		viewModel.userOnlineStatus.value = "online"
		binding.userData = viewModel
		binding.lifecycleOwner = this

		return binding.root
	}

}