package com.itscryo.hermes.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel(){
	val errorText = MutableLiveData<String>()

}