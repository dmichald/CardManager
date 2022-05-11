package pl.md.cardmanager.util.crypto

data class EncryptorResult(val encryption : ByteArray, val encryptionIV:ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptorResult

        if (!encryption.contentEquals(other.encryption)) return false
        if (!encryptionIV.contentEquals(other.encryptionIV)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryption.contentHashCode()
        result = 31 * result + encryptionIV.contentHashCode()
        return result
    }
}
