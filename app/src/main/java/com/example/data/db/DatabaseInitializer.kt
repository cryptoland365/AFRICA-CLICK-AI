package com.example.data.db

import com.example.data.models.BookEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.ReviewEntity
import com.example.data.models.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object DatabaseInitializer {

    suspend fun seedIfNecessary(db: AppDatabase) = withContext(Dispatchers.IO) {
        val bookCount = db.bookDao().getBookCount()
        if (bookCount == 0) {
            seedInitialData(db)
        }
    }

    private suspend fun seedInitialData(db: AppDatabase) {
        // Seed Users
        val adminUser = UserEntity(
            id = "admin_001",
            email = "admin@africaclickai.com",
            displayName = "Admin Africa Click",
            passwordHash = "admin123",
            isAdmin = true
        )
        val defaultUser = UserEntity(
            id = "user_001",
            email = "chouamenikebel@gmail.com",
            displayName = "Kebel Chouameni",
            passwordHash = "user123",
            isAdmin = false
        )
        db.userDao().insertUser(adminUser)
        db.userDao().insertUser(defaultUser)

        // Seed Books
        val books = listOf(
            BookEntity(
                id = "book_ai_001",
                title = "L'Intelligence Artificielle en Afrique : Guide Pratique",
                author = "Dr. Amina Konda & Africa Click AI Team",
                description = "Un guide complet et stratégique pour comprendre l'impact des algorithmes de Machine Learning, de l'IA générative et de l'automatisation dans le contexte économique et technologique africain.",
                priceFcfa = 5500,
                pages = 284,
                fileSize = "1.8 MB",
                format = "PDF",
                dateAdded = "2026-07-15",
                category = "Intelligence Artificielle & Tech",
                coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&auto=format&fit=crop",
                sampleContent = "L'Intelligence Artificielle en Afrique n'est plus une promesse futuriste, c'est une réalité opérationnelle qui transforme l'agriculture, la santé et la fintech...",
                fullText = """
                    CHAPITRE 1 : LA REVOLUTION DE L'IA EN AFRIQUE
                    
                    Bienvenue dans cet ouvrage visionnaire conçu par Africa Click AI. En Afrique, le saut technologique (leapfrogging) est une habitude. Tout comme le paiement mobile a supplanté le réseau bancaire traditionnel, l'IA s'impose aujourd'hui comme le principal moteur d'accélération économique du continent.
                    
                    1.1 Les opportunités clés
                    - Optimisation agricole grâce à la prédiction climatique et la télédétection.
                    - Diagnostics médicaux assistés par la vision par ordinateur dans les zones rurales.
                    - Traitement automatique du langage naturel pour les langues locales africaines.
                    
                    CHAPITRE 2 : CAS PRATIQUES ET OUTILS
                    
                    Pour déployer efficacement un système d'IA en entreprise, il convient de suivre trois piliers majeurs : la gouvernance des données, la sobriété numérique et l'intégration mobile-first.
                    
                    Conclusion et perspectives :
                    La jeunesse africaine possède l'énergie et la créativité nécessaires pour mener cette transformation mondiale.
                """.trimIndent(),
                isPopular = true,
                isNew = true,
                isPromotion = true,
                promoPriceFcfa = 4500,
                rating = 4.9f,
                reviewsCount = 38
            ),
            BookEntity(
                id = "book_biz_002",
                title = "Entreprendre et Réussir son Business en Afrique Central",
                author = "Jean-Marc Mba",
                description = "Les méthodes éprouvées pour créer, financer et développer une entreprise prospère au Gabon, au Cameroun et dans la zone CEMAC.",
                priceFcfa = 4000,
                pages = 196,
                fileSize = "950 KB",
                format = "PDF",
                dateAdded = "2026-06-20",
                category = "Business & Entreprenariat",
                coverUrl = "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=500&auto=format&fit=crop",
                sampleContent = "Créer une entreprise dynamique exige une maîtrise des enjeux juridiques localisés, du management des équipes et des canaux de distribution...",
                fullText = """
                    CHAPITRE 1 : DEFINIR UNE PROPOSITION DE VALEUR LOCALE
                    
                    L'entreprenariat en Afrique centrale repose sur la résolution de problèmes concrets du quotidien. Identifiez une douleur claire chez vos prospects.
                    
                    CHAPITRE 2 : FINANCEMENT ET FONDS DE ROULEMENT
                    
                    Les systèmes de financement par Mobile Money (Airtel Money, Moov Money) et les réseaux d'investisseurs régionaux représentent la première source de levée de fonds agile.
                """.trimIndent(),
                isPopular = true,
                isNew = false,
                isPromotion = false,
                rating = 4.7f,
                reviewsCount = 24
            ),
            BookEntity(
                id = "book_fin_003",
                title = "Indépendance Financière & Mobile Money Masterclass",
                author = "Sophie Nze & Emmanuel Kouamé",
                description = "Comment investir judicieusement ses revenus, constituer une épargne solide et exploiter les opportunités de la finance numérique.",
                priceFcfa = 3500,
                pages = 152,
                fileSize = "720 KB",
                format = "EPUB",
                dateAdded = "2026-07-01",
                category = "Finances & Économie",
                coverUrl = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=500&auto=format&fit=crop",
                sampleContent = "Gérer son budget avec sérénité commence par l'automatisation de son épargne et l'investissement dans des actifs productifs...",
                fullText = """
                    CHAPITRE 1 : LA REGLE DES 50/30/20 ADAPTEE
                    
                    Apprenez à diviser vos entrées financières : 50% pour les besoins vitaux, 30% pour les projets et 20% pour l'investissement passif.
                    
                    CHAPITRE 2 : SE PROTÉGER DE L'INFLATION
                    
                    Découvrez les actifs tangibles et les portefeuilles numériques pour protéger votre pouvoir d'achat.
                """.trimIndent(),
                isPopular = false,
                isNew = true,
                isPromotion = true,
                promoPriceFcfa = 2500,
                rating = 4.8f,
                reviewsCount = 19
            ),
            BookEntity(
                id = "book_dev_004",
                title = "Mindset d'Acier : Vaincre la Procrastination",
                author = "Marc-Aurèle Ondo",
                description = "Guide de psychologie comportementale pour développer une discipline personnelle inébranlable et atteindre tous vos objectifs.",
                priceFcfa = 3000,
                pages = 140,
                fileSize = "483 KB",
                format = "PDF",
                dateAdded = "2026-05-10",
                category = "Développement Personnel",
                coverUrl = "https://images.unsplash.com/photo-1506784983877-45594efa4cbe?w=500&auto=format&fit=crop",
                sampleContent = "La motivation est éphémère. Seule la structure et la clarté des habitudes quotidiennes garantissent des résultats durables...",
                fullText = """
                    CHAPITRE 1 : LA REGLE DES 5 SECONDES
                    
                    Lorsque vous ressentez l'impulsion d'agir sur un objectif, comptez 5, 4, 3, 2, 1 et passez immédiatement à l'action physique.
                    
                    CHAPITRE 2 : CONSTRUIRE UNE ROUTINE DU MATIN GAGNANTE
                    
                    Réveillez-vous avec un objectif clair, éloignez les distractions et concentrez-vous sur la tâche la plus importante.
                """.trimIndent(),
                isPopular = true,
                isNew = false,
                isPromotion = false,
                rating = 4.9f,
                reviewsCount = 42
            ),
            BookEntity(
                id = "book_media_005",
                title = "Stratégies de Marketing Digital & Création de Contenu",
                author = "Clarisse Biyogo",
                description = "Développez votre marque personnelle, captez l'attention sur TikTok, YouTube et LinkedIn, et convertissez vos abonnés en clients fidèles.",
                priceFcfa = 5000,
                pages = 210,
                fileSize = "2.1 MB",
                format = "PDF",
                dateAdded = "2026-07-22",
                category = "Marketing & Médias",
                coverUrl = "https://images.unsplash.com/photo-1432888622747-4eb9a8efeb07?w=500&auto=format&fit=crop",
                sampleContent = "Le storytelling authentique est la clé absolue pour captiver l'audience numérique africaine d'aujourd'hui...",
                fullText = """
                    CHAPITRE 1 : LES PILIERS DU STORYTELLING DIGITAL
                    
                    Pourquoi les histoires captivent plus que les arguments de vente directs. Analyse des campagnes virales les plus réussies.
                    
                    CHAPITRE 2 : OPTIMISER LE TUNNEL DE CONVERSION
                    
                    Du premier clic sur la vidéo à la commande WhatsApp, structurez un parcours utilisateur sans friction.
                """.trimIndent(),
                isPopular = false,
                isNew = true,
                isPromotion = false,
                rating = 4.6f,
                reviewsCount = 15
            ),
            BookEntity(
                id = "book_novel_006",
                title = "Les Ombres du Woleu : Roman d'Aventure et Mystère",
                author = "Josephine Ovono",
                description = "Un roman passionnant plongé au cœur des légendes équatoriales, mêlant suspense technologique et traditions ancestrales.",
                priceFcfa = 3000,
                pages = 320,
                fileSize = "1.2 MB",
                format = "EPUB",
                dateAdded = "2026-04-18",
                category = "Roman & Culture",
                coverUrl = "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500&auto=format&fit=crop",
                sampleContent = "Le brouillard du matin enveloppait doucement la grande forêt du Woleu-Ntem lorsque l'ingénieure Léa découvrit le signal mystérieux...",
                fullText = """
                    PROLOGUE
                    
                    Léa ajusta son scanner optique. Au milieu de la frondaison séculaire, une lueur dorée palpitait avec un rythme régulier...
                    
                    CHAPITRE 1 : LE SIGNAL DE LA FORÊT
                    
                    Les tambours lointains résonnaient dans la vallée tandis que les données s'affichaient sur son écran holographique.
                """.trimIndent(),
                isPopular = true,
                isNew = false,
                isPromotion = true,
                promoPriceFcfa = 2000,
                rating = 4.8f,
                reviewsCount = 31
            )
        )
        db.bookDao().insertAll(books)

        // Seed Initial Review
        val sampleReview = ReviewEntity(
            id = UUID.randomUUID().toString(),
            bookId = "book_ai_001",
            userId = "user_001",
            userName = "Kebel Chouameni",
            userEmail = "chouamenikebel@gmail.com",
            rating = 5,
            comment = "Un livre exceptionnel ! L'analyse de l'IA en Afrique est très pertinente et le paiement par WhatsApp a été instantané.",
            date = "2026-07-28"
        )
        db.reviewDao().insertReview(sampleReview)

        // Seed Initial Welcome Notification
        val welcomeNotif = NotificationEntity(
            id = UUID.randomUUID().toString(),
            userId = "user_001",
            title = "Bienvenue sur Africa Click AI ! 📚🤖",
            message = "Explorez notre catalogue d'eBooks numériques d'élite. Achetez facilement via WhatsApp au +241 77 24 45 15.",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            type = "SYSTEM"
        )
        db.notificationDao().insertNotification(welcomeNotif)
    }
}
