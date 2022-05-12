package pl.md.cardmanager.ui.add_edit_activity

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.md.cardmanager.ui.dto.CreditCardDto
import pl.md.cardmanager.util.UiEvent
import pl.md.cardmanager.util.UserUtils

@Composable
fun AddEditCard(
    viewModel: AddEditCardViewModel = hiltViewModel(),
    card: CreditCardDto,
    enabled: Boolean
) {
    val scaffoldState = rememberScaffoldState()
    val activity = (LocalContext.current as? Activity)
    val name = remember { mutableStateOf(card.name) }
    val number = remember { mutableStateOf(card.number) }
    val cvc = remember { mutableStateOf(card.CVC) }
    val owner = remember { mutableStateOf(card.ownerName) }
    val expirationMonth = remember { mutableStateOf(card.expirationMonth) }
    val expirationYear = remember { mutableStateOf(card.expirationYear) }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(activity, event.message, Toast.LENGTH_LONG).show()
                }
                is UiEvent.FinishActivity -> {
                    activity?.finish()
                }
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.padding(20.dp),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (enabled) {
                    viewModel.onEvent(
                        AddEditCardEvent.OnSaveButtonClick(
                            CreditCardDto(
                                id = card.id,
                                name = name.value,
                                number = number.value,
                                CVC = cvc.value,
                                ownerName = owner.value,
                                expirationMonth = expirationMonth.value,
                                expirationYear = expirationYear.value,
                                userId = UserUtils.loggedUserId
                            )
                        )
                    )
                } else {
                    viewModel.onEvent(
                        AddEditCardEvent.OnCloseButtonClick
                    )
                }

            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Ok"
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = viewModel.errors, fontSize = 15.sp, color = Color.Red)
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = name.value,
                onValueChange = { if (it.length < 30) name.value = it },
                label = {
                    Text(
                        text = "Nazwa karty:"
                    )
                },
                enabled = enabled
            )
            Spacer(modifier = Modifier.height(15.dp))
            TextField(
                value = number.value,
                onValueChange = {
                    if (isNumeric(it) && it.length <= 16) number.value = it
                },
                label = {
                    Text(
                        text = "Numer karty:"
                    )
                },
                enabled = enabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(15.dp))
            TextField(
                value = cvc.value,
                onValueChange = {
                    if (isNumeric(it) && it.length <= 4) {
                        cvc.value = it
                    }
                },
                label = {
                    Text(
                        text = "CVC:"
                    )
                },
                enabled = enabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(15.dp))
            TextField(
                value = owner.value,
                onValueChange = { if (it.length < 30) owner.value = it },
                label = {
                    Text(
                        text = "Wlasciciel:"
                    )
                },
                enabled = enabled
            )
            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                value = expirationMonth.value,
                onValueChange = {
                    if (isCorrectMonth(it)) expirationMonth.value = it
                },
                label = {
                    Text(
                        text = "Miesiac:"
                    )
                },
                enabled = enabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(15.dp))
            TextField(
                value = expirationYear.value,
                onValueChange = {
                    if (isCorrectYear(it)) expirationYear.value =
                        it
                },
                label = {
                    Text(
                        text = "Rok:"
                    )
                },
                enabled = enabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(15.dp))
        }

    }
}

fun isNumeric(toCheck: String): Boolean {
    return toCheck.all { c -> c.isDigit() }
}

fun isCorrectMonth(s: String): Boolean {
    return isNumeric(s) && s.length <= 2
}

fun isCorrectYear(s: String): Boolean {
    return isNumeric(s) && s.length <= 4
}



