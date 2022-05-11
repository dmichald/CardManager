import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.md.cardmanager.activities.RegisterActivity
import pl.md.cardmanager.ui.login.LoginEvent
import pl.md.cardmanager.ui.login.LoginViewModel
import pl.md.cardmanager.ui.model.UserRegisterDto
import pl.md.cardmanager.util.UiEvent

@Composable
fun LoginPage(
    isLogin: Boolean,
    viewModel: LoginViewModel = hiltViewModel()

) {
    val maxChars = 50
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            val username = remember { mutableStateOf(TextFieldValue()) }
            val password = remember { mutableStateOf(TextFieldValue()) }
            val pin = remember { mutableStateOf(TextFieldValue()) }
            if (isLogin) {
                Text(text = "Logowanie")
            } else {
                Text(text = "Rejestracja")
            }
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Nazwa użytkownika") },
                value = username.value,
                onValueChange = { if (it.text.length < maxChars) username.value = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Hasło") },
                value = password.value,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { if (it.text.length < maxChars) password.value = it })
            Spacer(modifier = Modifier.height(20.dp))

            Spacer(modifier = Modifier.height(20.dp))
            if (!isLogin) {
                TextField(
                    label = { Text(text = "Podaj pin") },
                    value = pin.value,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = { if (it.text.length < maxChars) pin.value = it })
                Spacer(modifier = Modifier.height(20.dp))
            }

            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        if (!isLogin) {
                            viewModel.onEvent(
                                LoginEvent.OnRegisterButtonClick(
                                    UserRegisterDto(
                                        username.value.text,
                                        password.value.text,
                                        pin.value.text
                                    )
                                )
                            )
                        } else {
                            viewModel.onEvent(
                                LoginEvent.OnLoginButtonClick(
                                    username.value.text,
                                    password.value.text
                                )
                            )
                        }

                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLogin) {
                        Text(text = "Zaloguj się")
                    } else {
                        Text(text = "Załóż konto")
                    }
                }
            }
            if (isLogin) {
                Spacer(modifier = Modifier.height(35.dp))
                ClickableText(text = AnnotatedString("Nie masz konta? Klinkij tu."), onClick = {
                    startRegisterActivity(context)
                }, style = TextStyle(fontSize = 15.sp))
            }
        }
    }

}

fun startRegisterActivity(c: Context) {
    val intent = Intent(c, RegisterActivity::class.java)
    c.startActivity(intent)
}