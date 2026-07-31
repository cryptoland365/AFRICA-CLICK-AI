package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AfricaClickLogo
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("chouamenikebel@gmail.com") }
    var password by remember { mutableStateOf("user123") }
    var name by remember { mutableStateOf("Kebel Chouameni") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }
    var showAdminLoginModal by remember { mutableStateOf(false) }
    var adminPassInput by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Navy900, Navy800, SurfaceDark)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Logo Header
            AfricaClickLogo(
                iconSize = 64.dp,
                showTagline = true,
                isLargeHeader = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = CardDark
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Tab Selector: Connexion / S'inscrire
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Navy900)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isSignUp) TechBlue else Color.Transparent)
                                .clickable { isSignUp = false }
                                .padding(vertical = 10.dp)
                                .testTag("tab_login"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Connexion",
                                fontWeight = FontWeight.Bold,
                                color = if (!isSignUp) Color.White else TextSecondaryDark
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSignUp) TechBlue else Color.Transparent)
                                .clickable { isSignUp = true }
                                .padding(vertical = 10.dp)
                                .testTag("tab_signup"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "S'inscrire",
                                fontWeight = FontWeight.Bold,
                                color = if (isSignUp) Color.White else TextSecondaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (authError != null) {
                        Text(
                            text = authError!!,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Sign Up Name Input
                    AnimatedVisibility(visible = isSignUp) {
                        Column {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nom complet") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Person, contentDescription = null, tint = TechBlue)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_name"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TechBlue,
                                    unfocusedBorderColor = Navy700
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Adresse E-mail") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = TechBlue)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_email"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            unfocusedBorderColor = Navy700
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = TechBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Afficher mot de passe",
                                    tint = TextSecondaryDark
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_password"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            unfocusedBorderColor = Navy700
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (!isSignUp) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showForgotPassword = true }) {
                                Text(
                                    text = "Mot de passe oublié ?",
                                    color = Gold400,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Main Submit Button
                    Button(
                        onClick = {
                            if (isSignUp) {
                                viewModel.registerUser(email, name, password) { success, msg ->
                                    if (!success && msg != null) {
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                viewModel.loginUser(email, password) { success, msg ->
                                    if (!success && msg != null) {
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_submit_auth"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TechBlue
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (isSignUp) "Créer mon compte" else "Se connecter",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider with Text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Navy700)
                        Text(
                            text = " OU ",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Navy700)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sign-In Button
                    OutlinedButton(
                        onClick = { viewModel.loginWithGoogleDemo() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_google_signin"),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(TechBlue, Gold500))),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Gold500,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continuer avec Google",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Admin Login Shortcut Link
            TextButton(
                onClick = { showAdminLoginModal = true },
                modifier = Modifier.testTag("btn_admin_portal")
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Gold500, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Accès Administrateur",
                    color = Gold500,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Forgot Password Dialog
        if (showForgotPassword) {
            var resetEmail by remember { mutableStateOf(email) }
            AlertDialog(
                onDismissRequest = { showForgotPassword = false },
                title = { Text("Réinitialisation du mot de passe", color = Color.White) },
                text = {
                    Column {
                        Text("Entrez votre e-mail pour recevoir les instructions de réinitialisation.", color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("E-mail") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetPassword(resetEmail) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                showForgotPassword = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechBlue)
                    ) {
                        Text("Envoyer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPassword = false }) {
                        Text("Annuler", color = TextSecondaryDark)
                    }
                },
                containerColor = CardDark
            )
        }

        // Admin Login Modal Dialog
        if (showAdminLoginModal) {
            AlertDialog(
                onDismissRequest = { showAdminLoginModal = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Gold500)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Espace Administration", color = Color.White)
                    }
                },
                text = {
                    Column {
                        Text("Saisissez le mot de passe administrateur pour accéder à la console de gestion.", color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = adminPassInput,
                            onValueChange = { adminPassInput = it },
                            label = { Text("Mot de passe admin (défaut: admin123)") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_admin_pass")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.loginAsAdmin(adminPassInput) { success ->
                                if (success) {
                                    showAdminLoginModal = false
                                    Toast.makeText(context, "Bienvenue Administrateur !", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Mot de passe administrateur incorrect", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Gold500),
                        modifier = Modifier.testTag("btn_confirm_admin_login")
                    ) {
                        Text("Connexion Admin", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAdminLoginModal = false }) {
                        Text("Annuler", color = TextSecondaryDark)
                    }
                },
                containerColor = CardDark
            )
        }
    }
}
