package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BookCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allBooks by viewModel.allBooks.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val userFavoriteIds by viewModel.userFavoriteIds.collectAsState()
    val userOrders by viewModel.userOrders.collectAsState()

    val unlockedBookIds = remember(userOrders) {
        userOrders.filter { it.status == "DELIVERED" || it.status == "APPROVED" }.map { it.bookId }.toSet()
    }

    // Filter and Sort Logic
    val filteredBooks = remember(allBooks, searchQuery, selectedCategory, selectedFormat, sortOption) {
        var list = allBooks.filter { book ->
            val matchesSearch = searchQuery.isBlank() ||
                    book.title.contains(searchQuery, ignoreCase = true) ||
                    book.author.contains(searchQuery, ignoreCase = true) ||
                    book.category.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null || book.category == selectedCategory
            val matchesFormat = selectedFormat == null || book.format.equals(selectedFormat, ignoreCase = true)

            matchesSearch && matchesCategory && matchesFormat
        }

        when (sortOption) {
            SortOption.POPULAR -> list.sortedByDescending { it.rating }
            SortOption.NEWEST -> list.sortedByDescending { it.dateAdded }
            SortOption.PRICE_ASC -> list.sortedBy { if (it.isPromotion && it.promoPriceFcfa != null) it.promoPriceFcfa else it.priceFcfa }
            SortOption.PRICE_DESC -> list.sortedByDescending { if (it.isPromotion && it.promoPriceFcfa != null) it.promoPriceFcfa else it.priceFcfa }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Catalogue d'eBooks",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Rechercher par titre, auteur, catégorie...", color = TextSecondaryLight) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryBlue) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Effacer", tint = TextSecondaryLight)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("catalog_search_bar"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputSurfaceLight,
                unfocusedContainerColor = InputSurfaceLight,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { viewModel.setCategoryFilter(null) },
                    label = { Text("Tous", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    shape = RoundedCornerShape(50.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = Color.White,
                        containerColor = ChipBackgroundLight,
                        labelColor = TextSecondaryLight
                    ),
                    border = null
                )
            }
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { viewModel.setCategoryFilter(cat) },
                    label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    shape = RoundedCornerShape(50.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = Color.White,
                        containerColor = ChipBackgroundLight,
                        labelColor = TextSecondaryLight
                    ),
                    border = null
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Format & Sorting Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Format toggle pills (PDF / EPUB)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = if (selectedFormat == "PDF") PrimaryBlue else ChipBackgroundLight,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.clickable {
                        viewModel.setFormatFilter(if (selectedFormat == "PDF") null else "PDF")
                    }
                ) {
                    Text(
                        "PDF",
                        color = if (selectedFormat == "PDF") Color.White else TextSecondaryLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Surface(
                    color = if (selectedFormat == "EPUB") PrimaryBlue else ChipBackgroundLight,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.clickable {
                        viewModel.setFormatFilter(if (selectedFormat == "EPUB") null else "EPUB")
                    }
                ) {
                    Text(
                        "EPUB",
                        color = if (selectedFormat == "EPUB") Color.White else TextSecondaryLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Sorting Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FilterList, contentDescription = null, tint = Gold500, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (sortOption) {
                        SortOption.POPULAR -> "Popularité"
                        SortOption.NEWEST -> "Nouveauté"
                        SortOption.PRICE_ASC -> "Prix croissant"
                        SortOption.PRICE_DESC -> "Prix décroissant"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = Gold400,
                    modifier = Modifier.clickable {
                        val next = when (sortOption) {
                            SortOption.POPULAR -> SortOption.NEWEST
                            SortOption.NEWEST -> SortOption.PRICE_ASC
                            SortOption.PRICE_ASC -> SortOption.PRICE_DESC
                            SortOption.PRICE_DESC -> SortOption.POPULAR
                        }
                        viewModel.setSortOption(next)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results List / Empty State
        if (filteredBooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aucun livre trouvé",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Essayez de modifier votre recherche ou vos filtres.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredBooks) { book ->
                    BookCard(
                        book = book,
                        isFavorite = userFavoriteIds.contains(book.id),
                        isUnlocked = unlockedBookIds.contains(book.id),
                        onBookClick = { viewModel.openBookDetail(book) },
                        onBuyClick = { viewModel.openPaymentModal(book) },
                        onReadClick = { viewModel.openReader(book) },
                        onFavoriteToggle = { viewModel.toggleFavorite(book.id) }
                    )
                }
            }
        }
    }
}
