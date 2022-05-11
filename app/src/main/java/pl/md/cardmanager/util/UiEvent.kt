package pl.md.cardmanager.util

import android.app.Activity

sealed class UiEvent {
    object FinishActivity : UiEvent()
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ) : UiEvent()

}