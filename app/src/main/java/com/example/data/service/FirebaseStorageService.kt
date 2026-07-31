package com.example.data.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

/**
 * FirebaseStorageService handles secure PDF eBook file storage, uploading,
 * download URL retrieval, and local PDF file caching for offline reading.
 */
class FirebaseStorageService {

    private val tag = "FirebaseStorageService"

    // Lazily initialized Firebase Storage instance
    private val storage: FirebaseStorage by lazy {
        try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            Log.w(tag, "FirebaseStorage initialization warning: ${e.localizedMessage}")
            FirebaseStorage.getInstance()
        }
    }

    /**
     * Storage root reference for eBooks
     */
    private val eBooksRef: StorageReference
        get() = storage.reference.child("ebooks/pdfs")

    /**
     * Upload an eBook PDF file to Firebase Storage.
     * @param bookId Unique identifier of the book
     * @param fileUri Local Uri of the PDF file to upload
     * @param onProgress Callback receiving percentage progress (0..100)
     * @return Download URL String on successful upload
     */
    suspend fun uploadEbookPdf(
        bookId: String,
        fileUri: Uri,
        onProgress: ((Int) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileRef = eBooksRef.child("$bookId.pdf")
            val uploadTask = fileRef.putFile(fileUri)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                onProgress?.invoke(progress)
            }

            uploadTask.await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            Log.d(tag, "PDF uploaded successfully for $bookId: $downloadUrl")
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(tag, "Error uploading PDF for $bookId", e)
            Result.failure(e)
        }
    }

    /**
     * Securely fetches the download URL for a given eBook PDF path or ID.
     */
    suspend fun getEbookDownloadUrl(bookIdOrPath: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val ref = if (bookIdOrPath.startsWith("gs://") || bookIdOrPath.startsWith("https://")) {
                storage.getReferenceFromUrl(bookIdOrPath)
            } else if (bookIdOrPath.contains("/")) {
                storage.reference.child(bookIdOrPath)
            } else {
                eBooksRef.child("$bookIdOrPath.pdf")
            }
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Log.e(tag, "Failed to get download URL for $bookIdOrPath", e)
            Result.failure(e)
        }
    }

    /**
     * Downloads an eBook PDF securely to local app cache directory for reading.
     */
    suspend fun downloadEbookPdfToCache(
        context: Context,
        bookId: String,
        pdfPathOrUrl: String,
        onProgress: ((Int) -> Unit)? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val localFile = File(context.cacheDir, "ebook_$bookId.pdf")
            if (localFile.exists() && localFile.length() > 0) {
                // Return cached file directly if present
                Log.d(tag, "eBook $bookId already cached at ${localFile.absolutePath}")
                return@withContext Result.success(localFile)
            }

            val ref = if (pdfPathOrUrl.startsWith("gs://") || pdfPathOrUrl.startsWith("https://")) {
                storage.getReferenceFromUrl(pdfPathOrUrl)
            } else if (pdfPathOrUrl.contains("/")) {
                storage.reference.child(pdfPathOrUrl)
            } else {
                eBooksRef.child("$pdfPathOrUrl.pdf")
            }

            val downloadTask = ref.getFile(localFile)
            downloadTask.addOnProgressListener { taskSnapshot ->
                if (taskSnapshot.totalByteCount > 0) {
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                    onProgress?.invoke(progress)
                }
            }

            downloadTask.await()
            Log.d(tag, "Downloaded eBook PDF successfully to ${localFile.absolutePath}")
            Result.success(localFile)
        } catch (e: Exception) {
            Log.e(tag, "Error downloading eBook PDF for $bookId", e)
            Result.failure(e)
        }
    }
}
