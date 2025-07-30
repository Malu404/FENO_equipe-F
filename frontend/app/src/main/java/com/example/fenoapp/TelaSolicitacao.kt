package com.example.fenoapp

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fenoapp.model.Disciplina
import com.example.fenoapp.model.MonitoriaRequest
import com.example.fenoapp.network.RetrofitClient
import kotlinx.coroutines.launch


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TelaSolicitacao() {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val tokenManager = remember { TokenManager(context) }
        var listaDisciplinas by remember { mutableStateOf<List<Disciplina>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }
        val token = remember { mutableStateOf<String?>(null) }
        //var nome by remember { mutableStateOf("")
        var data by remember { mutableStateOf(TextFieldValue("")) }
        var horario by remember { mutableStateOf(TextFieldValue("")) }
        var duvidas by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                try {
                    val token = TokenManager(context).getToken()
                    val response = RetrofitClient.apiService.getDisciplinas("Bearer $token")
                    if (response.isSuccessful) {
                        listaDisciplinas = response.body()?.data ?: emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("API", "Erro ao buscar disciplinas", e)
                } finally {
                    loading = false
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Solicitação de Monitoria") }
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(24.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //OutlinedTextField(
                    //    value = nome,
                    //    onValueChange = { nome = it },
                    //    label = { Text("Seu nome") },
                    //    modifier = Modifier.fillMaxWidth(),
                    //    colors = OutlinedTextFieldDefaults.colors(
                    //        focusedBorderColor = colorResource(id = R.color.red_pet),
                    //        unfocusedBorderColor = colorResource(id = R.color.red_pet).copy(alpha = 0.5f)
                    //    )

                    //)

                    var expanded by remember { mutableStateOf(false) }
                    var disciplinaSelecionada by remember { mutableStateOf<Disciplina?>(null) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = disciplinaSelecionada?.toString() ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selecione a disciplina") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listaDisciplinas.forEach { disciplina ->
                                DropdownMenuItem(
                                    text = { Text(disciplina.toString()) },
                                    onClick = {
                                        disciplinaSelecionada = disciplina
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = data,
                        onValueChange = { novoValor ->
                            val apenasNumeros = novoValor.text.filter { it.isDigit() }

                            val formatado = when {
                                apenasNumeros.length <= 2 -> apenasNumeros
                                apenasNumeros.length <= 4 -> "${apenasNumeros.take(2)}/${apenasNumeros.drop(2)}"
                                apenasNumeros.length <= 8 -> "${apenasNumeros.take(2)}/${apenasNumeros.drop(2).take(2)}/${apenasNumeros.drop(4)}"
                                else -> "${apenasNumeros.take(2)}/${apenasNumeros.drop(2).take(2)}/${apenasNumeros.drop(4).take(4)}"
                            }

                            // Atualiza o valor mantendo o cursor no fim
                            data = TextFieldValue(
                                text = formatado,
                                selection = TextRange(formatado.length)
                            )
                        },
                        label = { Text("Data (DD/MM/AAAA)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.red_pet),
                            unfocusedBorderColor = colorResource(id = R.color.red_pet).copy(alpha = 0.5f)
                        )

                    )

                    OutlinedTextField(
                        value = horario,
                        onValueChange = { novoValor ->
                            // filtra só números
                            val apenasNumeros = novoValor.text.filter { it.isDigit() }

                            val formatado = when {
                                apenasNumeros.length <= 2 -> apenasNumeros
                                apenasNumeros.length <= 4 -> "${apenasNumeros.take(2)}:${apenasNumeros.drop(2)}"
                                else -> "${apenasNumeros.take(2)}:${apenasNumeros.drop(2).take(2)}"
                            }

                            // Atualiza mantendo o cursor no fim
                            horario = TextFieldValue(
                                text = formatado,
                                selection = TextRange(formatado.length)
                            )
                        },
                        label = { Text("Horário (HH:MM)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.red_pet),
                            unfocusedBorderColor = colorResource(id = R.color.red_pet).copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        value = duvidas,
                        onValueChange = { duvidas = it },
                        label = { Text("Dúvidas") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.red_pet),
                            unfocusedBorderColor = colorResource(id = R.color.red_pet).copy(alpha = 0.5f)
                        )

                    )

                    Button(
                        onClick = {
                            val disciplinaId = disciplinaSelecionada?.id
                            if (disciplinaId == null) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Selecione uma disciplina.")
                                }
                                return@Button
                            }

                            coroutineScope.launch {
                                try {
                                    val conversor = DateTimeConverter()
                                    val tokenHeader = "Bearer ${TokenManager(context).getToken()}"
                                    val dataHoraInput = "${data.text} ${horario.text}"
                                    val dataHoraFormatada = conversor.paraISO(dataHoraInput)
                                    Log.e("HoraData", "$dataHoraFormatada")
                                    RetrofitClient.apiService.criarMonitoria(
                                        tokenHeader,
                                        MonitoriaRequest(
                                            data_hora = dataHoraFormatada,
                                            descricao = duvidas,
                                            disciplina = disciplinaId
                                        )
                                    )
                                    snackbarHostState.showSnackbar("Solicitação enviada com sucesso!")
                                } catch (e: Exception) {
                                    Log.e("API", "Erro ao enviar solicitação: ${e.message}")
                                    snackbarHostState.showSnackbar("Erro ao enviar solicitação.")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.red_pet)
                        )
                    ) {
                        Text("Enviar Solicitação", fontSize = 18.sp)
                    }
                }
            }
        )
    }
