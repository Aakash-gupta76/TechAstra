package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TechAstraLogoBadge
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TechAstraGold
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechCardBorder
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary

@Composable
fun SignupScreen(
    onSignupClick: (name: String, email: String, password: String, onError: (String) -> Unit) -> Unit,
    onGoogleSignupClick: (onError: (String) -> Unit) -> Unit,
    onNavigateBackToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var showGoogleHelpDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        var isValid = true

        if (fullName.isBlank()) {
            nameError = "Please enter your full name"
            isValid = false
        } else {
            nameError = null
        }

        if (email.isBlank()) {
            emailError = "Please enter your email"
            isValid = false
        } else if (!email.contains("@") || !email.contains(".")) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }

        if (confirmPassword != password) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        } else {
            confirmPasswordError = null
        }

        if (isValid) {
            isLoading = true
            generalError = null
            onSignupClick(fullName, email, password) { err ->
                isLoading = false
                generalError = err
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("signup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Bar with Back Button and Brand Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBackToLogin,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(TechSurfaceVariant)
                        .testTag("signup_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Login",
                        tint = TechTextPrimary
                    )
                }

                TechAstraLogoBadge(
                    size = 46.dp,
                    showRingGlow = false
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Create Account",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TechTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Join TechAstra and start your learning journey",
                fontSize = 14.sp,
                color = TechTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    if (nameError != null) nameError = null
                    generalError = null
                },
                label = { Text("Full Name") },
                placeholder = { Text("e.g. Aakash Gupta") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name",
                        tint = TechAstraGold
                    )
                },
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) Text(text = nameError ?: "", color = AccentRed)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_name_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechAstraGold,
                    unfocusedBorderColor = TechCardBorder,
                    focusedContainerColor = TechSurface,
                    unfocusedContainerColor = TechSurface,
                    focusedTextColor = TechTextPrimary,
                    unfocusedTextColor = TechTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                    generalError = null
                },
                label = { Text("Email address") },
                placeholder = { Text("e.g. aakash@gmail.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = TechAstraGold
                    )
                },
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) Text(text = emailError ?: "", color = AccentRed)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_email_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechAstraGold,
                    unfocusedBorderColor = TechCardBorder,
                    focusedContainerColor = TechSurface,
                    unfocusedContainerColor = TechSurface,
                    focusedTextColor = TechTextPrimary,
                    unfocusedTextColor = TechTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                    generalError = null
                },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = TechAstraGold
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = TechTextMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) Text(text = passwordError ?: "", color = AccentRed)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_password_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechAstraGold,
                    unfocusedBorderColor = TechCardBorder,
                    focusedContainerColor = TechSurface,
                    unfocusedContainerColor = TechSurface,
                    focusedTextColor = TechTextPrimary,
                    unfocusedTextColor = TechTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (confirmPasswordError != null) confirmPasswordError = null
                    generalError = null
                },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirm Password",
                        tint = TechAstraGold
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = TechTextMuted
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = confirmPasswordError != null,
                supportingText = {
                    if (confirmPasswordError != null) Text(text = confirmPasswordError ?: "", color = AccentRed)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_confirm_password_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechAstraGold,
                    unfocusedBorderColor = TechCardBorder,
                    focusedContainerColor = TechSurface,
                    unfocusedContainerColor = TechSurface,
                    focusedTextColor = TechTextPrimary,
                    unfocusedTextColor = TechTextPrimary
                )
            )

            if (generalError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = generalError ?: "", color = AccentRed, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Up Button
            Button(
                onClick = { validateAndSubmit() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("signup_submit_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechAstraGold,
                    contentColor = Color.Black
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Sign Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = TechCardBorder)
                Text(
                    text = "OR",
                    fontSize = 12.sp,
                    color = TechTextMuted,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = TechCardBorder)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google Signin
            OutlinedButton(
                onClick = {
                    if (!isGoogleLoading && !isLoading) {
                        isGoogleLoading = true
                        generalError = null
                        onGoogleSignupClick { err ->
                            isGoogleLoading = false
                            generalError = err
                        }
                    }
                },
                enabled = !isGoogleLoading && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("signup_google_button"),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = !isGoogleLoading && !isLoading).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(TechCardBorder)
                )
            ) {
                if (isGoogleLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TechAstraGold,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Google Sign-In connecting...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TechTextPrimary
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(TechSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = TechAstraGold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sign in with Google",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TechTextPrimary
                        )
                    }
                }
            }

            // Google Setup Info link
            TextButton(
                onClick = { showGoogleHelpDialog = true },
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "⚙️ Google Login problem? Check SHA-1 setup",
                    fontSize = 12.sp,
                    color = TechTextMuted
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = TechTextSecondary
                )
                Text(
                    text = "Login",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechAstraGold,
                    modifier = Modifier
                        .clickable { onNavigateBackToLogin() }
                        .padding(4.dp)
                        .testTag("signup_login_link")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Google Sign-In Setup & Info Dialog
    if (showGoogleHelpDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showGoogleHelpDialog = false },
            title = {
                Text(
                    text = "Google Sign-In Setup & Troubleshooting",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechTextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Agar 'Sign in with Google' kaam nahi kar raha hai to uski mukhya wajah ye hain:",
                        fontSize = 13.sp,
                        color = TechTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = TechSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "1. SHA-1 Fingerprint (MANDATORY):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechAstraGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Firebase Console me is app ka SHA-1 add karna zaroori hai:\nPackage: com.aistudio.techastra.bkpyqs\nSHA-1:\n9D:35:F2:9E:7F:EA:5E:02:7D:BE:E4:1F:D6:9C:C1:D8:06:D9:53:34",
                                fontSize = 11.sp,
                                color = TechTextPrimary,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = TechSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "2. Device me Google Account:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechAstraGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Emulator ya Phone ki Settings me kam se kam ek Google account logged in hona chahiye.",
                                fontSize = 12.sp,
                                color = TechTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = TechAstraGold.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechAstraGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "💡 Sabse Simple Tarika:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechAstraGold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Aap direct apna Name, Email & Password daal kar 'Create Account' karein. Email & Password 100% chalega bina kisi SHA-1 ke!",
                                fontSize = 12.sp,
                                color = TechTextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGoogleHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TechAstraGold)
                ) {
                    Text("Theek Hai", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = TechSurface
        )
    }
}
