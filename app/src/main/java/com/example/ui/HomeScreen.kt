package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookEntity
import com.example.ui.components.AfricaClickLogo
import com.example.ui.components.BookCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.NavigationTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val popularBooks by viewModel.popularBooks.collectAsState()
    val newBooks by viewModel.newBooks.collectAsState()
    val promotionBooks by viewModel.promotionBooks.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val userFavoriteIds by viewModel.userFavoriteIds.collectAsState()
    val userOrders by viewModel.userOrders.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()

    val unlockedBookIds = remember(userOrders) {
        userOrders.filter { it.status == "DELIVERED" || it.status == "APPROVED" }.map { it.bookId }.toSet()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // --- HERO BANNER HEADER WITH LOGO ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Navy900, Navy800)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & App Name
                    AfricaClickLogo(
                        iconSize = 40.dp,
                        showTagline = true
                    )

                    // Top Action Badges (Notifications & Theme Toggle)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleNotifications() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }

                        IconButton(
                            onClick = { viewModel.toggleDarkMode() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Brightness4,
                                contentDescription = "Mode Sombre",
                                tint = Gold400
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Search Bar Launcher
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.setSearchQuery(it)
                        if (it.isNotBlank()) viewModel.selectTab(NavigationTab.CATALOG)
                    },
                    placeholder = { Text("Rechercher un livre, auteur...", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.8f)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Effacer", tint = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        focusedBorderColor = Color.White.copy(alpha = 0.4f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Promo Card Banner
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(PrimaryBlue, Navy900, Gold500)
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Surface(
                                color = Gold400,
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(
                                    text = "OFFRE SPÉCIALE EBOOKS",
                                    color = Color(0xFF1C1B1F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Accélérez vos connaissances",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Des dizaines d'eBooks au format PDF & EPUB livrés instantanément.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CATEGORIES CAROUSEL ---
        Column {
            Text(
                text = "Catégories",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val selectedCategory by viewModel.selectedCategory.collectAsState()

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = (cat == selectedCategory)
                    Surface(
                        color = if (isSelected) PrimaryBlue else ChipBackgroundLight,
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.clickable {
                            viewModel.setCategoryFilter(cat)
                            viewModel.selectTab(NavigationTab.CATALOG)
                        }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else TextSecondaryLight,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- LIVRES POPULAIRES (POPULAR EBOOKS) ---
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Livres Populaires 🔥",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                TextButton(onClick = { viewModel.selectTab(NavigationTab.CATALOG) }) {
                    Text("Voir tout", color = TechBlue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(popularBooks) { book ->
                    BookCard(
                        book = book,
                        isFavorite = userFavoriteIds.contains(book.id),
                        isUnlocked = unlockedBookIds.contains(book.id),
                        onBookClick = { viewModel.openBookDetail(book) },
                        onBuyClick = { viewModel.openPaymentModal(book) },
                        onReadClick = { viewModel.openReader(book) },
                        onFavoriteToggle = { viewModel.toggleFavorite(book.id) },
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- PROMOTIONS SECTION ---
        if (promotionBooks.isNotEmpty()) {
            Column {
                Text(
                    text = "Promotions En Cours 🏷️",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Gold500,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(promotionBooks) { book ->
                        BookCard(
                            book = book,
                            isFavorite = userFavoriteIds.contains(book.id),
                            isUnlocked = unlockedBookIds.contains(book.id),
                            onBookClick = { viewModel.openBookDetail(book) },
                            onBuyClick = { viewModel.openPaymentModal(book) },
                            onReadClick = { viewModel.openReader(book) },
                            onFavoriteToggle = { viewModel.toggleFavorite(book.id) },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- NOUVEAUTÉS (NEW ARRIVALS) ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Nouveautés ⚡",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            newBooks.forEach { book ->
                BookCard(
                    book = book,
                    isFavorite = userFavoriteIds.contains(book.id),
                    isUnlocked = unlockedBookIds.contains(book.id),
                    onBookClick = { viewModel.openBookDetail(book) },
                    onBuyClick = { viewModel.openPaymentModal(book) },
                    onReadClick = { viewModel.openReader(book) },
                    onFavoriteToggle = { viewModel.toggleFavorite(book.id) },
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }
        }
    }
}
