package pl.md.cardmanager.ui.login

import pl.md.cardmanager.ui.model.UserRegisterDto

sealed class LoginEvent {
    data class OnLoginButtonClick(val newUser: UserRegisterDto) : LoginEvent()
    data class OnAuthenticationAttempt(val pin: String) : LoginEvent()
    object OnTooMuchFailedAttempt : LoginEvent()
}
