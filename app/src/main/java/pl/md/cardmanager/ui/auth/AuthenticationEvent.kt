package pl.md.cardmanager.ui.auth

import pl.md.cardmanager.ui.dto.UserRegisterDto

sealed class AuthenticationEvent {
    data class OnRegisterButtonClick(val newUser: UserRegisterDto) : AuthenticationEvent()
    data class OnLoginButtonClick(val username: String, val password: String) :
        AuthenticationEvent()

    data class OnAuthenticationAttempt(val pin: String) : AuthenticationEvent()
}
