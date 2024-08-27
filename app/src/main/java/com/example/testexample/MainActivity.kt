package com.example.testexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.testexample.ui.theme.TestExampleTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestExampleTheme {
                ChatCreationApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatCreationApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Клеточное наполнение",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Color(0xFF9C27B0))
            )
        },
        content = { padding ->
            CellCreationScreen(Modifier.padding(padding))
        },
        containerColor = Color(0xFF210336)
    )
}

@Composable
fun CellCreationScreen(modifier: Modifier = Modifier) {
    // Сохраняем список клеток
    val cells = rememberSaveable(
        saver = listSaver(
            save = { stateList -> stateList.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf<Triple<String, Int, String>>() }

    var consecutiveAliveCells by rememberSaveable { mutableStateOf(0) }
    var consecutiveDeadCells by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFF8A2BE2), Color(0xFF210336))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cells) { cell ->
                CellBanner(cell)
            }
        }

        Button(
            onClick = {
                val newCellIsAlive = (0..1).random() == 0
                if (newCellIsAlive) {
                    cells.add(Triple("Живая", R.drawable.alive, "и шевелится!"))
                    consecutiveAliveCells++
                    consecutiveDeadCells = 0
                } else {
                    cells.add(Triple("Мертвая", R.drawable.reaper, "или прикидывается"))
                    consecutiveAliveCells = 0
                    consecutiveDeadCells++
                }

                // Логика создания "Жизни"
                if (consecutiveAliveCells >= 3) {
                    cells.add(Triple("Жизнь", R.drawable.fertilization, "Ку-ку!"))
                    consecutiveAliveCells = 0
                }

                // Логика удаления жизни
                if (consecutiveDeadCells >= 3) {
                    val lifeIndex = cells.indexOfLast { it.first == "Жизнь" }
                    if (lifeIndex != -1) {
                        cells.removeAt(lifeIndex)
                    }
                    consecutiveDeadCells = 0
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF9C27B0))
        ) {
            Text(text = "СОТВОРИТЬ", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun CellBanner(cell: Triple<String, Int, String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = cell.second),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = cell.first, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = cell.third, fontSize = 14.sp, color = Color.Gray)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestExampleTheme {
        ChatCreationApp()
    }
}