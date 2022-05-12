package pl.md.cardmanager.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import pl.md.cardmanager.MainActivity
import pl.md.cardmanager.activities.AddEditCardActivity
import pl.md.cardmanager.activities.AuthActivity
import pl.md.cardmanager.activities.CardListActivity
import pl.md.cardmanager.data.model.CardInfo
import pl.md.cardmanager.util.UserUtils

@Composable
fun CardList(
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onAddNewClick: () -> Unit,
    cards: Flow<List<CardInfo>>,
    onDeleteItem: (Long) -> Unit
) {
    val cardsState = cards.collectAsState(initial = emptyList())
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "")
                },
                actions = {
                    Button(onClick = { logOut(context) }) { Text(text = "Wyloguj") }
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
                    onDeleteItem = { onDeleteItem(card.id) })
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
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .fillMaxWidth(),
        elevation = 15.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(0.8.dp, Color.Gray)
    ) {

        Column(
            modifier = Modifier.padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row() {
                Column() {
                    Text(text = cardInfo.name, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = cardInfo.number, fontSize = 18.sp)
                }
            }
            Divider()
            Row(
            ) {
                Button(onClick = { onDeleteItem(cardInfo.id) }) {
                    Text(text = "Usuń")
                }
                Spacer(modifier = Modifier.width(3.dp))
                Button(onClick = {
                    startAuthActivity(context, cardInfo.id, false)
                }) {
                    Text(text = "Pokaż")
                }
                Spacer(modifier = Modifier.width(3.dp))
                Button(onClick = { startAuthActivity(context, cardInfo.id, true) }) {
                    Text(text = "Edytuj")
                }

            }
        }

    }
}

fun logOut(c: Context) {
    UserUtils.clearUser(c as CardListActivity)
    val intent = Intent(c, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    c.startActivity(intent)
}

fun startAuthActivity(c: Context, cardId: Long, readOnly: Boolean) {
    val intent = Intent(c, AuthActivity::class.java)
    intent.putExtra(AddEditCardActivity.CARD_VIEW_ENABLED, readOnly)
    intent.putExtra(AddEditCardActivity.CREDIT_CARD_ID, cardId)
    c.startActivity(intent)
}