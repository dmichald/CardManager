package pl.md.cardmanager.ui.add_edit_card

import pl.md.cardmanager.ui.dto.CreditCardDto

sealed class AddEditCardEvent {
    data class OnSaveButtonClick(val creditCard: CreditCardDto) : AddEditCardEvent()
    object OnCloseButtonClick : AddEditCardEvent()
}