package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.DatabaseInitializer
import com.example.data.models.*
import com.example.data.repository.AfricaClickRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption {
    POPULAR, NEWEST, PRICE_ASC, PRICE_DESC
}

enum class NavigationTab {
    HOME, CATALOG, LIBRARY, CONTACT, ADMIN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = AfricaClickRepository(db)

    // Current Auth State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _adminPassword = MutableStateFlow("admin123")
    val adminPassword: StateFlow<String> = _adminPassword.asStateFlow()

    // Navigation & Screen States
    private val _activeTab = MutableStateFlow(NavigationTab.HOME)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _selectedBookForDetail = MutableStateFlow<BookEntity?>(null)
    val selectedBookForDetail: StateFlow<BookEntity?> = _selectedBookForDetail.asStateFlow()

    private val _selectedBookForPayment = MutableStateFlow<BookEntity?>(null)
    val selectedBookForPayment: StateFlow<BookEntity?> = _selectedBookForPayment.asStateFlow()

    private val _selectedBookForReader = MutableStateFlow<BookEntity?>(null)
    val selectedBookForReader: StateFlow<BookEntity?> = _selectedBookForReader.asStateFlow()

    // Search, Filter & Sorting
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedFormat = MutableStateFlow<String?>(null)
    val selectedFormat: StateFlow<String?> = _selectedFormat.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.POPULAR)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    // UI Preferences
    private val _language = MutableStateFlow("FR") // "FR" or "EN"
    val language: StateFlow<String> = _language.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _showNotifications = MutableStateFlow(false)
    val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()

    // AI Recommendation Assistant
    private val _aiPrompt = MutableStateFlow("")
    val aiPrompt: StateFlow<String> = _aiPrompt.asStateFlow()

    private val _aiResult = MutableStateFlow<String?>(null)
    val aiResult: StateFlow<String?> = _aiResult.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Data Flows from Repository
    val allBooks: StateFlow<List<BookEntity>> = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularBooks: StateFlow<List<BookEntity>> = repository.popularBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newBooks: StateFlow<List<BookEntity>> = repository.newBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val promotionBooks: StateFlow<List<BookEntity>> = repository.promotionBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRevenue: StateFlow<Int?> = repository.totalRevenue
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val userOrders: StateFlow<List<OrderEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getOrdersForUser(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userFavoriteIds: StateFlow<List<String>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getFavoriteIds(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userNotifications: StateFlow<List<NotificationEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getNotifications(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            DatabaseInitializer.seedIfNecessary(db)
            // Auto login default user for immediate testability if preferred
            loginUser("chouamenikebel@gmail.com", "user123")
        }
    }

    // --- AUTHENTICATION ACTIONS ---
    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            _authError.value = null
            val emailClean = email.trim().lowercase()
            if (emailClean.isBlank() || password.isBlank()) {
                _authError.value = "Veuillez remplir tous les champs."
                onResult(false, "Veuillez remplir tous les champs.")
                return@launch
            }
            val user = repository.getUserByEmail(emailClean)
            if (user != null && user.passwordHash == password) {
                _currentUser.value = user
                onResult(true, null)
            } else {
                _authError.value = "Adresse e-mail ou mot de passe incorrect."
                onResult(false, "Adresse e-mail ou mot de passe incorrect.")
            }
        }
    }

    fun registerUser(email: String, name: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val emailClean = email.trim().lowercase()
            if (emailClean.isBlank() || password.length < 4) {
                onResult(false, "Veuillez fournir un e-mail valide et un mot de passe d'au moins 4 caractères.")
                return@launch
            }
            val existing = repository.getUserByEmail(emailClean)
            if (existing != null) {
                onResult(false, "Cet e-mail est déjà utilisé.")
                return@launch
            }
            val newUser = repository.registerUser(emailClean, name, password, isAdmin = false)
            _currentUser.value = newUser
            onResult(true, null)
        }
    }

    fun loginWithGoogleDemo() {
        viewModelScope.launch {
            val googleEmail = "google.user@africaclickai.com"
            var user = repository.getUserByEmail(googleEmail)
            if (user == null) {
                user = repository.registerUser(googleEmail, "Utilisateur Google", "google_oauth_pass")
            }
            _currentUser.value = user
        }
    }

    fun loginAsAdmin(password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (password == _adminPassword.value) {
                val admin = repository.getUserByEmail("admin@africaclickai.com")
                    ?: repository.registerUser("admin@africaclickai.com", "Admin Africa Click", password, isAdmin = true)
                _currentUser.value = admin
                _activeTab.value = NavigationTab.ADMIN
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun changeAdminPassword(newPass: String) {
        if (newPass.isNotBlank()) {
            _adminPassword.value = newPass
        }
    }

    fun logout() {
        _currentUser.value = null
        _activeTab.value = NavigationTab.HOME
    }

    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email.trim().lowercase())
            if (user != null) {
                onResult(true, "Un lien de réinitialisation a été envoyé à ${user.email}.")
            } else {
                onResult(false, "Aucun compte trouvé avec cette adresse e-mail.")
            }
        }
    }

    // --- NAVIGATION & VIEWS ---
    fun selectTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun openBookDetail(book: BookEntity?) {
        _selectedBookForDetail.value = book
    }

    fun openPaymentModal(book: BookEntity) {
        _selectedBookForPayment.value = book
    }

    fun closePaymentModal() {
        _selectedBookForPayment.value = null
    }

    fun openReader(book: BookEntity) {
        _selectedBookForReader.value = book
    }

    fun closeReader() {
        _selectedBookForReader.value = null
    }

    fun toggleNotifications() {
        _showNotifications.value = !_showNotifications.value
        val user = _currentUser.value
        if (user != null && _showNotifications.value) {
            viewModelScope.launch {
                repository.markNotificationsRead(user.id)
            }
        }
    }

    // --- SEARCH, FILTERS & SORT ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun setFormatFilter(format: String?) {
        _selectedFormat.value = if (_selectedFormat.value == format) null else format
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == "FR") "EN" else "FR"
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleFavorite(bookId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val isFav = userFavoriteIds.value.contains(bookId)
            repository.toggleFavorite(user.id, bookId, isFav)
        }
    }

    // --- ORDERS & WHATSAPP ---
    fun createWhatsAppPurchaseOrder(book: BookEntity): String {
        val user = _currentUser.value
        val userEmail = user?.email ?: "client@africaclickai.com"
        val price = if (book.isPromotion && book.promoPriceFcfa != null) "${book.promoPriceFcfa} FCFA" else "${book.priceFcfa} FCFA"
        
        val prefilledMsg = """
            Bonjour.
            
            Je souhaite acheter le livre :
            ${book.title}
            
            Prix :
            $price
            
            Mon adresse e-mail est :
            $userEmail
            
            Merci.
        """.trimIndent()

        if (user != null) {
            viewModelScope.launch {
                repository.createOrder(
                    userId = user.id,
                    userEmail = userEmail,
                    book = book,
                    whatsappMsg = prefilledMsg
                )
            }
        }

        return prefilledMsg
    }

    // --- ADMIN ACTIONS ---
    fun validatePayment(orderId: String) {
        viewModelScope.launch {
            repository.validatePaymentAndDeliver(orderId)
        }
    }

    fun saveBook(book: BookEntity) {
        viewModelScope.launch {
            if (allBooks.value.any { it.id == book.id }) {
                repository.updateBook(book)
            } else {
                repository.insertBook(book)
            }
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            repository.deleteBook(bookId)
        }
    }

    fun submitReview(bookId: String, rating: Int, comment: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addReview(
                bookId = bookId,
                userId = user.id,
                userName = user.displayName,
                userEmail = user.email,
                rating = rating,
                comment = comment
            )
        }
    }

    // --- AI ASSISTANT RECOMMENDATION ---
    fun updateAiPrompt(prompt: String) {
        _aiPrompt.value = prompt
    }

    fun generateAiRecommendation() {
        val input = _aiPrompt.value.trim()
        if (input.isBlank()) return
        
        _isAiLoading.value = true
        _aiResult.value = null

        viewModelScope.launch {
            kotlinx.coroutines.delay(1200) // Realistic AI synthesis delay
            val queryLower = input.lowercase()
            val matches = allBooks.value.filter {
                it.title.lowercase().contains(queryLower) ||
                it.description.lowercase().contains(queryLower) ||
                it.category.lowercase().contains(queryLower)
            }

            val recommendationText = if (matches.isNotEmpty()) {
                val bestMatch = matches.first()
                "🤖 **Assistant Africa Click AI** :\n\nD'après votre besoin ('$input'), nous vous recommandons vivement :\n\n📖 **${bestMatch.title}** par ${bestMatch.author}\n*Catégorie : ${bestMatch.category}*\n\n> ${bestMatch.description}\n\nPrix : ${if(bestMatch.isPromotion && bestMatch.promoPriceFcfa != null) bestMatch.promoPriceFcfa else bestMatch.priceFcfa} FCFA"
            } else {
                "🤖 **Assistant Africa Click AI** :\n\nPour réussir dans '$input', découvrez notre best-seller **'L'Intelligence Artificielle en Afrique : Guide Pratique'** et **'Entreprendre et Réussir en Afrique Centrale'**. Ces ouvrages contiennent toutes les clés méthodologiques !"
            }

            _aiResult.value = recommendationText
            _isAiLoading.value = false
        }
    }
}
