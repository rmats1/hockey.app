package com.example.hockey_app.ui.screens.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = authService.getCurrentUser() ?: authService.getLocalUser()
        }
    }

    fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                val bitmap = context.contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
                    ?: throw IllegalStateException("No se pudo leer la imagen")
                val output = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 88, output)
                bitmap.recycle()
                authService.uploadProfilePhoto(output.toByteArray())
                    .onSuccess { url -> _user.value = _user.value?.copy(foto_url = url) }
            } catch (_: Exception) {
                // The screen keeps the current photo when the upload fails.
            }
        }
    }
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authService.logout()
            onSuccess()
        }
    }
}
