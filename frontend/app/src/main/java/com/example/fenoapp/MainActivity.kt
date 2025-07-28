package com.example.fenoapp

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
import com.example.fenoapp.ui.theme.Tela_loginTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fenoapp.model.LoginRequest
import com.example.fenoapp.model.RegisterRequest
import com.example.fenoapp.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Tela_loginTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(onNavigateToRegister = {
                            navController.navigate("register")
                        })
                    }
                    composable("register") {
                        RegisterScreen(onNavigateBack = {
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}


@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
                        // Se tudo estiver ok, tenta login via API
                        else {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.apiService.login(LoginRequest(user.trim(), pass.trim()))

                                    if (response.isSuccessful && response.body() != null) {
                                        val token = response.body()!!.token
                                        val tokenManager = TokenManager(context)
                                        tokenManager.saveToken(token)

                                        // Vai para a próxima tela
                                        val intent = Intent(context, MenuSelecao::class.java)
                                        context.startActivity(intent)
                                    } else {
                                        // erro de credenciais
                                        showError_wrongemail = true
                                    }
                                } catch (e: Exception) {
                                    // erro de rede, timeout, etc.
                                    showError_wrongemail = true
                                }
                            }
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
                TextButton(
                    onClick = { onNavigateToRegister() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Não tem conta? Cadastre-se", color = colorResource(id = R.color.red_pet))
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var cpf by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var anoIngresso by remember { mutableStateOf("") }

    var showError_emptyFields by remember { mutableStateOf(false) }
    var showError_invalidEmail by remember { mutableStateOf(false) }
    var showError_invalidCpf by remember { mutableStateOf(false) }
    var showError_invalidAno by remember { mutableStateOf(false) }

    val emailValido = email.trim().endsWith("@alu.ufc.br", ignoreCase = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.wallpapermuitochatosemgracasemsalinsososemcriatividade),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Card(
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
                    text = "Cadastro",
                    fontSize = 32.sp,
                    color = colorResource(id = R.color.red_pet),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = cpf,
                    onValueChange = { cpf = it },
                    label = { Text("CPF (11 dígitos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome completo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                OutlinedTextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    label = { Text("Matrícula") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text("Senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                OutlinedTextField(
                    value = anoIngresso,
                    onValueChange = { anoIngresso = it },
                    label = { Text("Ano de Ingresso") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                Button(
                    onClick = {
                        // Limpa erros
                        showError_emptyFields = false
                        showError_invalidEmail = false
                        showError_invalidCpf = false
                        showError_invalidAno = false

                        // Valida campos vazios
                        if (
                            cpf.trim().isEmpty() || nome.trim().isEmpty() || matricula.trim().isEmpty() ||
                            email.trim().isEmpty() || senha.trim().isEmpty() || anoIngresso.trim().isEmpty()
                        ) {
                            showError_emptyFields = true
                            return@Button
                        }

                        // Valida CPF: deve ter 11 dígitos numéricos
                        if (cpf.length != 11 || cpf.any { !it.isDigit() }) {
                            showError_invalidCpf = true
                            return@Button
                        }

                        // Valida email
                        if (!emailValido) {
                            showError_invalidEmail = true
                            return@Button
                        }

                        // Valida ano de ingresso
                        val ano = anoIngresso.toIntOrNull()
                        if (ano == null || ano < 1900 || ano > 2100) {
                            showError_invalidAno = true
                            return@Button
                        }

                        // Se passou em todas as validações, faz a chamada à API (com coroutines)

                        coroutineScope.launch {
                            try {
                                val registerRequest = RegisterRequest(
                                    cpf = cpf.trim(),
                                    nome = nome.trim(),
                                    matricula = matricula.trim(),
                                    email = email.trim(),
                                    senha = senha.trim(),
                                    ano_ingresso = ano
                                )
                                val response = RetrofitClient.apiService.register(registerRequest)

                                if (response.isSuccessful) {
                                    val token = response.body()?.token
                                    if (token != null) {
                                        // Salva o token
                                        TokenManager(context).saveToken(token)
                                        // Navega para próxima tela
                                        val intent = Intent(context, MenuSelecao::class.java)
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "Falha ao receber token.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Erro no cadastro: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro na conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
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
                    Text("Cadastrar")
                }

                if (showError_emptyFields) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Por favor, preencha todos os campos.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                if (showError_invalidCpf) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CPF deve conter exatamente 11 dígitos numéricos.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                if (showError_invalidEmail) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email deve terminar com @alu.ufc.br.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                if (showError_invalidAno) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ano de ingresso inválido.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Button(
                    onClick = { onNavigateBack() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Voltar para Login")
                }
            }
        }
    }
}