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

private val SignUpBgTop = Color(0xFFF6F1EB)
private val SignUpBgMid = Color(0xFFEDE4DA)
private val SignUpBgBottom = Color(0xFFD6BFA7)
private val SignUpPrimary = Color(0xFFD6BFA7)
private val SignUpSecondary = Color(0xFFE0D5C8)
private val SignUpAccent = Color(0xFFB08968)
private val SignUpTextPrimary = Color(0xFF3B2A1F)
private val SignUpTextSecondary = Color(0xFF7B6A58)
private val SignUpDivider = Color(0xFFE0D5C8)

@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String) -> Unit = { _, _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SignUpBgTop, SignUpBgMid, SignUpBgBottom)
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
            // Create Account Title
            Text(
                text = "Create Account",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = SignUpTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Sign up to get started",
                fontSize = 18.sp,
                color = SignUpTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Name TextField
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", color = SignUpTextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SignUpAccent,
                    unfocusedBorderColor = SignUpSecondary,
                    focusedTextColor = SignUpTextPrimary,
                    unfocusedTextColor = SignUpTextPrimary,
                    cursorColor = SignUpAccent,
                    focusedLabelColor = SignUpAccent,
                    unfocusedLabelColor = SignUpTextSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email TextField
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = SignUpTextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SignUpAccent,
                    unfocusedBorderColor = SignUpSecondary,
                    focusedTextColor = SignUpTextPrimary,
                    unfocusedTextColor = SignUpTextPrimary,
                    cursorColor = SignUpAccent,
                    focusedLabelColor = SignUpAccent,
                    unfocusedLabelColor = SignUpTextSecondary
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
                label = { Text("Password", color = SignUpTextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SignUpAccent,
                    unfocusedBorderColor = SignUpSecondary,
                    focusedTextColor = SignUpTextPrimary,
                    unfocusedTextColor = SignUpTextPrimary,
                    cursorColor = SignUpAccent,
                    focusedLabelColor = SignUpAccent,
                    unfocusedLabelColor = SignUpTextSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = "Show",
                            color = SignUpAccent,
                            fontSize = 14.sp
                        )
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Button
            Button(
                onClick = { onSignUpClick(name, email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SignUpPrimary,
                    contentColor = SignUpTextPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SignUpTextPrimary
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
                    color = SignUpDivider,
                    thickness = 1.dp
                )
                Text(
                    text = " OR ",
                    color = SignUpTextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = SignUpDivider,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Text
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = SignUpTextSecondary,
                    fontSize = 14.sp
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Login",
                        color = SignUpAccent,
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
fun SignUpScreenPreview() {
    Git_repo_4Theme {
        SignUpScreen()
    }
}
