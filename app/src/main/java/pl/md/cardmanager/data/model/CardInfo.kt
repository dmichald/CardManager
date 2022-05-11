package pl.md.cardmanager.data.model

class CardInfo(val id: Long, val name: String, var number: String) {
    init {
        number = "*** " + number.takeLast(4)
    }
}