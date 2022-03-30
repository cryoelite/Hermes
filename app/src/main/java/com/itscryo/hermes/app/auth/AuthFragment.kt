package com.itscryo.hermes.app.auth

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.itscryo.hermes.R
import com.itscryo.hermes.app.auth.viewmodels.AuthViewModel
import com.itscryo.hermes.app.auth.viewmodels.AuthViewModelFactory
import com.itscryo.hermes.databinding.FragmentAuthBinding
import com.itscryo.hermes.domain.IAuthRepository
import com.itscryo.hermes.domain.IFirestoreRepository
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.global_model.message_db_model.UserWithImage
import com.itscryo.hermes.repository.FirestoreRepository
import com.itscryo.hermes.repository.MessageDBRepository
import com.itscryo.hermes.service.MessageDBService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
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

	private var messageDBService: MessageDBService? = null

	@Inject
	lateinit var db: MessageDBRepository

	@Inject
	lateinit var firestoreRepository: IFirestoreRepository

	private val connection = object : ServiceConnection {
		override fun onServiceConnected(className: ComponentName, service: IBinder) {
			val binder = service as MessageDBService.LocalBinder
			messageDBService = binder.getService()

		}

		override fun onServiceDisconnected(arg0: ComponentName) {
			messageDBService = null
		}

	}

	private suspend fun <T> withBind(lambda: suspend () -> T): T {
		bind()
		val result = lambda()
		unbind()
		return result
	}

	private fun bind() {
		Intent(requireContext(), MessageDBService::class.java).also { intent ->
			requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
		}
	}

	private fun unbind() {
		requireContext().unbindService(connection)
	}

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
		lifecycleScope.launch {
			try {
				val result = authRepo.signInAsync(email, pass).await()
				localRepo.storeUserCredAsync(result, requireContext())
				withBind {
					messageDBService?.generateUser(email,result)
				}
				if(firestoreRepository.getUserInfo(result)==null)
				{
					val user= db.getUserAsync(result).first() ?: throw Throwable("Failed to get stored user")
					val image= db.getUserImageAsync(user.userImageID).first() ?: throw Throwable("Failed to get user image")
					firestoreRepository.storeUserInfo(UserWithImage(user,image))
				}

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
		if(messageDBService!=null){
			unbind()
		}
		super.onDestroy()
	}
}