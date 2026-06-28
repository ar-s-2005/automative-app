package com.example.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebase.FirebaseManager
import com.example.models.UserProfile

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val profile: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _loginState = MutableLiveData<AuthState>(AuthState.Idle)
    val loginState: LiveData<AuthState> get() = _loginState

    private val _signupState = MutableLiveData<AuthState>(AuthState.Idle)
    val signupState: LiveData<AuthState> get() = _signupState

    private val _forgotState = MutableLiveData<AuthState>(AuthState.Idle)
    val forgotState: LiveData<AuthState> get() = _forgotState

    private val _currentUser = MutableLiveData<UserProfile?>()
    val currentUser: LiveData<UserProfile?> get() = _currentUser

    init {
        FirebaseManager.init(application)
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val user = FirebaseManager.getCurrentUser(getApplication())
        _currentUser.value = user
    }

    fun login(email: String, password: String) {
        _loginState.value = AuthState.Loading
        FirebaseManager.login(email, password,
            onSuccess = { profile ->
                _currentUser.value = profile
                _loginState.value = AuthState.Success(profile)
            },
            onFailure = { error ->
                _loginState.value = AuthState.Error(error.localizedMessage ?: "Unknown Error")
            }
        )
    }

    fun signup(name: String, email: String, password: String) {
        _signupState.value = AuthState.Loading
        FirebaseManager.signup(name, email, password,
            onSuccess = { profile ->
                _currentUser.value = profile
                _signupState.value = AuthState.Success(profile)
            },
            onFailure = { error ->
                _signupState.value = AuthState.Error(error.localizedMessage ?: "Unknown Error")
            }
        )
    }

    fun forgotPassword(email: String) {
        _forgotState.value = AuthState.Loading
        FirebaseManager.forgotPassword(email,
            onSuccess = {
                _forgotState.value = AuthState.Success(UserProfile(email = email))
            },
            onFailure = { error ->
                _forgotState.value = AuthState.Error(error.localizedMessage ?: "Unknown Error")
            }
        )
    }

    fun logout() {
        FirebaseManager.logout()
        _currentUser.value = null
        _loginState.value = AuthState.Idle
        _signupState.value = AuthState.Idle
    }
}
