package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.models.UserEntity
import com.example.data.repository.AfricaClickRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: UserEntity) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

typealias AuthenticationViewModel = AuthViewModel

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "AuthViewModel"
    private val db = AppDatabase.getDatabase(application)
    private val repository = AfricaClickRepository(db)

    // Firebase Auth instance
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // UI States
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Check initial auth state from Firebase or local DB
        checkCurrentAuthState()
    }

    private fun checkCurrentAuthState() {
        viewModelScope.launch {
            try {
                val fbUser = firebaseAuth.currentUser
                if (fbUser != null) {
                    val userEntity = UserEntity(
                        id = fbUser.uid,
                        email = fbUser.email ?: "",
                        displayName = fbUser.displayName ?: fbUser.email?.substringBefore("@") ?: "Utilisateur",
                        passwordHash = "",
                        isAdmin = fbUser.email?.lowercase() == "admin@africaclick.ai" || fbUser.email?.lowercase() == "chouamenikebel@gmail.com"
                    )
                    _currentUser.value = userEntity
                    _uiState.value = AuthUiState.Success(userEntity)
                    repository.saveUser(userEntity)
                }
            } catch (e: Exception) {
                Log.w(tag, "Checking initial auth state: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Sign In with Email & Password using Firebase Auth (with local DB fallback)
     */
    fun signInWithEmail(email: String, password: String, onComplete: ((Boolean) -> Unit)? = null) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Veuillez remplir l'email et le mot de passe."
            _uiState.value = AuthUiState.Error("Veuillez remplir tous les champs.")
            onComplete?.invoke(false)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = AuthUiState.Loading
            _errorMessage.value = null

            try {
                // Try Firebase Auth Sign In
                val authResult = firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
                val fbUser = authResult.user

                if (fbUser != null) {
                    val userEntity = UserEntity(
                        id = fbUser.uid,
                        email = fbUser.email ?: email.trim(),
                        displayName = fbUser.displayName ?: email.substringBefore("@"),
                        passwordHash = "",
                        isAdmin = email.trim().lowercase() == "admin@africaclick.ai" || email.trim().lowercase() == "chouamenikebel@gmail.com"
                    )
                    _currentUser.value = userEntity
                    _uiState.value = AuthUiState.Success(userEntity)
                    repository.saveUser(userEntity)
                    onComplete?.invoke(true)
                } else {
                    throw Exception("Utilisateur introuvable.")
                }
            } catch (e: Exception) {
                Log.w(tag, "Firebase auth sign in failed, trying local DB fallback: ${e.localizedMessage}")
                // Fallback to local Room Database authentication
                val localUser = repository.authenticateUser(email.trim(), password.trim())
                if (localUser != null) {
                    _currentUser.value = localUser
                    _uiState.value = AuthUiState.Success(localUser)
                    onComplete?.invoke(true)
                } else {
                    val errorMsg = e.localizedMessage ?: "Échec de la connexion. Vérifiez vos identifiants."
                    _errorMessage.value = errorMsg
                    _uiState.value = AuthUiState.Error(errorMsg)
                    onComplete?.invoke(false)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sign Up with Email & Password using Firebase Auth (and save to local DB)
     */
    fun signUpWithEmail(
        displayName: String,
        email: String,
        password: String,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            _errorMessage.value = "Veuillez remplir tous les champs d'inscription."
            _uiState.value = AuthUiState.Error("Veuillez remplir tous les champs.")
            onComplete?.invoke(false)
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "Le mot de passe doit contenir au moins 6 caractères."
            _uiState.value = AuthUiState.Error("Mot de passe trop court.")
            onComplete?.invoke(false)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = AuthUiState.Loading
            _errorMessage.value = null

            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
                val fbUser = authResult.user

                if (fbUser != null) {
                    // Update Firebase profile display name
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.trim())
                        .build()
                    fbUser.updateProfile(profileUpdates).await()

                    val userEntity = UserEntity(
                        id = fbUser.uid,
                        email = fbUser.email ?: email.trim(),
                        displayName = displayName.trim(),
                        passwordHash = "",
                        isAdmin = email.trim().lowercase() == "admin@africaclick.ai" || email.trim().lowercase() == "chouamenikebel@gmail.com"
                    )
                    repository.saveUser(userEntity)
                    _currentUser.value = userEntity
                    _uiState.value = AuthUiState.Success(userEntity)
                    onComplete?.invoke(true)
                } else {
                    throw Exception("Création de compte échouée.")
                }
            } catch (e: Exception) {
                Log.w(tag, "Firebase auth signup failed, using local DB register: ${e.localizedMessage}")
                val newUser = repository.registerUser(
                    email = email.trim(),
                    displayName = displayName.trim(),
                    passwordHash = password.trim(),
                    isAdmin = email.trim().lowercase() == "admin@africaclick.ai"
                )
                _currentUser.value = newUser
                _uiState.value = AuthUiState.Success(newUser)
                onComplete?.invoke(true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Send Password Reset Email
     */
    fun sendPasswordReset(email: String, onComplete: (Boolean, String) -> Unit) {
        if (email.isBlank()) {
            onComplete(false, "Veuillez entrer une adresse e-mail valide.")
            return
        }

        viewModelScope.launch {
            try {
                firebaseAuth.sendPasswordResetEmail(email.trim()).await()
                onComplete(true, "Un lien de réinitialisation a été envoyé à $email")
            } catch (e: Exception) {
                Log.e(tag, "Reset password error", e)
                onComplete(false, e.localizedMessage ?: "Impossible d'envoyer le lien de réinitialisation.")
            }
        }
    }

    /**
     * Google Sign In flow with Credential Manager
     */
    fun signInWithGoogle(context: Context, webClientId: String = "", onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = AuthUiState.Loading
            _errorMessage.value = null

            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(if (webClientId.isNotBlank()) webClientId else "dummy-client-id")
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                if (credential is GoogleIdTokenCredential) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                    val fbUser = authResult.user

                    if (fbUser != null) {
                        val userEntity = UserEntity(
                            id = fbUser.uid,
                            email = fbUser.email ?: "",
                            displayName = fbUser.displayName ?: "Utilisateur Google",
                            passwordHash = "",
                            isAdmin = fbUser.email?.lowercase() == "admin@africaclick.ai"
                        )
                        repository.saveUser(userEntity)
                        _currentUser.value = userEntity
                        _uiState.value = AuthUiState.Success(userEntity)
                        onComplete?.invoke(true)
                        return@launch
                    }
                }
                throw Exception("Authentification Google annulée ou échouée.")
            } catch (e: Exception) {
                Log.w(tag, "Google Sign-In Credential Manager: ${e.localizedMessage}")
                val errorMsg = e.localizedMessage ?: "Échec de la connexion Google."
                _errorMessage.value = errorMsg
                _uiState.value = AuthUiState.Error(errorMsg)
                onComplete?.invoke(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sign out user from Firebase Auth & Clear local state
     */
    fun signOut() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            Log.w(tag, "Error signing out from Firebase: ${e.localizedMessage}")
        }
        _currentUser.value = null
        _uiState.value = AuthUiState.Idle
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
