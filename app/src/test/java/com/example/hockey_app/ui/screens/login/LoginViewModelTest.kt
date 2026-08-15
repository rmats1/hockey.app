package com.example.hockey_app.ui.screens.login

import app.cash.turbine.test
import com.example.hockey_app.domain.auth.AuthRepository
import com.example.hockey_app.domain.auth.SignInUseCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val authRepository = mockk<AuthRepository>()
    private val signIn = SignInUseCase(authRepository)
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.sessionStatus } returns MutableStateFlow(SessionStatus.Initializing)
        viewModel = LoginViewModel(authRepository, signIn)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when email and password are valid login should succeed`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        
        coEvery { authRepository.signInWithEmail(email, password) } returns Result.success(Unit)
        
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        
        viewModel.state.test {
            awaitItem() shouldBe LoginState.Idle
            viewModel.login()
            awaitItem() shouldBe LoginState.Success
        }
    }

    @Test
    fun `when fields are blank login should return error`() = runTest {
        viewModel.state.test {
            awaitItem() shouldBe LoginState.Idle
            viewModel.login()
            val error = awaitItem() as LoginState.Error
            error.message shouldBe "Por favor completa todos los campos"
        }
    }
}
