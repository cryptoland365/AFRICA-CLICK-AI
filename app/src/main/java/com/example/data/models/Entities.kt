package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val description: String,
    val priceFcfa: Int,
    val pages: Int,
    val fileSize: String,
    val format: String = "PDF",
    val dateAdded: String,
    val category: String,
    val coverUrl: String,
    val sampleContent: String,
    val fullText: String,
    val isPopular: Boolean = false,
    val isNew: Boolean = false,
    val isPromotion: Boolean = false,
    val promoPriceFcfa: Int? = null,
    val isAvailable: Boolean = true,
    val rating: Float = 4.8f,
    val reviewsCount: Int = 12
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val isAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userEmail: String,
    val bookId: String,
    val bookTitle: String,
    val bookPriceFcfa: Int,
    val bookCoverUrl: String,
    val orderDate: String,
    val status: String = "PENDING", // PENDING, APPROVED, DELIVERED, REJECTED
    val whatsappMessage: String,
    val deliveryEmail: String
)

@Entity(tableName = "favorites", primaryKeys = ["userId", "bookId"])
data class FavoriteEntity(
    val userId: String,
    val bookId: String
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val bookId: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val rating: Int,
    val comment: String,
    val date: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "GENERAL"
)
