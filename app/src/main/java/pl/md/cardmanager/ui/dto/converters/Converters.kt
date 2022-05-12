package pl.md.cardmanager.ui.dto.converters

import pl.md.cardmanager.data.model.CreditCard
import pl.md.cardmanager.ui.dto.CreditCardDto
import java.sql.Date

class Converters {
    companion object {
        fun toCreditCard(dto: CreditCardDto): CreditCard {
            return CreditCard(
                dto.name,
                dto.number,
                dto.CVC.toInt(),
                dto.ownerName,
                Date(dto.expirationYear.toInt(), dto.expirationMonth.toInt(), 0),
                dto.userId,
                dto.id
            )
        }

        fun toCreditCardDto(card: CreditCard): CreditCardDto {
            return CreditCardDto(
                card.id,
                card.name,
                card.number,
                card.CVC.toString(),
                card.ownerName,
                card.expirationDate.month.toString(),
                card.expirationDate.year.toString(),
                card.userId
            )
        }
    }
}