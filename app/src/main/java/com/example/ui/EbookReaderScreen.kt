package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookEntity
import com.example.ui.theme.*

enum class ReaderTheme {
    LIGHT, SEPIA, DARK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EbookReaderScreen(
    book: BookEntity,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fontSize by remember { mutableStateOf(16) }
    var readerTheme by remember { mutableStateOf(ReaderTheme.DARK) }
    var showSettings by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Determine colors based on selected reader theme
    val bgColor = when (readerTheme) {
        ReaderTheme.LIGHT -> Color(0xFFFAFAFA)
        ReaderTheme.SEPIA -> Color(0xFFFBF0D9)
        ReaderTheme.DARK -> Color(0xFF121824)
    }

    val textColor = when (readerTheme) {
        ReaderTheme.LIGHT -> Color(0xFF1A1A1A)
        ReaderTheme.SEPIA -> Color(0xFF4A3B2C)
        ReaderTheme.DARK -> Color(0xFFE2E8F0)
    }

    val topBarBg = when (readerTheme) {
        ReaderTheme.LIGHT -> Color(0xFFEEEEEE)
        ReaderTheme.SEPIA -> Color(0xFFF2E3C6)
        ReaderTheme.DARK -> Navy900
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor,
                            maxLines = 1
                        )
                        Text(
                            text = "${book.author} • Format ${book.format}",
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose, modifier = Modifier.testTag("btn_close_reader")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Fermer", tint = textColor)
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Rechercher", tint = textColor)
                    }
                    IconButton(onClick = { showSettings = !showSettings }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Paramètres", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarBg)
            )
        },
        bottomBar = {
            Surface(
                color = topBarBg,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val progress = if (scrollState.maxValue > 0) {
                        (scrollState.value.toFloat() / scrollState.maxValue.toFloat() * 100).toInt()
                    } else 0

                    Text(
                        text = "Progression : $progress%",
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor.copy(alpha = 0.8f)
                    )

                    LinearProgressIndicator(
                        progress = { if (scrollState.maxValue > 0) scrollState.value.toFloat() / scrollState.maxValue else 0f },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TechBlue,
                        trackColor = textColor.copy(alpha = 0.2f)
                    )

                    Text(
                        text = "${book.pages} pages",
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor.copy(alpha = 0.8f)
                    )
                }
            }
        },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar popup
            AnimatedVisibility(visible = showSearch) {
                Surface(
                    color = topBarBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Rechercher dans le texte...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        IconButton(onClick = { showSearch = false }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = textColor)
                        }
                    }
                }
            }

            // Reader Settings Panel popup
            AnimatedVisibility(visible = showSettings) {
                Surface(
                    color = topBarBg,
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Taille du texte", fontWeight = FontWeight.Bold, color = textColor, fontSize = 13.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(onClick = { if (fontSize > 12) fontSize -= 2 }) {
                                Text("A-", fontWeight = FontWeight.Bold, color = textColor)
                            }
                            Text("$fontSize sp", color = textColor, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { if (fontSize < 30) fontSize += 2 }) {
                                Text("A+", fontWeight = FontWeight.Bold, color = textColor)
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Theme Selector Chips
                            Text("Thème :", fontWeight = FontWeight.Bold, color = textColor, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFAFAFA))
                                        .clickable { readerTheme = ReaderTheme.LIGHT }
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFBF0D9))
                                        .clickable { readerTheme = ReaderTheme.SEPIA }
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF121824))
                                        .clickable { readerTheme = ReaderTheme.DARK }
                                )
                            }
                        }
                    }
                }
            }

            // Document Reader Canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Édition Numérique Africa Click AI • ${book.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TechBlue
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Render Book Full Text
                    val paragraphs = book.fullText.split("\n\n")
                    paragraphs.forEach { paragraph ->
                        Text(
                            text = paragraph,
                            fontSize = fontSize.sp,
                            fontFamily = FontFamily.Serif,
                            color = textColor,
                            lineHeight = (fontSize * 1.5).sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Surface(
                        color = topBarBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "--- Fin de l'extrait de lecture ---\nAcheté et certifié par africaclickai@gmail.com",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = textColor.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
