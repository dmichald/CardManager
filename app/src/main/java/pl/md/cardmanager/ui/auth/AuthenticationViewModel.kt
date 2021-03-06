package pl.md.cardmanager.ui.auth

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
import pl.md.cardmanager.data.repository.UserRepository
import pl.md.cardmanager.ui.dto.UserRegisterDto
import pl.md.cardmanager.util.Constants
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

    val loggedUserId = MutableLiveData(UserUtils.NO_USER.toString())
    val authPassed = MutableLiveData(false)
    val loggedUserKeyAlias = MutableLiveData(Constants.EMPTY_GUID)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var error by mutableStateOf("")
        private set
    var usePasswordError by mutableStateOf("")
        private set

    var pinPasswordLabel by mutableStateOf("Pin")
        private set


    fun onEvent(event: AuthenticationEvent) {
        when (event) {
            is AuthenticationEvent.OnRegisterButtonClick -> {
                viewModelScope.launch {
                    validateNewuser(event.newUser)
                }
            }
            is AuthenticationEvent.OnAuthenticationAttempt -> {
                viewModelScope.launch {
                    validatePin(event.pin)
                }
            }

            is AuthenticationEvent.OnLoginButtonClick -> {
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
            error = "Uzupe??nij wszystkie pola."
        } else if (isUserExists(newUser)) {
            error = "U??ytkownik o takiej nazwie istnieje. Podaj inn??."
        } else if (newUser.password.trim().length <= 10) {
            error = "Haslo musi mie?? minimum 10 znak??w."
        } else if (newUser.pin.trim().length <= 4) {
            error = "Pin musi miec minimum 4 znaki."
        } else {
            val encryptedId = createUser(newUser)
            loggedUserId.value = encryptedId
            sendUiEvent(UiEvent.ShowToast(message = "U??ytkownik dodany."))
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
            error = "Uzupe??nij wszystkie pola."
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
                "Przekroczono mo??liw?? ilo???? niepoprawnych logowa?? z u??yciem pinu. U??yj has??a."
            pinPasswordLabel = "Has??o"
            if (BcryptUtil.verify(pin, user.password)) {
                authPassed.value = true
                userRepo.resetFailedLoginAttempts(UserUtils.loggedUserId)

            } else {
                authPassed.value = false
                error = "Niepoprawne has??o"
            }
        }

    }

    private suspend fun validateExisitngUser(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            error = "Uzupe??nij wszystkie pola."
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

