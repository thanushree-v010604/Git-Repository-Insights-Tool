package com.example.git_repo_4

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.git_repo_4.ui.theme.Git_repo_4Theme
import com.example.git_repo_4.utils.SessionManager
import com.example.git_repo_4.utils.ValidationUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private enum class AppScreen {
    SPLASH,
    LOGIN,
    SIGN_UP
}

class MainActivity : ComponentActivity() {
    private var auth: FirebaseAuth? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleSignInResult(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sessionManager = SessionManager(this)

        val firebaseReady = FirebaseInitializer.initialize(applicationContext)
        if (firebaseReady) {
            auth = FirebaseAuth.getInstance()
            configureGoogleSignIn()
        } else {
            Toast.makeText(
                this,
                "Firebase is not configured. Verify app/google-services.json.",
                Toast.LENGTH_LONG
            ).show()
        }

        setContent {
            Git_repo_4Theme {
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                when (currentScreen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onTimeout = {
                                val currentUser = auth?.currentUser
                                if (currentUser != null) {
                                    sessionManager.saveLogin(currentUser.email)
                                    navigateToHome()
                                } else {
                                    sessionManager.logout()
                                    currentScreen = AppScreen.LOGIN
                                }
                            }
                        )
                    }

                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginClick = { email, password ->
                                performLogin(email, password)
                            },
                            onGoogleSignInClick = {
                                startGoogleSignIn()
                            },
                            onSignUpClick = { currentScreen = AppScreen.SIGN_UP },
                            onForgotPasswordClick = {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Forgot Password clicked",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    AppScreen.SIGN_UP -> {
                        SignUpScreen(
                            onSignUpClick = { name, email, password ->
                                performSignUp(name, email, password) { currentScreen = AppScreen.LOGIN }
                            },
                            onLoginClick = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                }
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            Toast.makeText(this, "Firebase is not configured", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate inputs
        val emailError = ValidationUtils.validateEmail(email)
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show()
            return
        }

        val passwordError = ValidationUtils.validatePassword(password)
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show()
            return
        }

        // Authenticate with Firebase
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sessionManager.saveLogin(email)
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    // Login failed
                    val errorMessage = when {
                        task.exception?.message?.contains("user not found", ignoreCase = true) == true -> {
                            "User not found. Please sign up first."
                        }
                        task.exception?.message?.contains("password is invalid", ignoreCase = true) == true -> {
                            "Invalid password"
                        }
                        else -> {
                            task.exception?.message ?: "Authentication failed"
                        }
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun performSignUp(
        name: String,
        email: String,
        password: String,
        navigateToLogin: () -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            Toast.makeText(this, "Firebase is not configured", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate inputs
        val nameError = ValidationUtils.validateName(name)
        if (nameError != null) {
            Toast.makeText(this, nameError, Toast.LENGTH_SHORT).show()
            return
        }

        val emailError = ValidationUtils.validateEmail(email)
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show()
            return
        }

        val passwordError = ValidationUtils.validatePassword(password)
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show()
            return
        }

        // Create user with Firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Signup success
                    Toast.makeText(
                        this,
                        "Account created successfully. Please log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToLogin()
                } else {
                    // Signup failed
                    val errorMessage = when {
                        task.exception?.message?.contains("email already", ignoreCase = true) == true -> {
                            "Email is already registered"
                        }
                        else -> {
                            task.exception?.message ?: "Registration failed"
                        }
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun startGoogleSignIn() {
        if (auth == null) {
            Toast.makeText(this, "Firebase is not configured", Toast.LENGTH_SHORT).show()
            return
        }

        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken.isNullOrBlank()) {
                Toast.makeText(this, "Google sign-in failed: missing ID token", Toast.LENGTH_SHORT).show()
                return
            }

            firebaseAuthWithGoogle(idToken)
        } catch (e: ApiException) {
            Toast.makeText(this, "Google sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            Toast.makeText(this, "Firebase is not configured", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val email = firebaseAuth.currentUser?.email
                    sessionManager.saveLogin(email)
                    Toast.makeText(this, "Google sign-in successful", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Toast.makeText(
                        this,
                        task.exception?.message ?: "Google authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
