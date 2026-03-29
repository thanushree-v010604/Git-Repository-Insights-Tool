package com.example.git_repo_4

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.git_repo_4.ui.theme.Git_repo_4Theme

private val LoginBgTop = Color(0xFF0A0F14)
private val LoginBgMid = Color(0xFF0A0F14)
private val LoginBgBottom = Color(0xFF101820)
private val LoginPrimary = Color(0xFF00E5FF)
private val LoginSecondary = Color(0xFF1F2A33)
private val LoginAccent = Color(0xFF00F0FF)
private val LoginTextPrimary = Color(0xFFE6F7FF)
private val LoginTextSecondary = Color(0xFF7AA6B2)
private val LoginDivider = Color(0xFF1F2A33)

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LoginBgTop, LoginBgMid, LoginBgBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Welcome Back Title
            Text(
                text = "Welcome Back",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = LoginTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Login to your account",
                fontSize = 18.sp,
                color = LoginTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email TextField
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = LoginTextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LoginAccent,
                    unfocusedBorderColor = LoginSecondary,
                    focusedTextColor = LoginTextPrimary,
                    unfocusedTextColor = LoginTextPrimary,
                    cursorColor = LoginAccent,
                    focusedLabelColor = LoginAccent,
                    unfocusedLabelColor = LoginTextSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = LoginTextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LoginAccent,
                    unfocusedBorderColor = LoginSecondary,
                    focusedTextColor = LoginTextPrimary,
                    unfocusedTextColor = LoginTextPrimary,
                    cursorColor = LoginAccent,
                    focusedLabelColor = LoginAccent,
                    unfocusedLabelColor = LoginTextSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = "Show",
                            color = LoginAccent,
                            fontSize = 14.sp
                        )
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = "Forgot Password?",
                        color = LoginAccent,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginPrimary,
                    contentColor = LoginTextPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LoginTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // OR Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = LoginDivider,
                    thickness = 1.dp
                )
                Text(
                    text = " OR ",
                    color = LoginTextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = LoginDivider,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Text
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = LoginTextSecondary,
                    fontSize = 14.sp
                )
                TextButton(onClick = onSignUpClick) {
                    Text(
                        text = "Sign Up",
                        color = LoginAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    Git_repo_4Theme {
        LoginScreen()
    }
}
