package com.example.tela_login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tela_login.ui.theme.Tela_loginTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import android.app.Activity
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Tela_loginTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var showError_emptylogin by remember { mutableStateOf(false) }
    var showError_wrongemail by remember { mutableStateOf(false) }
    val emailValido = user.trim().endsWith("@alu.ufc.br", ignoreCase = true)
    Box(//caixa do smartphone
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Image(//plano de fundo
            painter = painterResource(id = R.drawable.wallpapermuitochatosemgracasemsalinsososemcriatividade),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Card(//layer acima do plano de fundo
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(20.dp),
            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.logo_pet_vertical_vermelho),
                    contentDescription = "Logo do pet",
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = "Login",
                    fontSize = 32.sp,
                    color = colorResource(id = R.color.red_pet),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                /*var user by remember { mutableStateOf("") }
                var pass by remember { mutableStateOf("") }*/

                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                )

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                Button(
                    onClick = {
                        showError_emptylogin = false
                        showError_wrongemail = false

                        // Verifica se os campos estão vazios
                        if (user.trim().isEmpty() || pass.trim().isEmpty()) {
                            showError_emptylogin = true
                        }
                        // Verifica se o email termina com @alu.ufc.br
                        else if (!user.trim().endsWith("@alu.ufc.br", ignoreCase = true)) {
                            showError_wrongemail = true
                        }
                        // Só entra se não tiver erro nenhum
                        else {
                            val intent = Intent(context, MenuSelecao::class.java)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.red_pet)
                    )
                ) {
                    Text("Login")
                }
                if (showError_emptylogin) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Por favor, preencha usuário e senha.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                if (showError_wrongemail) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email deve terminar com @alu.ufc.br e senha não pode estar vazia.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}