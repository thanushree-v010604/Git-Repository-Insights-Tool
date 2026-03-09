package com.example.git_repo_4

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.git_repo_4.ui.theme.Git_repo_4Theme

private enum class AppScreen {
    SPLASH,
    LOGIN,
    SIGN_UP
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Git_repo_4Theme {
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                when (currentScreen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(onTimeout = { currentScreen = AppScreen.LOGIN })
                    }

                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginClick = { email, password ->
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    startActivity(Intent(this, HomeActivity::class.java))
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Please enter email and password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onSignUpClick = { currentScreen = AppScreen.SIGN_UP },
                            onForgotPasswordClick = {
                                Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }

                    AppScreen.SIGN_UP -> {
                        SignUpScreen(
                            onSignUpClick = { name, email, password ->
                                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                    startActivity(Intent(this, HomeActivity::class.java))
                                } else {
                                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            onLoginClick = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                }
            }
        }
    }
}
