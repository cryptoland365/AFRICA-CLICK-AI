package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookEntity
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allBooks by viewModel.allBooks.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val adminPassword by viewModel.adminPassword.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) } // 0: Stats & Orders, 1: Books Management, 2: Users, 3: Password
    var showAddBookModal by remember { mutableStateOf(false) }
    var bookToEdit by remember { mutableStateOf<BookEntity?>(null) }
    var newPasswordInput by remember { mutableStateOf("") }

    val pendingOrders = remember(allOrders) { allOrders.filter { it.status == "PENDING" } }
    val deliveredOrders = remember(allOrders) { allOrders.filter { it.status == "DELIVERED" || it.status == "APPROVED" } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Admin Header Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Gold500.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Gold500,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Console d'Administration", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text("Supervisé par : africaclickai@gmail.com", style = MaterialTheme.typography.bodySmall, color = TechBlueLight)
                }

                IconButton(onClick = { viewModel.logout() }) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Quitter", tint = ErrorRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = Color.Transparent,
            contentColor = Gold500,
            edgePadding = 0.dp
        ) {
            Tab(selected = selectedAdminTab == 0, onClick = { selectedAdminTab = 0 }, text = { Text("Commandes (${pendingOrders.size})") })
            Tab(selected = selectedAdminTab == 1, onClick = { selectedAdminTab = 1 }, text = { Text("Livres (${allBooks.size})") })
            Tab(selected = selectedAdminTab == 2, onClick = { selectedAdminTab = 2 }, text = { Text("Utilisateurs (${allUsers.size})") })
            Tab(selected = selectedAdminTab == 3, onClick = { selectedAdminTab = 3 }, text = { Text("Sécurité") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedAdminTab) {
            // 0: STATS & ORDERS
            0 -> {
                Column {
                    // Stats Row Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = CardDark,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Chiffre d'affaires", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("${totalRevenue ?: 0} FCFA", fontWeight = FontWeight.Bold, color = Gold500, fontSize = 16.sp)
                            }
                        }

                        Surface(
                            color = CardDark,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Livres Vendus", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("${deliveredOrders.size}", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Validation des Paiements WhatsApp", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    if (pendingOrders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                            Text("Aucun paiement en attente.", color = TextSecondaryDark)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(pendingOrders) { order ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardDark),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(order.id, fontWeight = FontWeight.Bold, color = Gold400)
                                            Text(order.orderDate, color = TextSecondaryDark, fontSize = 11.sp)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Client : ${order.userEmail}", color = Color.White, fontSize = 13.sp)
                                        Text("Livre : ${order.bookTitle}", color = TechBlueLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Montant : ${order.bookPriceFcfa} FCFA", color = Gold500, fontWeight = FontWeight.Bold)

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Action Button: Valider le paiement & Déclencher l'envoi
                                        Button(
                                            onClick = {
                                                viewModel.validatePayment(order.id)
                                                Toast.makeText(context, "Paiement validé ! Livre envoyé automatiquement à ${order.userEmail}", Toast.LENGTH_LONG).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("btn_validate_payment_${order.id}")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Valider Paiement & Envoyer Livre", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 1: BOOKS MANAGEMENT
            1 -> {
                Column {
                    Button(
                        onClick = {
                            bookToEdit = null
                            showAddBookModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_admin_add_book")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter un Nouveau Livre", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(allBooks) { book ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardDark),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(book.title, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                        Text("Auteur: ${book.author} • ${book.category}", color = TextSecondaryDark, fontSize = 11.sp)
                                        Text("${book.priceFcfa} FCFA • ${book.pages} p • ${book.fileSize}", color = Gold400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    IconButton(
                                        onClick = {
                                            bookToEdit = book
                                            showAddBookModal = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = TechBlue)
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteBook(book.id)
                                            Toast.makeText(context, "Livre supprimé", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = ErrorRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2: USERS LIST
            2 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(allUsers) { user ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Person, contentDescription = null, tint = TechBlue)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(user.displayName, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(user.email, color = TextSecondaryDark, fontSize = 12.sp)
                                }
                                if (user.isAdmin) {
                                    Surface(color = Gold500.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                                        Text("Admin", color = Gold500, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3: PASSWORD & SECURITY MANAGEMENT
            3 -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Modifier le mot de passe administrateur", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = newPasswordInput,
                            onValueChange = { newPasswordInput = it },
                            label = { Text("Nouveau mot de passe admin") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (newPasswordInput.isNotBlank()) {
                                    viewModel.changeAdminPassword(newPasswordInput)
                                    Toast.makeText(context, "Mot de passe administrateur mis à jour !", Toast.LENGTH_SHORT).show()
                                    newPasswordInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold500),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Mettre à jour", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Add / Edit Book Modal Dialog
        if (showAddBookModal) {
            AddOrEditBookDialog(
                bookToEdit = bookToEdit,
                onDismiss = { showAddBookModal = false },
                onSave = { book ->
                    viewModel.saveBook(book)
                    showAddBookModal = false
                    Toast.makeText(context, "Livre enregistré avec succès !", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditBookDialog(
    bookToEdit: BookEntity?,
    onDismiss: () -> Unit,
    onSave: (BookEntity) -> Unit
) {
    var title by remember { mutableStateOf(bookToEdit?.title ?: "") }
    var author by remember { mutableStateOf(bookToEdit?.author ?: "") }
    var description by remember { mutableStateOf(bookToEdit?.description ?: "") }
    var price by remember { mutableStateOf(bookToEdit?.priceFcfa?.toString() ?: "3500") }
    var pages by remember { mutableStateOf(bookToEdit?.pages?.toString() ?: "180") }
    var fileSize by remember { mutableStateOf(bookToEdit?.fileSize ?: "1.2 MB") }
    var format by remember { mutableStateOf(bookToEdit?.format ?: "PDF") }
    var category by remember { mutableStateOf(bookToEdit?.category ?: "Intelligence Artificielle & Tech") }
    var coverUrl by remember { mutableStateOf(bookToEdit?.coverUrl ?: "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&auto=format&fit=crop") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (bookToEdit == null) "Ajouter un livre" else "Modifier le livre", color = Color.White) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre du livre") }, singleLine = true)
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Auteur") }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, maxLines = 3)
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Prix (FCFA)") }, singleLine = true)
                OutlinedTextField(value = pages, onValueChange = { pages = it }, label = { Text("Nombre de pages") }, singleLine = true)
                OutlinedTextField(value = fileSize, onValueChange = { fileSize = it }, label = { Text("Taille du fichier (ex: 1.2 MB)") }, singleLine = true)
                OutlinedTextField(value = format, onValueChange = { format = it }, label = { Text("Format (PDF / EPUB)") }, singleLine = true)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Catégorie") }, singleLine = true)
                OutlinedTextField(value = coverUrl, onValueChange = { coverUrl = it }, label = { Text("Lien de l'image de couverture") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val book = BookEntity(
                        id = bookToEdit?.id ?: ("book_" + UUID.randomUUID().toString().take(8)),
                        title = title,
                        author = author,
                        description = description,
                        priceFcfa = price.toIntOrNull() ?: 3500,
                        pages = pages.toIntOrNull() ?: 180,
                        fileSize = fileSize,
                        format = format,
                        dateAdded = "2026-07-31",
                        category = category,
                        coverUrl = coverUrl,
                        sampleContent = description.take(150),
                        fullText = "Extrait complet du livre $title rédigé par $author.\n\n$description",
                        isPopular = bookToEdit?.isPopular ?: false,
                        isNew = bookToEdit?.isNew ?: true
                    )
                    onSave(book)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Gold500)
            ) {
                Text("Enregistrer", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextSecondaryDark)
            }
        },
        containerColor = CardDark
    )
}
