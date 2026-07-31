package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder

/**
 * PaymentHandler object responsible for generating WhatsApp payment intent URIs
 * and message templates for eBook orders.
 */
object PaymentHandler {

    /**
     * Official WhatsApp contact number for Mobile Money payment confirmation.
     */
    const val PHONE_NUMBER: String = "+24177244515"

    /**
     * Formats the WhatsApp message template dynamically with book title, price, and optional user email.
     */
    fun createMessage(title: String, price: String, userEmail: String? = null): String {
        val emailSection = if (!userEmail.isNullOrBlank()) {
            "\n\nMon adresse e-mail est :\n$userEmail"
        } else ""

        return """
            Bonjour,

            Je souhaite acheter le livre :
            $title

            Prix :
            $price$emailSection

            Merci.
        """.trimIndent()
    }

    /**
     * Generates the WhatsApp Uri object containing the recipient phone number and URL-encoded message.
     */
    fun generateWhatsAppUri(title: String, price: String, userEmail: String? = null): Uri {
        return Uri.parse(generateWhatsAppUriString(title, price, userEmail))
    }

    /**
     * Generates the raw WhatsApp intent URI string.
     */
    fun generateWhatsAppUriString(title: String, price: String, userEmail: String? = null): String {
        val cleanPhone = PHONE_NUMBER.replace("+", "").replace(" ", "").trim()
        val message = createMessage(title, price, userEmail)
        val encodedMessage = try {
            URLEncoder.encode(message, "UTF-8")
        } catch (e: Exception) {
            Uri.encode(message)
        }
        return "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
    }

    /**
     * Creates an Intent to launch WhatsApp with the pre-filled order details.
     */
    fun createWhatsAppIntent(title: String, price: String, userEmail: String? = null): Intent {
        val uri = generateWhatsAppUri(title, price, userEmail)
        return Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }
    }

    /**
     * Creates a fallback Intent that opens via web browser if the native app is not installed.
     */
    fun createWhatsAppBrowserIntent(title: String, price: String, userEmail: String? = null): Intent {
        val uri = generateWhatsAppUri(title, price, userEmail)
        return Intent(Intent.ACTION_VIEW, uri)
    }

    /**
     * Helper to directly launch WhatsApp from a Android Context with browser fallback.
     */
    fun launchWhatsAppPayment(context: Context, title: String, price: String, userEmail: String? = null): Boolean {
        return try {
            val intent = createWhatsAppIntent(title, price, userEmail)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val browserIntent = createWhatsAppBrowserIntent(title, price, userEmail)
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(browserIntent)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}
