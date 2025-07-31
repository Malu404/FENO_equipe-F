package com.example.fenoapp.ui.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.fenoapp.TokenManager
import com.example.fenoapp.model.Monitoria
import com.example.fenoapp.network.RetrofitClient
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth


@Composable
fun CalendarioScreen(token: String) {
    val currentMonth = remember { YearMonth.now() }
    var selectedDate by remember { mutableStateOf<CalendarDay?>(null) }
    var compromissos by remember { mutableStateOf<Map<LocalDate, List<String>>>(emptyMap()) }
    val coroutineScope = rememberCoroutineScope()

    // Quando selectedDate muda, busca monitorias para essa data
    LaunchedEffect(selectedDate) {
        selectedDate?.let { day ->
            val dataStr = day.date.toString() // "YYYY-MM-DD"
            coroutineScope.launch {
                val monitorias = buscarMonitoriasParaData(token, dataStr)
                compromissos = compromissos.toMutableMap().apply {
                    put(day.date, monitorias.map { monitoria ->
                        val horaFormatada = monitoria.data_hora?.substring(11,16) ?: "??:??"
                        "${monitoria.disciplina} às $horaFormatada"
                    })
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        HorizontalCalendar(
            dayContent = { day ->
                val isSelected = selectedDate?.date == day.date
                val temMonitoria = compromissos[day.date]?.isNotEmpty() == true

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) Color(0xFF90CAF9) else Color(0xFFE3F2FD))
                        .clickable { selectedDate = day },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else Color.Black
                        )

                        // Bolinha verde 💚 apenas se tiver monitoria
                        if (temMonitoria) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF4CAF50))
                            )
                        }
                    }
                }
            },
            monthBody = { month, content ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val daysOfWeek = daysOfWeek()
                        daysOfWeek.forEach { dayOfWeek ->
                            Text(
                                text = dayOfWeek.name.take(3),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    content()
                }
            }
        )
    }

    selectedDate?.let { diaSelecionado ->
        val compromissosDoDia = compromissos[diaSelecionado.date]
        AlertDialog(
            onDismissRequest = { selectedDate = null },
            title = {
                Text("Monitorias de ${diaSelecionado.date}")
            },
            text = {
                if (!compromissosDoDia.isNullOrEmpty()) {
                    Column {
                        compromissosDoDia.forEach {
                            Text("• $it", fontSize = 14.sp)
                        }
                    }
                } else {
                    Text("Nenhuma monitoria agendada", fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(onClick = { selectedDate = null }) {
                    Text("Fechar")
                }
            }
        )
    }
}

// Função suspensa para buscar monitorias da API para uma data
suspend fun buscarMonitoriasParaData(token: String, data: String): List<Monitoria> {
    return try {
        val response = RetrofitClient.apiService.obterMonitoriasPorData("Bearer $token", data)
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }
}