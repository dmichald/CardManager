package pl.md.cardmanager.ui.add_edit_card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.md.cardmanager.data.repository.CreditCardRepository
import pl.md.cardmanager.ui.dto.CreditCardDto
import pl.md.cardmanager.ui.dto.converters.Converters
import pl.md.cardmanager.util.UiEvent
import pl.md.cardmanager.util.UserUtils
import javax.inject.Inject

@HiltViewModel
class AddEditCardViewModel @Inject constructor(
    private val cardRepo: CreditCardRepository
) : ViewModel() {

    var errors by mutableStateOf("")
        private set
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AddEditCardEvent) {
        when (event) {
            is AddEditCardEvent.OnSaveButtonClick -> {
                viewModelScope.launch {
                    validateCard(event.creditCard)
                }
            }
            is AddEditCardEvent.OnCloseButtonClick -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.FinishActivity)
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private suspend fun validateCard(card: CreditCardDto) {
        if (areFieldsEmpty(card)) {
            errors = "Uzupełnij wszystkie pola."
        } else if (card.number.length != 16) {
            errors = "Wprowadz poprawny numner karty (16 cyfr)"
        } else if (!isCVCValid(card.CVC)) {
            errors = "Wprowadz poprawny kod CVC (3 lub 4 cyfry)"
        } else if (!isMonthValid(card.expirationMonth)) {
            errors = "Niepoprawny miesiąc. Wybierz wartość pomiędzy 1 a 12"
        } else if (!isYearValid(card.expirationYear)) {
            errors = "Niepoprawny rok. Wybierz wartość pomiędzy 2000 a 3000"
        } else {
            val creditCard = Converters.toCreditCard(card)
            creditCard.userId = UserUtils.loggedUserId
            cardRepo.insertCard(creditCard)
            sendUiEvent(UiEvent.ShowToast(message = "Karta została zapisana."))
            sendUiEvent(UiEvent.FinishActivity)
        }
    }

    private fun areFieldsEmpty(card: CreditCardDto): Boolean {
        return card.name.isBlank() ||
                card.CVC.isBlank() ||
                card.ownerName.isBlank() ||
                card.number.isBlank() ||
                card.expirationMonth.isBlank() ||
                card.expirationYear.isBlank()
    }

    private fun isMonthValid(m: String): Boolean {
        return m.toInt() in 1..12
    }

    private fun isYearValid(m: String): Boolean {
        return m.toInt() in 2000..3000
    }

    private fun isCVCValid(m: String): Boolean {
        return m.length in 3..4
    }

}