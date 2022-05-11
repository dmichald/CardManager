package pl.md.cardmanager.ui.add_edit_activity
import pl.md.cardmanager.ui.model.CreditCardDto

sealed class AddEditCardEvent {
    data class OnSaveButtonClick(val creditCard: CreditCardDto) : AddEditCardEvent()
}