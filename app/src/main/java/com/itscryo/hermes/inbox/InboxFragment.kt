package com.itscryo.hermes.inbox

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import  com.itscryo.hermes.R
import com.itscryo.hermes.databinding.FragmentInboxBinding

class InboxFragment : Fragment() {
private lateinit var binding: FragmentInboxBinding
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding= FragmentInboxBinding.inflate(inflater)

		this.setHasOptionsMenu(true)
		return binding.root
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

		inflater.inflate(R.menu.inbox_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)


	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {

		return true;
	}

}