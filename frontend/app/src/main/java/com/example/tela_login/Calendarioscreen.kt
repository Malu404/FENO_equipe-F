package com.example.tela_login.ui.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.tela_login.R
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarioScreen() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var scrollToMonth by remember { mutableStateOf<YearMonth?>(null) }

    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(100),
        endMonth = currentMonth.plusMonths(100),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek().first()  // ✅ corrigido!
    )

    LaunchedEffect(scrollToMonth) {
        scrollToMonth?.let {
            calendarState.animateScrollToMonth(it)
            scrollToMonth = null
        }
    }

    val compromissos = remember {
        mapOf(
            YearMonth.of(2025, 7).atDay(15) to listOf("Gado Cálculo 14h", "Gado grafos 13h"),
            YearMonth.of(2025, 7).atDay(21) to listOf("Placeholder aaaaaa", "Hatsune miku esteve aqui"),
            YearMonth.of(2025, 6).atDay(20) to listOf("Gado Computação Gráfica 14h")
        )
    }

    val selectedDate = remember { mutableStateOf<CalendarDay?>(null) }
    val visibleMonth = calendarState.firstVisibleMonth ?: return
    val nomeMes = visibleMonth.yearMonth.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
    val ano = visibleMonth.yearMonth.year

    Column(modifier = Modifier.fillMaxSize()) {
        // Navegação
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                    scrollToMonth = currentMonth
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.redder_pet),
                    contentColor = Color.White
                )
            ) {
                Text("Anterior")
            }

            Text(
                text = "$nomeMes $ano",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Button(onClick = {
                currentMonth = currentMonth.plusMonths(1)
                scrollToMonth = currentMonth
            },
                colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.redder_pet),
                contentColor = Color.White
            )
            ) {
                Text("Próximo")
            }
        }

        // Título do mês
        Text(
            text = nomeMes,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Calendário horizontal
        HorizontalCalendar(
            state = calendarState,
            dayContent = { day ->
                val isSameMonth = day.date.month == visibleMonth.yearMonth.month &&
                        day.date.year == visibleMonth.yearMonth.year
                val isSelected = selectedDate.value?.date == day.date
                val hasCompromisso = compromissos.containsKey(day.date)

                Column(
                    modifier = Modifier
                        .size(42.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                isSelected -> Color(0xFF90CAF9)
                                isSameMonth -> Color(0xFFE3F2FD)
                                else -> Color.Transparent
                            }
                        )
                        .clickable(enabled = isSameMonth) {
                            selectedDate.value = day
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Bolinha verde de compromisso
                    if (hasCompromisso && isSameMonth) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                    }

                    if (isSameMonth) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            },
            monthBody = { _, content ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        daysOfWeek().forEach { dayOfWeek ->  // ✅ corrigido!
                            Text(
                                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR")),
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

    // Diálogo de compromissos
    selectedDate.value?.let { diaSelecionado ->
        val compromissosDoDia = compromissos[diaSelecionado.date]
        AlertDialog(
            onDismissRequest = { selectedDate.value = null },
            title = {
                Text("Compromissos de ${diaSelecionado.date}")
            },
            text = {
                if (!compromissosDoDia.isNullOrEmpty()) {
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
