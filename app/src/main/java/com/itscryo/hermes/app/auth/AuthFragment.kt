package com.itscryo.hermes.app.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.itscryo.hermes.R
import com.itscryo.hermes.app.auth.viewmodel.AuthViewModel
import com.itscryo.hermes.app.auth.viewmodel.AuthViewModelFactory
import com.itscryo.hermes.databinding.FragmentAuthBinding
import com.itscryo.hermes.domain.IAuthRepository
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.global_model.AuthUserData
import com.itscryo.hermes.global_model.UserData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {
	private lateinit var binding: FragmentAuthBinding
	private lateinit var viewModel: AuthViewModel
	private lateinit var viewModelFactory: AuthViewModelFactory
	@Inject
	lateinit var authRepo: IAuthRepository
	@Inject
	lateinit var localRepo: ILocalRepository

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentAuthBinding.inflate(inflater)
		viewModelFactory = AuthViewModelFactory()
		viewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
		binding.submitButton.setOnClickListener(::submitButtonHandler)
		return binding.root;
	}


	private fun submitButtonHandler(view: View) {
		val email = binding.editEmail.text.toString()
		val pass = binding.editPassword.text.toString()
		val authUser = AuthUserData(email, pass)
		lifecycleScope.launch {
			try {
				val result = authRepo.signInAsync(authUser).await()
				localRepo.storeUserCredAsync(UserData(result.userID)).await()
				navigateToInbox()
			} catch (e: Exception) {
				binding.errorBox.text = e.message
				binding.editPassword.text.clear()
			}
		}
	}

	private fun navigateToInbox(){
		this.findNavController()
			.navigate(R.id.action_authFragment_to_inboxFragment)
	}
	override fun onDestroy() {
		super.onDestroy()
	}
}