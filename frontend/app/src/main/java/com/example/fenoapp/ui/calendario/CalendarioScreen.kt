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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.YearMonth

@Composable
fun CalendarioScreen() {
    val currentMonth = remember { YearMonth.now() }
    val compromissos = remember { //adicionar aqui o pull da database
        mapOf(
            currentMonth.atDay(15) to listOf("Gado Cálculo 14h", "Gado grafos 13h"),
            currentMonth.atDay(21) to listOf("Placeholder aaaaaa", "Hatsune miku esteve aqui")
        )
    }

    val selectedDate = remember { mutableStateOf<CalendarDay?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        HorizontalCalendar(
            dayContent = { day ->
                val isSelected = selectedDate.value?.date == day.date

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) Color(0xFF90CAF9) else Color(0xFFE3F2FD)
                        )
                        .clickable {
                            selectedDate.value = day
                            println("Dia clicado: ${day.date}")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            },
            monthBody = { month , content ->
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
    selectedDate.value?.let { diaSelecionado ->
        val compromissosDoDia = compromissos[diaSelecionado.date]
        AlertDialog(
            onDismissRequest = { selectedDate.value = null },
            title = {
                Text("Compromissos de ${diaSelecionado.date}")
            },
            text = {
                if (compromissosDoDia != null && compromissosDoDia.isNotEmpty()) {
                    Column {
                        compromissosDoDia.forEach {
                            Text("• $it", fontSize = 14.sp)
                        }
                    }
                } else {
                    Text("Nenhum compromisso", fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(onClick = { selectedDate.value = null }) {
                    Text("Fechar")
                }
            }
        )
    }
}