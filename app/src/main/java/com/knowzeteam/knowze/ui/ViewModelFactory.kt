package com.knowzeteam.knowze.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.knowzeteam.knowze.data.remote.retrofit.ApiService
import com.knowzeteam.knowze.di.ServiceLocator
import com.knowzeteam.knowze.ui.screen.auth.login.LoginViewModel
import com.knowzeteam.knowze.ui.screen.home.GenerateViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Provide ApiService
            val apiService = ServiceLocator.provideApiService(context)

            // Provide UserRepository
            val userRepository = ServiceLocator.provideUserRepository(context)
            val tokenRepository = ServiceLocator.provideTokenRepository(context)


            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(apiService, userRepository, tokenRepository ) as T
        }
        if (modelClass.isAssignableFrom(GenerateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val generateRepository = ServiceLocator.provideGenerateRepository(context)
            return GenerateViewModel(generateRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}