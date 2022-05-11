package pl.md.cardmanager.ui.login

import pl.md.cardmanager.ui.model.UserRegisterDto

sealed class LoginEvent {
    data class OnRegisterButtonClick(val newUser: UserRegisterDto) : LoginEvent()
    data class OnLoginButtonClick(val username: String, val password: String) : LoginEvent()
    data class OnAuthenticationAttempt(val pin: String) : LoginEvent()
}
