package pl.md.cardmanager.util

import android.app.Activity

sealed class UiEvent {
    object FinishActivity : UiEvent()
    data class ShowToast(
        val message: String
    ) : UiEvent()

}