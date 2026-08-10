package com.example.hockey_app.ui.screens.login

import app.cash.turbine.test
import com.example.hockey_app.data.services.AuthService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val authService = mockk<AuthService>()
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when email and password are valid login should succeed`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        
        coEvery { authService.signInWithEmail(email, password) } returns Result.success(Unit)
        
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        
        viewModel.state.test {
            awaitItem() shouldBe LoginState.Idle
            viewModel.login()
            awaitItem() shouldBe LoginState.Loading
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
