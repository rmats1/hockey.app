package com.example.hockey_app.ui.screens.register

import com.example.hockey_app.data.models.ClubModel
import com.example.hockey_app.data.services.DataService
import com.example.hockey_app.domain.auth.AuthRepository
import com.example.hockey_app.domain.auth.RegisterUserUseCase
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val authRepository = mockk<AuthRepository>()
    private val dataService = mockk<DataService>()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { dataService.getClubes() } returns emptyList()
        viewModel = RegisterViewModel(RegisterUserUseCase(authRepository), dataService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register without club returns validation error`() = runTest {
        viewModel.register()

        viewModel.state.value shouldBe RegisterState.Error("Por favor selecciona tu club")
    }

    @Test
    fun `player without division returns validation error`() = runTest {
        viewModel.selectedClub.value = ClubModel("club-1", "Club 1")

        viewModel.register()

        viewModel.state.value shouldBe RegisterState.Error("Por favor selecciona tu división")
    }

    @Test
    fun `mismatched passwords return validation error`() = runTest {
        viewModel.selectedClub.value = ClubModel("club-1", "Club 1")
        viewModel.division.value = "Primera"
        viewModel.password.value = "one"
        viewModel.confirmPassword.value = "two"

        viewModel.register()

        viewModel.state.value shouldBe RegisterState.Error("Las contraseñas no coinciden")
    }

    @Test
    fun `valid registration reaches success`() = runTest {
        val club = ClubModel("club-1", "Club 1", "crest.png")
        viewModel.selectedClub.value = club
        viewModel.division.value = "Primera"
        viewModel.email.value = "test@example.com"
        viewModel.name.value = "Test User"
        viewModel.password.value = "password123"
        viewModel.confirmPassword.value = "password123"
        coEvery { authRepository.signUpWithEmail(any(), any(), any()) } returns Result.success(Unit)

        viewModel.register()

        viewModel.state.value shouldBe RegisterState.Success
    }
}
