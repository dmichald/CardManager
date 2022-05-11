package pl.md.cardmanager.ui.model

import pl.md.cardmanager.util.UserUtils


data class CreditCardDto(
    val id: Long? = null,
    val name: String = "",
    val number: String = "",
    val CVC: String = "",
    val ownerName: String = "",
    val expirationMonth: String = "",
    val expirationYear: String = "",
    var userId: Long = UserUtils.loggedUserId
) {
}
