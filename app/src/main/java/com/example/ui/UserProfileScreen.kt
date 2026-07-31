package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.models.OrderEntity
import com.example.ui.components.BookCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

/**
 * UserProfileScreen displays the authenticated user's profile, purchased books,
 * order history retrieved from Firestore and Room DB, in a clean dashboard layout.
 */
@Composable
fun UserProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val localOrders by viewModel.userOrders.collectAsState()
    val allBooks by viewModel.allBooks.collectAsState()
    val userFavoriteIds by viewModel.userFavoriteIds.collectAsState()
    val language by viewModel.language.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var firestoreOrders by remember { mutableStateOf<List<OrderEntity>>(emptyList()) }
    var isSyncingFirestore by remember { mutableStateOf(false) }
    var selectedSection by remember { mutableStateOf(0) } // 0: Library, 1: Orders, 2: Favorites, 3: Settings

    // Fetch order history from Firestore on launch or when user email changes
    LaunchedEffect(currentUser?.email) {
        val userEmail = currentUser?.email
        if (!userEmail.isNullOrBlank()) {
            isSyncingFirestore = true
            viewModel.repository.fetchUserOrdersFromFirestore(userEmail) { orders ->
                if (orders.isNotEmpty()) {
                    firestoreOrders = orders
                }
                isSyncingFirestore = false
            }
        }
    }

    // Combine local & Firestore orders cleanly (deduplicating by ID)
    val combinedOrders = remember(localOrders, firestoreOrders) {
        val orderMap = mutableMapOf<String, OrderEntity>()
        localOrders.forEach { orderMap[it.id] = it }
        firestoreOrders.forEach { orderMap[it.id] = it }
        orderMap.values.sortedByDescending { it.orderDate }
    }

    val unlockedBookIds = remember(combinedOrders) {
        combinedOrders
            .filter { it.status == "DELIVERED" || it.status == "APPROVED" }
            .map { it.bookId }
            .toSet()
    }

    val unlockedBooks = remember(allBooks, unlockedBookIds) {
        allBooks.filter { unlockedBookIds.contains(it.id) }
    }

    val favoriteBooks = remember(allBooks, userFavoriteIds) {
        allBooks.filter { userFavoriteIds.contains(it.id) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("user_profile_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Header Card with Gradient Accent
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PrimaryHeaderGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.displayName?.take(1)?.uppercase() ?: "U",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.displayName ?: "Utilisateur",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                        Text(
                            text = currentUser?.email ?: "Non connecté",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (currentUser?.isAdmin == true) "Administrateur" else "Compte Membre",
                                    color = if (currentUser?.isAdmin == true) Gold400 else SuccessGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = PrimaryBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isSyncingFirestore) "Sync..." else "Firestore",
                                    color = TechBlueLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Déconnexion",
                            tint = ErrorRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dashboard Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(
                        value = unlockedBooks.size.toString(),
                        label = "eBooks Acquis",
                        icon = Icons.Outlined.MenuBook,
                        tint = TechBlueLight
                    )
                    MetricItem(
                        value = combinedOrders.size.toString(),
                        label = "Commandes",
                        icon = Icons.Outlined.ReceiptLong,
                        tint = Gold400
                    )
                    MetricItem(
                        value = favoriteBooks.size.toString(),
                        label = "Favoris",
                        icon = Icons.Outlined.FavoriteBorder,
                        tint = ErrorRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Tabs (Bibliothèque, Commandes, Favoris, Paramètres)
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            containerColor = Color.Transparent,
            contentColor = PrimaryBlue,
            edgePadding = 0.dp
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("Mes eBooks (${unlockedBooks.size})", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_library")
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Commandes (${combinedOrders.size})", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_orders")
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("Favoris (${favoriteBooks.size})", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_favorites")
            )
            Tab(
                selected = selectedSection == 3,
                onClick = { selectedSection = 3 },
                text = { Text("Paramètres", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_settings")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content Views
        when (selectedSection) {
            // 0: PURCHASED EBOOKS / LIBRARY
            0 -> {
                if (unlockedBooks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aucun eBook débloqué pour le moment",
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Commandez un livre via WhatsApp Mobile Money pour l'ajouter à votre bibliothèque.",
                                color = TextSecondaryDark,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(unlockedBooks) { book ->
                            BookCard(
                                book = book,
                                isFavorite = userFavoriteIds.contains(book.id),
                                isUnlocked = true,
                                onBookClick = { viewModel.openBookDetail(book) },
                                onBuyClick = {},
                                onReadClick = { viewModel.openReader(book) },
                                onFavoriteToggle = { viewModel.toggleFavorite(book.id) }
                            )
                        }
                    }
                }
            }

            // 1: FIRESTORE ORDER HISTORY
            1 -> {
                if (combinedOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aucune commande trouvée dans Firestore ou locale.",
                            color = TextSecondaryDark
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(combinedOrders) { order ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Commande N° ${order.id.take(8).uppercase()}",
                                            fontWeight = FontWeight.Bold,
                                            color = Gold400,
                                            fontSize = 13.sp
                                        )
                                        Surface(
                                            color = when (order.status) {
                                                "DELIVERED", "APPROVED" -> SuccessGreen.copy(alpha = 0.2f)
                                                else -> WarningAmber.copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = when (order.status) {
                                                    "DELIVERED", "APPROVED" -> "Payé & Livré"
                                                    else -> "En Attente de Validation"
                                                },
                                                color = when (order.status) {
                                                    "DELIVERED", "APPROVED" -> SuccessGreen
                                                    else -> WarningAmber
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = order.bookTitle,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Prix : ${order.bookPriceFcfa} FCFA • Date : ${order.orderDate}",
                                        color = TextSecondaryDark,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Destinataire : ${order.deliveryEmail}",
                                        color = TechBlueLight,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2: FAVORITES
            2 -> {
                if (favoriteBooks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aucun livre enregistré dans vos favoris.",
                            color = TextSecondaryDark
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(favoriteBooks) { book ->
                            BookCard(
                                book = book,
                                isFavorite = true,
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

            // 3: ACCOUNT & DASHBOARD SETTINGS
            3 -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Translate,
                                        contentDescription = null,
                                        tint = TechBlueLight
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Langue (Language)", color = PureWhite)
                                }
                                TextButton(onClick = { viewModel.toggleLanguage() }) {
                                    Text(
                                        text = if (language == "FR") "Français (FR)" else "English (EN)",
                                        color = Gold400,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            HorizontalDivider(color = Navy900, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.DarkMode,
                                        contentDescription = null,
                                        tint = Gold400
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Mode Sombre", color = PureWhite)
                                }
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = { viewModel.toggleDarkMode() }
                                )
                            }
                        }
                    }

                    // Contact & Support Info Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Support & Client Service",
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "WhatsApp Client : +241 77 24 45 15",
                                color = SuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "E-mail Pro : contact@africaclick.com",
                                color = TechBlueLight,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PureWhite
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondaryDark
        )
    }
}
