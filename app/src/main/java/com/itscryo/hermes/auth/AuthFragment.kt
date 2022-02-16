package com.itscryo.hermes.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.itscryo.hermes.R
import com.itscryo.hermes.databinding.FragmentAuthBinding
import com.itscryo.hermes.model.AuthUserData
import com.itscryo.hermes.service.FirebaseAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception


class AuthFragment : Fragment(), CoroutineScope by MainScope() {
	private lateinit var binding: FragmentAuthBinding
	private lateinit var viewModel: AuthViewModel
	private lateinit var viewModelFactory: AuthViewModelFactory

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding= FragmentAuthBinding.inflate(inflater)
		viewModelFactory= AuthViewModelFactory()
		viewModel= ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
		binding.submitButton.setOnClickListener(::submitButtonHandler)
		return binding.root;
	}

	companion object {
		fun newInstance() = AuthFragment()
	}

	private fun submitButtonHandler(view: View) {
//		val email= binding.editEmail.text.toString()
//		val pass= binding.editPassword.text.toString()
		val email="fizzyfuse31@gmail.com"
		val pass= "Poppu"
		val authUser= AuthUserData(email, pass)
		val authLib= FirebaseAuthRepository.create()
		lifecycleScope.launch {
			try {
				val result = authLib.signInAsync(authUser).await()
			}
			catch(e: Exception)
			{
				binding.errorBox.text=e.message

				println("Reason is ${e.message}")
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		cancel()
	}
}