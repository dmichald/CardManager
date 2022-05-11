package pl.md.cardmanager.ui.add_edit_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.md.cardmanager.MainActivity
import pl.md.cardmanager.data.repository.CreditCardRepository
import pl.md.cardmanager.ui.model.CreditCardDto
import pl.md.cardmanager.util.Converters
import pl.md.cardmanager.util.UiEvent
import pl.md.cardmanager.util.UserUtils
import javax.inject.Inject

@HiltViewModel
class AddEditCardViewModel @Inject constructor(
    private val cardRepo: CreditCardRepository
) : ViewModel() {

   val save = MutableLiveData<Boolean>()
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AddEditCardEvent) {
        when (event) {
            is AddEditCardEvent.OnSaveButtonClick -> {
                viewModelScope.launch {
                    validateCard(event.creditCard)
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
            sendUiEvent(UiEvent.ShowSnackbar(message = "Uzupełnij wszystkie pola."))
            save.value = false
        } else if (card.number.length <5) {
            sendUiEvent(UiEvent.ShowSnackbar(message = "Wprowadz poprawny numner karty (16 cyfr)"))
            save.value = false
        } else if (card.CVC.length < 4) {
            sendUiEvent(UiEvent.ShowSnackbar(message = "Wprowadz poprawny kod CVC (3-4 cyfry)"))
            save.value = false
        } else {
            val creditCard = Converters.toCreditCard(card)
            creditCard.userId = UserUtils.loggedUserId
            cardRepo.insertCard(creditCard)
            sendUiEvent(UiEvent.ShowSnackbar(message = "Karta została dodana."))
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

}