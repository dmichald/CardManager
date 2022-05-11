package pl.md.cardmanager.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.md.cardmanager.data.repository.UserRepository
import pl.md.cardmanager.ui.model.UserRegisterDto
import pl.md.cardmanager.util.UiEvent
import pl.md.cardmanager.util.UserUtils
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepository,
) : ViewModel() {
    val loggedUserId = MutableLiveData<Long>()
    val authPassed = MutableLiveData<Boolean>()
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnRegisterButtonClick -> {
                viewModelScope.launch {
                    validateUser(event.newUser)
                }
            }
            is LoginEvent.OnAuthenticationAttempt -> {
                viewModelScope.launch {
                    validatePin(event.pin)
                }
            }

            is LoginEvent.OnLoginButtonClick -> {
                viewModelScope.launch {
                    validateUserCredentials(event.username, event.password)
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private suspend fun validateUser(newUser: UserRegisterDto) {
        val userName = newUser.username
        val password = newUser.password
        val pin = newUser.pin
        if (userName.isBlank() || password.isBlank() || pin.isBlank()) {
            sendUiEvent(UiEvent.ShowSnackbar(message = "Uzupełnij wszystkie pola."))
            loggedUserId.value = UserUtils.NO_USER
        } else if (isUserExists(newUser)) {
            sendUiEvent(UiEvent.ShowSnackbar(message = "Uzytkownik o takiej nazwie istnieje. Podaj inna."))
            loggedUserId.value = UserUtils.NO_USER
        } else if (newUser.password.trim().length < 10) {
            sendUiEvent(UiEvent.ShowSnackbar(message = "Haslo musi miec minimum 10 znakow."))
            loggedUserId.value = UserUtils.NO_USER
        } else if (newUser.pin.trim().length < 5) {
            sendUiEvent(UiEvent.ShowSnackbar(message = "Pin musi miec co najmniej 4 znaki."))
            loggedUserId.value = UserUtils.NO_USER
        } else {
            val userId = userRepo.insertUser(newUser)
            loggedUserId.value = userId
        }
    }

    private suspend fun isUserExists(user: UserRegisterDto): Boolean {
        return userRepo.getUserByName(user.username) != null
    }

    private suspend fun validatePin(pin: String) {
        val incorrectLoginAttempts =
            userRepo.getFailedAuthenticationAttemptsCount(UserUtils.loggedUserId)
        val user = userRepo.getUserById(UserUtils.loggedUserId)
        if (pin.trim().isBlank()) {
            sendUiEvent(UiEvent.ShowSnackbar(message = "Uzupełnij pola."))
            authPassed.value = false
        } else if (incorrectLoginAttempts <= 3) {
            if (user.pin == pin) {
                authPassed.value = true
            } else {
                authPassed.value = false
                userRepo.addFailedLoginAttempt(UserUtils.loggedUserId)
                sendUiEvent(UiEvent.ShowSnackbar(message = "Niepoprawny pin"))
            }
        } else {
            sendUiEvent(UiEvent.ShowSnackbar(message = "Przekroczono możliwą ilość logowań z użyciem pinu. Użyj hasła."))
            if (user.password == pin) {
                authPassed.value = true
                userRepo.resetFailedLoginAttempts(UserUtils.loggedUserId)
                sendUiEvent(UiEvent.ShowSnackbar("Konto utworzone. Zaloguj się."))

            } else {
                authPassed.value = false
                sendUiEvent(UiEvent.ShowSnackbar("Niepoprawne hasło"))
            }
        }

    }

    private suspend fun validateUserCredentials(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            authPassed.value = false
            sendUiEvent(UiEvent.ShowSnackbar("Niepoprawne dane logowania"))
            return
        }
        val user = userRepo.getUser(username, password)
        if (user == null) {
            sendUiEvent(UiEvent.ShowSnackbar("Niepoprawne dane logowania"))
        } else {
            loggedUserId.value = user.id
        }
    }

}

