package com.itscryo.hermes.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.itscryo.hermes.R
import com.itscryo.hermes.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val binding= FragmentSplashBinding.inflate(inflater)
		binding.splashNextButton.setOnClickListener(::onClickNext)
		return  binding.root
	}

	companion object {

		fun newInstance() = SplashFragment()
	}

	fun onClickNext(view: View) = this.findNavController().navigate(R.id.action_splashFragment_to_authFragment)
}