package pl.md.cardmanager.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import pl.md.cardmanager.activities.AddEditCardActivity
import pl.md.cardmanager.activities.AuthActivity
import pl.md.cardmanager.data.model.CardInfo

@Composable
fun CardList(
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onAddNewClick: () -> Unit,
    cards: Flow<List<CardInfo>>,
    onDeleteItem: (Long) -> Unit
) {
    val cardsState = cards.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "")
                },
                actions = {
                    Button(onClick = { onExportClick() }) { Text(text = "Eksportuj") }
                    Button(onClick = { onImportClick() }) { Text(text = "Importuj") }
                    Button(onClick = { onAddNewClick() }) { Text(text = "Dodaj") }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(cardsState.value) { card ->
                CardItem(cardInfo = card,
                onDeleteItem = {onDeleteItem(card.id)})
            }
        }
    }
}

@Composable
fun CardItem(
    cardInfo: CardInfo,
    onDeleteItem: (Long) -> Unit?
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(3.dp),
        backgroundColor = Color.LightGray,
    ) {
        Column() {
            Row() {
                Column() {
                    Text(text = cardInfo.name)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = cardInfo.number)
                }
            }
            Divider()
            Row() {
                Button(onClick = { onDeleteItem(cardInfo.id) }) {
                    Text(text = "Usun")
                }
                Spacer(modifier = Modifier.width(3.dp))
                Button(onClick = {
                    startAuthActivity(context, cardInfo.id, false)
                }) {
                    Text(text = "Pokaz")
                }
                Spacer(modifier = Modifier.width(3.dp))
                Button(onClick = { startAuthActivity(context, cardInfo.id, true) }) {
                    Text(text = "Edytuj")
                }

            }
        }

    }
}

fun startAuthActivity(c: Context, cardId: Long, readOnly: Boolean) {
    val intent = Intent(c, AuthActivity::class.java)
    intent.putExtra(AddEditCardActivity.CARD_VIEW_ENABLED, readOnly)
    intent.putExtra(AddEditCardActivity.CREDIT_CARD_ID, cardId)
    c.startActivity(intent)
}