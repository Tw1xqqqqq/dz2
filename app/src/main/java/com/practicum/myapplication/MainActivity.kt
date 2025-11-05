package com.practicum.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.practicum.myapplication.ui.theme.MyApplicationTheme
import kotlin.random.Random

class Picture(
    val id: Int,
    val author: String,
    val url: String
)

fun generateSamplePictures(): List<Picture> {
    return listOf(
        Picture(1, "Ван Гог", "https://picsum.photos/400/300?random=1"),
        Picture(2, "Пикассо", "https://picsum.photos/400/300?random=2"),
        Picture(3, "Моне", "https://picsum.photos/400/300?random=3"),
        Picture(4, "Да Винчи", "https://picsum.photos/400/300?random=4"),
        Picture(5, "Рембрандт", "https://picsum.photos/400/300?random=5"),
        Picture(6, "Дали", "https://picsum.photos/400/300?random=6"),
        Picture(7, "Кандинский", "https://picsum.photos/400/300?random=7"),
        Picture(8, "Матисс", "https://picsum.photos/400/300?random=8")
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GalleryScreen()
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GalleryScreen() {
    var gallery by remember { mutableStateOf(generateSamplePictures()) }
    var searchText by remember { mutableStateOf("") }
    var isGridView by remember { mutableStateOf(false) }
    var nextId by remember { mutableStateOf(9) }
    var nextUrlSeed by remember { mutableStateOf(100) }
    
    val authors = listOf("Ван Гог", "Пикассо", "Моне", "Да Винчи", "Рембрандт", "Дали", "Кандинский", "Матисс")

    val filteredGallery = gallery.filter { picture ->
        picture.author.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val randomAuthor = authors[Random.nextInt(authors.size)]
                    val newUrl = "https://picsum.photos/400/300?random=${nextUrlSeed++}"
                    val newId = nextId++
                    
                    // Проверка на существование картинки с таким же url или id
                    val exists = gallery.any { 
                        it.url == newUrl || it.id == newId 
                    }
                    
                    if (!exists) {
                        val newPicture = Picture(
                            id = newId,
                            author = randomAuthor,
                            url = newUrl
                        )
                        gallery = gallery + newPicture
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add picture")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Поле поиска и кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Поиск по автору") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                // Кнопка переключения режима отображения
                TextButton(
                    onClick = { isGridView = !isGridView }
                ) {
                    Text(
                        text = if (isGridView) "Список" else "Сетка"
                    )
                }
                
                // Кнопка "Очистить всё"
                IconButton(
                    onClick = { gallery = emptyList() }
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Очистить всё")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Отображение галереи
            if (filteredGallery.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchText.isEmpty()) "Галерея пуста" else "Ничего не найдено",
                        fontSize = 18.sp
                    )
                }
            } else {
                if (isGridView) {
                    // Режим сетки
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredGallery,
                            key = { it.id }
                        ) { picture ->
                            PictureCard(
                                picture = picture,
                                onPictureClick = {
                                    gallery = gallery.filter { it.id != picture.id }
                                }
                            )
                        }
                    }
                } else {
                    // Режим списка
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredGallery) { picture ->
                            PictureCard(
                                picture = picture,
                                onPictureClick = {
                                    gallery = gallery.filter { it.id != picture.id }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PictureCard(
    picture: Picture,
    onPictureClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPictureClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            GlideImage(
                model = picture.url,
                contentDescription = picture.author,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = picture.author,
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
