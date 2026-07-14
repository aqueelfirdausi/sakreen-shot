package com.sakreenshot.app.data.classification

class TextClassifier {

    fun classify(text: String): Category {
        if (text.isBlank()) return Category.UNSORTED

        val normalized = text.lowercase()

        val paymentKeywords = listOf(
            "receipt", "paid", "transfer", "amount", "invoice", "balance", 
            "transaction", "payment", "bank", "card", "tax", "fee",
            "jazzcash", "easypaisa", "raast", "iban", "tracking number", 
            "dispatch", "order number", "payment successful"
        )
        val chatKeywords = listOf(
            "whatsapp", "telegram", "message", "chat", "online", 
            "typing...", "sms", "imessage", "messenger", "customer message"
        )
        val documentKeywords = listOf(
            "article", "report", "terms", "conditions", "contract", 
            "agreement", "policy", "page", "chapter", "read", "cnic", "supplier", "customer"
        )

        var paymentScore = 0
        var chatScore = 0
        var documentScore = 0

        for (word in paymentKeywords) {
            if (normalized.contains(word)) paymentScore++
        }
        for (word in chatKeywords) {
            if (normalized.contains(word)) chatScore++
        }
        for (word in documentKeywords) {
            if (normalized.contains(word)) documentScore++
        }

        // Add some regex bonuses for payments (like $100.00, or £50, or Rs. / PKR)
        if (Regex("[$£€₹¥]|rs\\.?\\s*|pkr\\s*\\d+").containsMatchIn(normalized)) paymentScore += 2
        if (Regex("\\d+\\.\\d{2}").containsMatchIn(normalized)) paymentScore += 1

        // Add some regex bonuses for chats (time formats like 10:45 AM)
        if (Regex("\\d{1,2}:\\d{2}\\s?(am|pm)?").containsMatchIn(normalized)) chatScore += 1

        val maxScore = maxOf(paymentScore, chatScore, documentScore)

        return when {
            maxScore == 0 -> Category.UNSORTED
            maxScore == paymentScore -> Category.PAYMENTS
            maxScore == chatScore -> Category.CHATS
            maxScore == documentScore -> Category.DOCUMENTS
            else -> Category.UNSORTED
        }
    }
}
