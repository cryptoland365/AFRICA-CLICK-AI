package com.example.data.repository

import android.util.Log
import com.example.data.db.AppDatabase
import com.example.data.models.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class AfricaClickRepository(private val db: AppDatabase) {

    // --- FIRESTORE INSTANCE ---
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    /**
     * Store book catalog details in Firestore
     */
    fun syncBookToFirestore(book: BookEntity) {
        try {
            val bookData = mapOf(
                "id" to book.id,
                "title" to book.title,
                "author" to book.author,
                "description" to book.description,
                "priceFcfa" to book.priceFcfa,
                "category" to book.category,
                "coverUrl" to book.coverUrl,
                "fileSize" to book.fileSize,
                "format" to book.format,
                "pages" to book.pages,
                "isPopular" to book.isPopular,
                "isNew" to book.isNew,
                "isPromotion" to book.isPromotion,
                "promoPriceFcfa" to (book.promoPriceFcfa ?: book.priceFcfa),
                "rating" to book.rating
            )
            firestore.collection("books").document(book.id).set(bookData)
        } catch (e: Exception) {
            Log.w("AfricaClickRepository", "Error storing book in Firestore: ${e.localizedMessage}")
        }
    }

    /**
     * Store user purchase order data in Firestore
     */
    fun syncOrderToFirestore(order: OrderEntity) {
        try {
            val orderData = mapOf(
                "id" to order.id,
                "userId" to order.userId,
                "userEmail" to order.userEmail,
                "bookId" to order.bookId,
                "bookTitle" to order.bookTitle,
                "bookPriceFcfa" to order.bookPriceFcfa,
                "bookCoverUrl" to order.bookCoverUrl,
                "orderDate" to order.orderDate,
                "status" to order.status,
                "whatsappMessage" to order.whatsappMessage,
                "deliveryEmail" to order.deliveryEmail
            )
            firestore.collection("orders").document(order.id).set(orderData)
        } catch (e: Exception) {
            Log.w("AfricaClickRepository", "Error storing order in Firestore: ${e.localizedMessage}")
        }
    }

    /**
     * Retrieve user order history from Firestore by user email
     */
    fun fetchUserOrdersFromFirestore(userEmail: String, onResult: (List<OrderEntity>) -> Unit) {
        try {
            firestore.collection("orders")
                .whereEqualTo("userEmail", userEmail)
                .get()
                .addOnSuccessListener { documents ->
                    val orders = documents.mapNotNull { doc ->
                        try {
                            OrderEntity(
                                id = doc.getString("id") ?: doc.id,
                                userId = doc.getString("userId") ?: "",
                                userEmail = doc.getString("userEmail") ?: userEmail,
                                bookId = doc.getString("bookId") ?: "",
                                bookTitle = doc.getString("bookTitle") ?: "",
                                bookPriceFcfa = doc.getLong("bookPriceFcfa")?.toInt() ?: 0,
                                bookCoverUrl = doc.getString("bookCoverUrl") ?: "",
                                orderDate = doc.getString("orderDate") ?: "",
                                status = doc.getString("status") ?: "PENDING",
                                whatsappMessage = doc.getString("whatsappMessage") ?: "",
                                deliveryEmail = doc.getString("deliveryEmail") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    onResult(orders)
                }
                .addOnFailureListener {
                    onResult(emptyList())
                }
        } catch (e: Exception) {
            Log.w("AfricaClickRepository", "Error fetching orders from Firestore: ${e.localizedMessage}")
            onResult(emptyList())
        }
    }

    // --- BOOKS ---
    val allBooks: Flow<List<BookEntity>> = db.bookDao().getAllBooks()
    val popularBooks: Flow<List<BookEntity>> = db.bookDao().getPopularBooks()
    val newBooks: Flow<List<BookEntity>> = db.bookDao().getNewBooks()
    val promotionBooks: Flow<List<BookEntity>> = db.bookDao().getPromotionBooks()
    val categories: Flow<List<String>> = db.bookDao().getCategories()

    suspend fun getBookById(id: String): BookEntity? = db.bookDao().getBookById(id)

    suspend fun insertBook(book: BookEntity) {
        db.bookDao().insertBook(book)
        syncBookToFirestore(book)
    }

    suspend fun updateBook(book: BookEntity) {
        db.bookDao().updateBook(book)
        syncBookToFirestore(book)
    }

    suspend fun deleteBook(id: String) {
        db.bookDao().deleteBookById(id)
        try {
            firestore.collection("books").document(id).delete()
        } catch (e: Exception) {
            Log.w("AfricaClickRepository", "Error deleting book from Firestore: ${e.localizedMessage}")
        }
    }

    // --- USERS & AUTH ---
    suspend fun getUserByEmail(email: String): UserEntity? = db.userDao().getUserByEmail(email)
    suspend fun getUserById(id: String): UserEntity? = db.userDao().getUserById(id)
    val allUsers: Flow<List<UserEntity>> = db.userDao().getAllUsers()

    suspend fun saveUser(user: UserEntity) {
        db.userDao().insertUser(user)
    }

    suspend fun authenticateUser(email: String, passwordHash: String): UserEntity? {
        val user = db.userDao().getUserByEmail(email.trim().lowercase())
        return if (user != null && (user.passwordHash == passwordHash || user.passwordHash.isBlank())) {
            user
        } else null
    }

    suspend fun registerUser(email: String, displayName: String, passwordHash: String, isAdmin: Boolean = false): UserEntity {
        val id = UUID.randomUUID().toString()
        val user = UserEntity(
            id = id,
            email = email.trim().lowercase(),
            displayName = displayName.ifBlank { email.substringBefore("@") },
            passwordHash = passwordHash,
            isAdmin = isAdmin
        )
        db.userDao().insertUser(user)
        
        // Add welcome notification
        val notif = NotificationEntity(
            id = UUID.randomUUID().toString(),
            userId = id,
            title = "Compte créé avec succès ! 🎉",
            message = "Bienvenue sur Africa Click AI. Parcourez notre catalogue et achetez vos livres via WhatsApp.",
            timestamp = System.currentTimeMillis()
        )
        db.notificationDao().insertNotification(notif)
        return user
    }

    suspend fun updateUser(user: UserEntity) = db.userDao().updateUser(user)

    // --- ORDERS & PAYMENTS ---
    fun getOrdersForUser(userId: String): Flow<List<OrderEntity>> = db.orderDao().getOrdersByUser(userId)
    val allOrders: Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
    val totalRevenue: Flow<Int?> = db.orderDao().getTotalRevenue()

    suspend fun createOrder(
        userId: String,
        userEmail: String,
        book: BookEntity,
        whatsappMsg: String
    ): OrderEntity {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val price = if (book.isPromotion && book.promoPriceFcfa != null) book.promoPriceFcfa else book.priceFcfa

        val order = OrderEntity(
            id = "CMD-" + System.currentTimeMillis().toString().takeLast(6),
            userId = userId,
            userEmail = userEmail,
            bookId = book.id,
            bookTitle = book.title,
            bookPriceFcfa = price,
            bookCoverUrl = book.coverUrl,
            orderDate = currentDate,
            status = "PENDING",
            whatsappMessage = whatsappMsg,
            deliveryEmail = userEmail
        )
        db.orderDao().insertOrder(order)
        syncOrderToFirestore(order)

        // Notify user about order placement
        val notif = NotificationEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = "Commande transmise sur WhatsApp 📲",
            message = "Votre demande pour '${book.title}' (${price} FCFA) a été initiée. Dès validation du paiement, votre e-mail recevra le livre.",
            timestamp = System.currentTimeMillis(),
            type = "PURCHASE"
        )
        db.notificationDao().insertNotification(notif)

        // Notify admin
        val adminNotif = NotificationEntity(
            id = UUID.randomUUID().toString(),
            userId = "admin_001",
            title = "Nouvelle commande WhatsApp !",
            message = "Client: $userEmail | Livre: ${book.title} | ${price} FCFA",
            timestamp = System.currentTimeMillis(),
            type = "PURCHASE"
        )
        db.notificationDao().insertNotification(adminNotif)

        return order
    }

    suspend fun validatePaymentAndDeliver(orderId: String) {
        val order = db.orderDao().getOrderById(orderId) ?: return
        db.orderDao().updateOrderStatus(orderId, "DELIVERED")
        syncOrderToFirestore(order.copy(status = "DELIVERED"))

        // Auto delivery notification sent to customer
        val userNotif = NotificationEntity(
            id = UUID.randomUUID().toString(),
            userId = order.userId,
            title = "Paiement Validé ! Livre Disponible 🚀",
            message = "Votre achat de '${order.bookTitle}' a été validé par africaclickai@gmail.com. Le fichier est débloqué dans votre bibliothèque et envoyé à ${order.deliveryEmail}.",
            timestamp = System.currentTimeMillis(),
            type = "DELIVERY"
        )
        db.notificationDao().insertNotification(userNotif)
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        db.orderDao().updateOrderStatus(orderId, status)
        val order = db.orderDao().getOrderById(orderId)
        if (order != null) {
            syncOrderToFirestore(order)
        }
    }

    // --- FAVORITES ---
    fun getFavoriteIds(userId: String): Flow<List<String>> = db.favoriteDao().getFavoriteBookIds(userId)
    fun isFavorite(userId: String, bookId: String): Flow<Boolean> = db.favoriteDao().isFavorite(userId, bookId)

    suspend fun toggleFavorite(userId: String, bookId: String, isFav: Boolean) {
        if (isFav) {
            db.favoriteDao().removeFavorite(userId, bookId)
        } else {
            db.favoriteDao().addFavorite(FavoriteEntity(userId, bookId))
        }
    }

    // --- REVIEWS ---
    fun getReviews(bookId: String): Flow<List<ReviewEntity>> = db.reviewDao().getReviewsForBook(bookId)

    suspend fun addReview(bookId: String, userId: String, userName: String, userEmail: String, rating: Int, comment: String) {
        val review = ReviewEntity(
            id = UUID.randomUUID().toString(),
            bookId = bookId,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            rating = rating,
            comment = comment,
            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )
        db.reviewDao().insertReview(review)
    }

    // --- NOTIFICATIONS ---
    fun getNotifications(userId: String): Flow<List<NotificationEntity>> = db.notificationDao().getNotificationsForUser(userId)
    suspend fun markNotificationsRead(userId: String) = db.notificationDao().markAllAsRead(userId)
}
