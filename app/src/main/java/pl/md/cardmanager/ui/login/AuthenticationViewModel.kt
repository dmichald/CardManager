package pl.md.cardmanager.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.md.cardmanager.MainActivity
import pl.md.cardmanager.data.repository.UserRepository
import pl.md.cardmanager.ui.model.UserRegisterDto
import pl.md.cardmanager.util.UiEvent
import pl.md.cardmanager.util.UserUtils
import pl.md.cardmanager.util.crypto.BcryptUtil
import pl.md.cardmanager.util.crypto.EnCryptor
import pl.md.cardmanager.util.crypto.KeyStoreUtil
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val userRepo: UserRepository,
) : ViewModel() {
    companion object {
        val TAG = AuthenticationViewModel::class.java.toString()
    }

    val loggedUserId = MutableLiveData(UserUtils.NO_USER.toString())
    val authPassed = MutableLiveData(false)
    val emptyGuid = "00000000-0000-0000-0000-000000000000"
    val loggedUserKeyAlias = MutableLiveData(emptyGuid)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var error by mutableStateOf("")
        private set
    var usePasswordError by mutableStateOf("")
        private set

    var pinPasswordLabel by mutableStateOf("Pin")
        private set


    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnRegisterButtonClick -> {
                viewModelScope.launch {
                    validateNewuser(event.newUser)
                }
            }
            is LoginEvent.OnAuthenticationAttempt -> {
                viewModelScope.launch {
                    validatePin(event.pin)
                }
            }

            is LoginEvent.OnLoginButtonClick -> {
                viewModelScope.launch {
                    validateExisitngUser(event.username, event.password)
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private suspend fun validateNewuser(newUser: UserRegisterDto) {
        val userName = newUser.username
        val password = newUser.password
        val pin = newUser.pin
        if (userName.isBlank() || password.isBlank() || pin.isBlank()) {
            error = "Uzupełnij wszystkie pola."
        } else if (isUserExists(newUser)) {
            error = "Użytkownik o takiej nazwie istnieje. Podaj inną."
        } else if (newUser.password.trim().length <= 10) {
            error = "Haslo musi mieć minimum 10 znaków."
        } else if (newUser.pin.trim().length <= 4) {
            error = "Pin musi miec minimum 4 znaki."
        } else {
            val encryptedId = createUser(newUser)
            loggedUserId.value = encryptedId
            sendUiEvent(UiEvent.ShowToast(message = "Użytkownik dodany."))
        }
    }

    private suspend fun createUser(dto: UserRegisterDto): String {
        dto.password = BcryptUtil.hash(dto.password)
        dto.pin = BcryptUtil.hash(dto.pin)
        val createdUser = userRepo.insertUser(dto)
        val userKeyAlias = createdUser.userKeyAlias
        val key = KeyStoreUtil.generateAndSave(userKeyAlias)
        val encryptorResult = EnCryptor.encryptText(key, createdUser.id.toString())
        val encryptedId =
            encryptorResult.encryptionAsString + "#" + encryptorResult.encryptionIVAsString
        userRepo.insertUserEncryptedPassword(createdUser.id!!, encryptedId)

        return encryptedId
    }

    private suspend fun isUserExists(user: UserRegisterDto): Boolean {
        return userRepo.getUserByName(user.username) != null
    }

    private suspend fun validatePin(pin: String) {
        val incorrectLoginAttempts =
            userRepo.getFailedAuthenticationAttemptsCount(UserUtils.loggedUserId)
        val user = userRepo.getUserById(UserUtils.loggedUserId)
        if (pin.trim().isBlank()) {
            error = "Uzupełnij wszystkie pola."
            authPassed.value = false
        } else if (incorrectLoginAttempts <= 3) {
            if (BcryptUtil.verify(pin, user.pin)) {
                authPassed.value = true
            } else {
                authPassed.value = false
                userRepo.addFailedLoginAttempt(UserUtils.loggedUserId)
                error = "Niepoprawny pin"
            }
        } else {
            usePasswordError =
                "Przekroczono możliwą ilość niepoprawnych logowań z użyciem pinu. Użyj hasła."
            pinPasswordLabel = "Hasło"
            if (BcryptUtil.verify(pin, user.password)) {
                authPassed.value = true
                userRepo.resetFailedLoginAttempts(UserUtils.loggedUserId)

            } else {
                authPassed.value = false
                error = "Niepoprawne hasło"
            }
        }

    }

    private suspend fun validateExisitngUser(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            error = "Uzupełnij wszystkie pola."
            return
        }
        val user = userRepo.getUserByName(username)
        if (user == null) {
            error = "Niepoprawne dane logowania"
        } else {
            if (BcryptUtil.verify(password, user.password)) {
                loggedUserKeyAlias.value = user.userKeyAlias
                loggedUserId.value = user.encryptedUserId
            } else {
                error = "Niepoprawne dane logowania"
            }
        }
    }

}

