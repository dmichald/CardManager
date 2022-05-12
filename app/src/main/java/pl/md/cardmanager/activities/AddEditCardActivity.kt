package pl.md.cardmanager.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.md.cardmanager.data.repository.CreditCardRepository
import pl.md.cardmanager.ui.add_edit_activity.AddEditCard
import pl.md.cardmanager.ui.dto.CreditCardDto
import pl.md.cardmanager.ui.dto.converters.Converters
import pl.md.cardmanager.util.UserUtils
import javax.inject.Inject

@AndroidEntryPoint
class AddEditCardActivity : ComponentActivity() {
    @Inject
    lateinit var cardRepository: CreditCardRepository

    companion object {
        val NO_CREDIT_CARD by lazy { -1L }
        const val CREDIT_CARD_ID = "CREDIT_CARD_ID"
        const val CARD_VIEW_ENABLED = "CARD_VIEW_ENABLED"
        private var cardId = NO_CREDIT_CARD
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = getCardIdFromExtras()
        cardId = extras.first
        if (cardId != NO_CREDIT_CARD) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        lifecycleScope.launch {
            val card = getCard(extras.first)
            setContent {
                AddEditCard(
                    card = card,
                    enabled = extras.second
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (cardId != NO_CREDIT_CARD) {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        if (cardId != NO_CREDIT_CARD) {
            finish()
        }
    }

    private fun getCardIdFromExtras(): Pair<Long, Boolean> {
        val extras = intent.extras
        var value = NO_CREDIT_CARD
        var enabled = true
        if (extras != null) {
            value = extras.getLong(CREDIT_CARD_ID)
            enabled = extras.getBoolean(CARD_VIEW_ENABLED)
        }
        return Pair(value, enabled)
    }

    private suspend fun getCard(cardId: Long): CreditCardDto {
        return if (cardId == NO_CREDIT_CARD) {
            val card = CreditCardDto(userId = UserUtils.loggedUserId)
            card
        } else {
            val card = cardRepository.getCardById(cardId)
            Converters.toCreditCardDto(card)
        }
    }

}