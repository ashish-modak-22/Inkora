//package com.example.notesapp.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.notesapp.model.RegisterRequest
//import com.example.notesapp.model.RegisterResponse
//import com.example.notesapp.repository.AuthRepository
//import kotlinx.coroutines.launch
//import retrofit2.Response
//
//
//class AuthViewModel: ViewModel() {
//
//    private val repository = AuthRepository()
//
//    private val _registerResponse = MutableLiveData<Response<RegisterResponse>>()
//    val registerResponse: LiveData<Response<RegisterResponse>>
//        get() = _registerResponse
//
//    fun registerUser(request: RegisterRequest) {
//
//        viewModelScope.launch {
//
//            _registerResponse.value = repository.registerUser((request))
//        }
//    }
//}
