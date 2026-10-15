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

    var depositInput by remember { mutableStateOf("10") }
    var depositStatusMsg by remember { mutableStateOf<String?>(null) }
    val quickDepositChips = listOf(10, 50, 100, 250, 500, 1000)

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
            // Balance Tile with Bonus notice
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
                            color = LuckyGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "KES 500 BONUS",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyGoldBright,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Minimum 10 Shillings Notice Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGreenBright)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = LuckyGreenBright,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MINIMUM DEPOSIT: 10 SHILLINGS",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyGreenBright,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "You can pay as little as 10 shillings to top up your LuckyBet account via M-Pesa.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LuckyTextSecondary
                            )
                        }
                    }
                }
            }

            // Deposit Amount Selector & Instructions
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SELECT OR ENTER DEPOSIT AMOUNT",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = depositInput,
                            onValueChange = {
                                depositInput = it.filter { ch -> ch.isDigit() || ch == '.' }
                                depositStatusMsg = null
                            },
                            label = { Text("Amount (Minimum 10 Shillings)") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickDepositChips.forEach { amt ->
                                Surface(
                                    color = if (depositInput == amt.toString()) LuckyGreen else LuckySurfaceElevated,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            depositInput = amt.toString()
                                            depositStatusMsg = null
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "$amt",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (depositInput == amt.toString()) Color(0xFF042F2E) else LuckyTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        if (depositStatusMsg != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = LuckyGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = depositStatusMsg!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuckyGoldBright,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val amt = depositInput.toDoubleOrNull() ?: 0.0
                                if (amt < 10.0) {
                                    depositStatusMsg = "Minimum deposit is 10 shillings! Please enter at least 10."
                                } else {
                                    depositStatusMsg = "Prompt sent to ${currentUser.phoneNumber} for KES ${String.format(Locale.US, "%,.0f", amt)}. Alternatively, use Paybill 290290 (Account: ${currentUser.phoneNumber})."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCard,
                                contentDescription = null,
                                tint = Color(0xFF042F2E)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PAY VIA M-PESA (KES ${depositInput.ifBlank { "0" }})",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF042F2E),
                                fontWeight = FontWeight.Black
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = LuckyGoldBright
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "M-PESA PAYBILL PAYMENT DETAILS",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = LuckySurfaceElevated,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Paybill Number:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyTextSecondary
                                )
                                Text(
                                    text = "290290 (LuckyBet Kenya)",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = LuckyGreenBright,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Account Number:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyTextSecondary
                                )
                                Text(
                                    text = currentUser.phoneNumber,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Minimum Amount:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyTextSecondary
                                )
                                Text(
                                    text = "KES 10.00 (Minimum 10 shillings)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LuckyGoldBright,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
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

    var withdrawAmountInput by remember { mutableStateOf("1000") }
    val quickAmounts = listOf(1000, 1500, 2000, 3000, 5000, 10000)
    val isBelow1000 = currentUser.balance < 1000.0

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
                            text = "Destination: M-Pesa ${currentUser.phoneNumber}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LuckyGreenBright
                        )
                    }
                }
            }

            // WITHDRAWAL RULES CONDITIONAL NOTICES
            item {
                if (isBelow1000) {
                    // Under 1,000 KES Rule: Must deposit 500 since minimum withdrawal is 1000
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LuckyCrimson.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, LuckyCrimsonLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = LuckyCrimsonLight
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "WITHDRAWAL REQUIREMENT",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LuckyCrimsonLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Please deposit KES 500 since minimum withdrawal is KES 1,000! Your current balance is KES ${String.format(Locale.US, "%,.2f", currentUser.balance)} (including your KES 500 welcome bonus).",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // 1,000+ KES Rule: Prompt to add 300 for transaction fee
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LuckyGold.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, LuckyGoldBright)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = LuckyGoldBright
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TRANSACTION FEE NOTICE",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LuckyGoldBright,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your account has reached KES 1,000! Please add KES 300 for transaction fee to disburse your M-Pesa withdrawal.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
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
                            label = { Text("Amount (KES) • Minimum KES 1,000") },
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
                                text = "REQUEST WITHDRAWAL (KES ${withdrawAmountInput.ifBlank { "0" }})",
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
