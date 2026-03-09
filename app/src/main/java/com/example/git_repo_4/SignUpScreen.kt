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
                    colors = listOf(
                        Color(0xFF2D4A2B),
                        Color(0xFF1A2E19),
                        Color(0xFF0D1A0D)
                    )
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
                color = Color(0xFF6CBF47),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Sign up to get started",
                fontSize = 18.sp,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Name TextField
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", color = Color(0xFF9E9E9E)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6CBF47),
                    unfocusedBorderColor = Color(0xFF4A4A4A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF6CBF47),
                    focusedLabelColor = Color(0xFF6CBF47),
                    unfocusedLabelColor = Color(0xFF9E9E9E)
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email TextField
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color(0xFF9E9E9E)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6CBF47),
                    unfocusedBorderColor = Color(0xFF4A4A4A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF6CBF47),
                    focusedLabelColor = Color(0xFF6CBF47),
                    unfocusedLabelColor = Color(0xFF9E9E9E)
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
                label = { Text("Password", color = Color(0xFF9E9E9E)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6CBF47),
                    unfocusedBorderColor = Color(0xFF4A4A4A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF6CBF47),
                    focusedLabelColor = Color(0xFF6CBF47),
                    unfocusedLabelColor = Color(0xFF9E9E9E)
                ),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = "Show",
                            color = Color(0xFF6CBF47),
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
                    containerColor = Color(0xFF3D5A3B)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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
                    color = Color(0xFF4A4A4A),
                    thickness = 1.dp
                )
                Text(
                    text = " OR ",
                    color = Color(0xFF9E9E9E),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF4A4A4A),
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
                    color = Color(0xFF9E9E9E),
                    fontSize = 14.sp
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Login",
                        color = Color(0xFF6CBF47),
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

