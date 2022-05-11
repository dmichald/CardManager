package pl.md.cardmanager.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.sql.Date

@Serializable
data class CreditCardBackupDto(
    val name: String,
    val number: String,
    val CVC: Int,
    val ownerName: String,
    @Serializable(with = DateSerializer::class)
    val expirationDate: Date,
)

object DateSerializer : KSerializer<Date> {
    override val descriptor = PrimitiveSerialDescriptor("expDate", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date {
        return Date.valueOf(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
data class CreditCardBackupDtoList(val cards: List<CreditCardBackupDto>)