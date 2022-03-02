package com.itscryo.hermes.app.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel(){
	val errorText = MutableLiveData<String>()

}