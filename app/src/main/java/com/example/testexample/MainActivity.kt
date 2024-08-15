package com.example.testexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testexample.ui.theme.TestExampleTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestExampleTheme {
                CellCreationApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellCreationApp() {
    var cells by remember { mutableStateOf(listOf<Cell>()) }
    var consecutiveAliveCount by remember { mutableStateOf(0) }
    var consecutiveDeadCount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Клеточное наполнение",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                        },
                colors = TopAppBarDefaults.topAppBarColors(Color(0xFF9C27B0))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF9C27B0), // Пурпурный цвет
                                Color(0xFF4A0072)  // Тёмно-пурпурный цвет
                            )
                        )
                    )
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Список клеток
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(cells) { cell ->
                        CellBanner(cell)
                    }
                }

                // Кнопка "Сотворить"
                Button(
                    onClick = {
                        addCell(
                            cells = cells,
                            consecutiveAliveCount = consecutiveAliveCount,
                            consecutiveDeadCount = consecutiveDeadCount,
                            onUpdateCells = { newCells -> cells = newCells },
                            onAliveCountChange = { newCount -> consecutiveAliveCount = newCount },
                            onDeadCountChange = { newCount -> consecutiveDeadCount = newCount }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)), // Устанавливаем цвет кнопки
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Сотворить",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    )
}

// Функция для добавления клетки
fun addCell(
    cells: List<Cell>,
    consecutiveAliveCount: Int,
    consecutiveDeadCount: Int,
    onUpdateCells: (List<Cell>) -> Unit,
    onAliveCountChange: (Int) -> Unit,
    onDeadCountChange: (Int) -> Unit
) {
    val newCell = if (Random.nextBoolean()) Cell.Alive else Cell.Dead

    val updatedCells = cells.toMutableList().apply { add(newCell) }

    // Логика для отслеживания подряд идущих живых и мёртвых клеток
    var newAliveCount = consecutiveAliveCount
    var newDeadCount = consecutiveDeadCount

    if (newCell is Cell.Alive) {
        newAliveCount++
        newDeadCount = 0
    } else {
        newDeadCount++
        newAliveCount = 0
    }

    // Проверка на три подряд живые клетки для создания жизни
    if (newAliveCount == 3) {
        updatedCells.add(Cell.Life)
        newAliveCount = 0
    }

    // Проверка на три подряд мёртвые клетки для удаления жизни
    if (newDeadCount == 3) {
        val lifeIndex = updatedCells.indexOfLast { it is Cell.Life }
        if (lifeIndex != -1) {
            updatedCells.removeAt(lifeIndex)
        }
        newDeadCount = 0
    }

    // Обновляем состояние
    onUpdateCells(updatedCells)
    onAliveCountChange(newAliveCount)
    onDeadCountChange(newDeadCount)
}

// Баннер для клетки
@Composable
fun CellBanner(cell: Cell) {
    val text: String
    val subText: String
    val icon: ImageVector
    val color: Color

    when (cell) {
        is Cell.Alive -> {
            text = "Живая"
            subText = "и шевелится!"
            icon = Icons.Filled.Favorite
            color = Color.Green
        }
        is Cell.Dead -> {
            text = "Мертвая"
            subText = "или прикидывается"
            icon = Icons.Filled.Clear
            color = Color.Red
        }
        is Cell.Life -> {
            text = "Жизнь"
            subText = "Ку-ку!"
            icon = Icons.Filled.Star
            color = Color.Yellow
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = rememberVectorPainter(image = icon),
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = text, color = Color.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subText, color = Color.Black, fontSize = 12.sp)
            }
        }
    }
}

// Определение клеток
sealed class Cell {
    object Alive : Cell()
    object Dead : Cell()
    object Life : Cell()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestExampleTheme {
        CellCreationApp()
    }
}