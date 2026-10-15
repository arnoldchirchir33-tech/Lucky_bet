package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.UserAccountEntity
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    currentUser: UserAccountEntity,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Deposit Funds",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuckySurfaceDark)
            )
        },
        containerColor = LuckyBgDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .testTag("deposit_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Tile
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT BALANCE",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyTextSecondary
                            )
                            Text(
                                text = "KES ${String.format(Locale.US, "%,.2f", currentUser.balance)}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = LuckyGreenBright,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Surface(
                            color = LuckyGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = currentUser.phoneNumber,
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyGreenBright,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Direct Instructions & Paybill Info
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = LuckyGoldBright
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HOW TO DEPOSIT (CASHIER / ADMIN)",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "To fund your LuckyBet account, your account is credited directly by the Administrator or Cashier.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LuckyTextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = LuckySurfaceElevated,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "1. Your Account Number / Phone:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyTextSecondary
                                )
                                Text(
                                    text = currentUser.phoneNumber,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "2. Paybill Number (M-Pesa):",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyTextSecondary
                                )
                                Text(
                                    text = "290290 (LuckyBet Kenya)",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = LuckyGreenBright,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "3. Instant Admin Credit:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyTextSecondary
                                )
                                Text(
                                    text = "The Admin funds registered accounts from luckybet.com/admin_panel.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LuckyGoldBright
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "RETURN TO SPORTSBOOK & AVIATOR",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF042F2E),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawalScreen(
    currentUser: UserAccountEntity,
    onRequestWithdrawal: (String, Double) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var withdrawAmountInput by remember { mutableStateOf("500") }
    val quickAmounts = listOf(100, 250, 500, 1000, 2000, 5000)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Withdraw Funds",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuckySurfaceDark)
            )
        },
        containerColor = LuckyBgDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .testTag("withdrawal_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AVAILABLE FOR WITHDRAWAL",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuckyTextSecondary
                        )
                        Text(
                            text = "KES ${String.format(Locale.US, "%,.2f", currentUser.balance)}",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Payout Destination: M-Pesa ${currentUser.phoneNumber}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LuckyGreenBright
                        )
                    }
                }
            }

            // Withdrawal Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ENTER WITHDRAWAL AMOUNT",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = withdrawAmountInput,
                            onValueChange = { withdrawAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Amount (KES) • Min KES 50") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("standalone_withdraw_amount_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickAmounts.forEach { amt ->
                                Surface(
                                    color = LuckySurfaceElevated,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { withdrawAmountInput = amt.toString() }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "$amt",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = LuckyTextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = withdrawAmountInput.toDoubleOrNull() ?: 0.0
                                onRequestWithdrawal(currentUser.phoneNumber, amt)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("standalone_withdraw_submit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = Color(0xFF1E1B18)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WITHDRAW TO M-PESA (KES ${withdrawAmountInput.ifBlank { "0" }})",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF1E1B18),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
