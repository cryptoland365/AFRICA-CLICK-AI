package com.example.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Data class representing an eBook structure with title, author, price, description,
 * cover URL, and file download URL, with Moshi JSON serialization support.
 */
@JsonClass(generateAdapter = true)
data class Book(
    @Json(name = "id")
    val id: String = "",

    @Json(name = "title")
    val title: String,

    @Json(name = "author")
    val author: String,

    @Json(name = "price")
    val price: Double = 0.0,

    @Json(name = "priceFcfa")
    val priceFcfa: Int = 0,

    @Json(name = "description")
    val description: String = "",

    @Json(name = "fileDownloadUrl")
    val fileDownloadUrl: String = "",

    @Json(name = "coverUrl")
    val coverUrl: String = "",

    @Json(name = "category")
    val category: String = "Général",

    @Json(name = "pages")
    val pages: Int = 0,

    @Json(name = "fileSize")
    val fileSize: String = "0 MB",

    @Json(name = "format")
    val format: String = "PDF",

    @Json(name = "isPopular")
    val isPopular: Boolean = false,

    @Json(name = "isNew")
    val isNew: Boolean = false,

    @Json(name = "rating")
    val rating: Float = 4.8f
) {
    /**
     * Serializes this Book instance to a JSON string.
     */
    fun toJson(): String {
        return moshi.adapter(Book::class.java).toJson(this)
    }

    /**
     * Converts this Book model to a Room database BookEntity.
     */
    fun toEntity(): BookEntity {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return BookEntity(
            id = id.ifBlank { UUID.randomUUID().toString() },
            title = title,
            author = author,
            description = description,
            priceFcfa = if (priceFcfa > 0) priceFcfa else price.toInt(),
            pages = pages,
            fileSize = fileSize,
            format = format,
            dateAdded = dateFormat.format(Date()),
            category = category,
            coverUrl = coverUrl,
            sampleContent = description.take(200),
            fullText = description,
            isPopular = isPopular,
            isNew = isNew,
            rating = rating
        )
    }

    companion object {
        private val moshi: Moshi by lazy {
            Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        }

        /**
         * Deserializes a JSON string into a Book instance.
         */
        fun fromJson(json: String): Book? {
            return try {
                moshi.adapter(Book::class.java).fromJson(json)
            } catch (e: Exception) {
                null
            }
        }

        /**
         * Creates a Book data model from a Room database BookEntity.
         */
        fun fromEntity(entity: BookEntity, downloadUrl: String = ""): Book {
            return Book(
                id = entity.id,
                title = entity.title,
                author = entity.author,
                price = entity.priceFcfa.toDouble(),
                priceFcfa = entity.priceFcfa,
                description = entity.description,
                fileDownloadUrl = downloadUrl,
                coverUrl = entity.coverUrl,
                category = entity.category,
                pages = entity.pages,
                fileSize = entity.fileSize,
                format = entity.format,
                isPopular = entity.isPopular,
                isNew = entity.isNew,
                rating = entity.rating
            )
        }
    }
}
