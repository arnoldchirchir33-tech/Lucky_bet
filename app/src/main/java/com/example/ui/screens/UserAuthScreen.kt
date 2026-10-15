package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.*

enum class AuthScreenMode {
    LOGIN,
    REGISTER,
    VERIFY_CODE
}

@Composable
fun UserAuthScreen(
    authError: String?,
    onClearError: () -> Unit,
    onLogin: (phone: String, password: String) -> Unit,
    onRegister: (phone: String, fullName: String, password: String) -> Unit,
    onVerifyCode: (phone: String, code: String) -> Unit = { _, _ -> }
) {
    var mode by remember { mutableStateOf(AuthScreenMode.LOGIN) }
    var phone by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuckyBgDark)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
            .testTag("user_auth_screen"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, LuckyGreen.copy(alpha = 0.65f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_aviator_1791620877833),
                        contentDescription = "LuckyBet Welcome",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x550A0F1D),
                                        Color(0xEE131C31)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = LuckyCrimson,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "LUCKYBET.COM",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SPORTSBOOK & AVIATOR",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (mode) {
                                AuthScreenMode.LOGIN -> "LOGIN TO LUCKYBET"
                                AuthScreenMode.REGISTER -> "CREATE ACCOUNT"
                                AuthScreenMode.VERIFY_CODE -> "VERIFY YOUR ACCOUNT"
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Switcher Tabs (Login vs Register vs Verify Code)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LuckySurfaceElevated)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = if (mode == AuthScreenMode.LOGIN) LuckyGreen else Color.Transparent,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    mode = AuthScreenMode.LOGIN
                                    localError = null
                                    onClearError()
                                }
                                .testTag("auth_tab_login")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "LOGIN",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (mode == AuthScreenMode.LOGIN) Color(0xFF042F2E) else LuckyTextPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Surface(
                            color = if (mode == AuthScreenMode.REGISTER) LuckyGreen else Color.Transparent,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    mode = AuthScreenMode.REGISTER
                                    localError = null
                                    onClearError()
                                }
                                .testTag("auth_tab_register")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "REGISTER",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (mode == AuthScreenMode.REGISTER) Color(0xFF042F2E) else LuckyTextPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Surface(
                            color = if (mode == AuthScreenMode.VERIFY_CODE) LuckyGold else Color.Transparent,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .clickable {
                                    mode = AuthScreenMode.VERIFY_CODE
                                    localError = null
                                    onClearError()
                                }
                                .testTag("auth_tab_verify")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "VERIFY CODE",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (mode == AuthScreenMode.VERIFY_CODE) Color(0xFF1E1B18) else LuckyTextPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone input field for all modes
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            localError = null
                            onClearError()
                        },
                        label = { Text("Phone Number (e.g. 0712345678)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = LuckyGreenBright
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_phone_input")
                    )

                    // REGISTER SPECIFIC FIELDS (Strictly NO account or wallet rules displayed)
                    if (mode == AuthScreenMode.REGISTER) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                localError = null
                                onClearError()
                            },
                            label = { Text("Full Name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = LuckyGreenBright
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_fullname_input")
                        )
                    }

                    // PASSWORD FIELDS (Login & Register)
                    if (mode == AuthScreenMode.LOGIN || mode == AuthScreenMode.REGISTER) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                localError = null
                                onClearError()
                            },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = LuckyGreenBright
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )
                    }

                    if (mode == AuthScreenMode.REGISTER) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                localError = null
                                onClearError()
                            },
                            label = { Text("Confirm Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = LuckyGreenBright
                                )
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_confirm_password_input")
                        )
                    }

                    // VERIFY CODE SPECIFIC FIELDS
                    if (mode == AuthScreenMode.VERIFY_CODE) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = verificationCode,
                            onValueChange = {
                                verificationCode = it.filter { ch -> ch.isDigit() }.take(6)
                                localError = null
                                onClearError()
                            },
                            label = { Text("6-Digit Verification Code") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = LuckyGoldBright
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_verification_code_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = LuckyGold.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = LuckyGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Your verification code is generated on the Admin Panel (luckybet.com/admin_login). Enter your 6-digit code above to verify.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuckyGoldBright
                                )
                            }
                        }
                    }

                    val displayedError = localError ?: authError
                    if (displayedError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = if (displayedError.contains("successful", ignoreCase = true) || displayedError.contains("verified", ignoreCase = true)) {
                                LuckyGreen.copy(alpha = 0.2f)
                            } else {
                                LuckyCrimson.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = displayedError,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (displayedError.contains("successful", ignoreCase = true) || displayedError.contains("verified", ignoreCase = true)) {
                                    LuckyGreenBright
                                } else {
                                    LuckyCrimsonLight
                                },
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            when (mode) {
                                AuthScreenMode.REGISTER -> {
                                    if (phone.isBlank() || fullName.isBlank() || password.isBlank()) {
                                        localError = "Please fill in all registration fields."
                                    } else if (password != confirmPassword) {
                                        localError = "Passwords do not match!"
                                    } else {
                                        onRegister(phone, fullName, password)
                                        mode = AuthScreenMode.VERIFY_CODE
                                    }
                                }
                                AuthScreenMode.LOGIN -> {
                                    if (phone.isBlank() || password.isBlank()) {
                                        localError = "Please enter phone number and password."
                                    } else {
                                        onLogin(phone, password)
                                    }
                                }
                                AuthScreenMode.VERIFY_CODE -> {
                                    if (phone.isBlank() || verificationCode.isBlank()) {
                                        localError = "Please enter both phone number and verification code."
                                    } else {
                                        onVerifyCode(phone, verificationCode)
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mode == AuthScreenMode.VERIFY_CODE) LuckyGold else LuckyGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_submit_button")
                    ) {
                        Icon(
                            imageVector = when (mode) {
                                AuthScreenMode.REGISTER -> Icons.Default.PersonAdd
                                AuthScreenMode.LOGIN -> Icons.Default.Login
                                AuthScreenMode.VERIFY_CODE -> Icons.Default.Verified
                            },
                            contentDescription = null,
                            tint = if (mode == AuthScreenMode.VERIFY_CODE) Color(0xFF1E1B18) else Color(0xFF042F2E)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (mode) {
                                AuthScreenMode.REGISTER -> "REGISTER ACCOUNT"
                                AuthScreenMode.LOGIN -> "LOGIN TO LUCKYBET"
                                AuthScreenMode.VERIFY_CODE -> "VERIFY & ENTER ACCOUNT"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = if (mode == AuthScreenMode.VERIFY_CODE) Color(0xFF1E1B18) else Color(0xFF042F2E),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (mode != AuthScreenMode.LOGIN) {
                            TextButton(
                                onClick = {
                                    mode = AuthScreenMode.LOGIN
                                    localError = null
                                    onClearError()
                                }
                            ) {
                                Text("Back to Login", color = LuckyTextSecondary)
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    mode = AuthScreenMode.REGISTER
                                    localError = null
                                    onClearError()
                                }
                            ) {
                                Text("New user? Register", color = LuckyGoldBright)
                            }
                        }

                        if (mode != AuthScreenMode.VERIFY_CODE) {
                            TextButton(
                                onClick = {
                                    mode = AuthScreenMode.VERIFY_CODE
                                    localError = null
                                    onClearError()
                                }
                            ) {
                                Text("Enter Code", color = LuckyGoldBright)
                            }
                        }
                    }
                }
            }
        }
    }
}
