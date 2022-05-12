package pl.md.cardmanager.util

sealed class UiEvent {
    object FinishActivity : UiEvent()
    data class ShowToast(
        val message: String
    ) : UiEvent()

}