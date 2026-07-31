package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val isDarkMode by mainViewModel.isDarkMode.collectAsState()

            AfricaClickTheme(darkTheme = isDarkMode) {
                MainAppContent(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedDetailBook by viewModel.selectedBookForDetail.collectAsState()
    val selectedPaymentBook by viewModel.selectedBookForPayment.collectAsState()
    val selectedReaderBook by viewModel.selectedBookForReader.collectAsState()
    val showNotifications by viewModel.showNotifications.collectAsState()
    val userNotifications by viewModel.userNotifications.collectAsState()
    val userOrders by viewModel.userOrders.collectAsState()
    val userFavoriteIds by viewModel.userFavoriteIds.collectAsState()

    val unlockedBookIds = remember(userOrders) {
        userOrders.filter { it.status == "DELIVERED" || it.status == "APPROVED" }.map { it.bookId }.toSet()
    }

    // MANDATORY REQUIREMENT 1: User must log in before accessing the app!
    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
    } else {
        // FULL EBOOK READER OVERLAY (IF OPEN)
        if (selectedReaderBook != null) {
            EbookReaderScreen(
                book = selectedReaderBook!!,
                onClose = { viewModel.closeReader() }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 6.dp
                    ) {
                        NavigationBarItem(
                            selected = activeTab == NavigationTab.HOME,
                            onClick = { viewModel.selectTab(NavigationTab.HOME) },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == NavigationTab.HOME) Icons.Default.Home else Icons.Outlined.Home,
                                    contentDescription = "Accueil"
                                )
                            },
                            label = { Text("Accueil", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                indicatorColor = BlueSelection,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("nav_home")
                        )

                        NavigationBarItem(
                            selected = activeTab == NavigationTab.CATALOG,
                            onClick = { viewModel.selectTab(NavigationTab.CATALOG) },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == NavigationTab.CATALOG) Icons.Default.MenuBook else Icons.Outlined.MenuBook,
                                    contentDescription = "Livres"
                                )
                            },
                            label = { Text("Livres", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                indicatorColor = BlueSelection,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("nav_catalog")
                        )

                        NavigationBarItem(
                            selected = activeTab == NavigationTab.LIBRARY || activeTab == NavigationTab.ADMIN,
                            onClick = {
                                if (currentUser?.isAdmin == true) {
                                    viewModel.selectTab(NavigationTab.ADMIN)
                                } else {
                                    viewModel.selectTab(NavigationTab.LIBRARY)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentUser?.isAdmin == true) {
                                        if (activeTab == NavigationTab.ADMIN) Icons.Default.Shield else Icons.Outlined.Shield
                                    } else {
                                        if (activeTab == NavigationTab.LIBRARY) Icons.Default.AccountCircle else Icons.Outlined.AccountCircle
                                    },
                                    contentDescription = if (currentUser?.isAdmin == true) "Admin" else "Profil"
                                )
                            },
                            label = {
                                Text(
                                    text = if (currentUser?.isAdmin == true) "Admin" else "Profil",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (currentUser?.isAdmin == true) Gold500 else PrimaryBlue,
                                selectedTextColor = if (currentUser?.isAdmin == true) Gold500 else PrimaryBlue,
                                indicatorColor = BlueSelection,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("nav_profile")
                        )

                        NavigationBarItem(
                            selected = activeTab == NavigationTab.CONTACT,
                            onClick = { viewModel.selectTab(NavigationTab.CONTACT) },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == NavigationTab.CONTACT) Icons.Default.Info else Icons.Outlined.Info,
                                    contentDescription = "Contact"
                                )
                            },
                            label = { Text("Contact", fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechBlue,
                                selectedTextColor = TechBlue,
                                indicatorColor = Navy800
                            ),
                            modifier = Modifier.testTag("nav_contact")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (activeTab) {
                        NavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                        NavigationTab.CATALOG -> CatalogScreen(viewModel = viewModel)
                        NavigationTab.LIBRARY -> UserDashboardScreen(viewModel = viewModel)
                        NavigationTab.ADMIN -> AdminDashboardScreen(viewModel = viewModel)
                        NavigationTab.CONTACT -> ContactAndInfoScreen()
                    }

                    // MODALS & DIALOGS
                    if (selectedDetailBook != null) {
                        BookDetailSheet(
                            book = selectedDetailBook!!,
                            viewModel = viewModel,
                            isUnlocked = unlockedBookIds.contains(selectedDetailBook!!.id),
                            isFavorite = userFavoriteIds.contains(selectedDetailBook!!.id),
                            onClose = { viewModel.openBookDetail(null) }
                        )
                    }

                    if (selectedPaymentBook != null) {
                        WhatsAppPaymentModal(
                            book = selectedPaymentBook!!,
                            viewModel = viewModel,
                            onDismiss = { viewModel.closePaymentModal() }
                        )
                    }

                    if (showNotifications) {
                        NotificationsDialog(
                            notifications = userNotifications,
                            onDismiss = { viewModel.toggleNotifications() }
                        )
                    }
                }
            }
        }
    }
}
