package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AfricaClickLogo
import com.example.ui.theme.*

@Composable
fun ContactAndInfoScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        AfricaClickLogo(
            iconSize = 52.dp,
            showTagline = true,
            isLargeHeader = true
        )

        // Contact Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Nous contacter 📞",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Gold500
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("WhatsApp Commercial : +241 77 24 45 15", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = TechBlue)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("E-mail Professionnel : contact@africaclick.com", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = ErrorRed)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Siège : Libreville, Gabon • Afrique Centrale", color = TextSecondaryDark)
                }
            }
        }

        // About Us Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "À Propos d'AFRICA CLICK AI 📚",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AFRICA CLICK AI est la plateforme pionnière de diffusion du savoir et d'ouvrages numériques en Afrique. Notre mission est de démocratiser l'accès aux livres électroniques d'élite (PDF & EPUB) en permettant des transactions simples et sécurisées par Mobile Money et WhatsApp.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    lineHeight = 22.sp
                )
            }
        }

        // Terms & Privacy
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Politique de Confidentialité & CGU",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Protection des données : Vos informations personnelles et adresses e-mail sont strictement confidentielles.\n• Livraison : Tout livre acheté sur WhatsApp (+241 77 24 45 15) est transmis par e-mail via contact@africaclick.com et débloqué directement dans votre compte utilisateur.\n• Anti-piratage : La redistribution des fichiers PDF non autorisée est strictly interdite.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
