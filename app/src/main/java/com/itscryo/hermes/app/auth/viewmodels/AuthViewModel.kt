package com.itscryo.hermes.app.auth.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel(){
	val errorText = MutableLiveData<String>()

}