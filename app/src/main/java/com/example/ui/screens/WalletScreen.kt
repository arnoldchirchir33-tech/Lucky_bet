package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.UserAccountEntity
import com.example.data.WalletTransactionEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dedicated WalletScreen displaying:
 * 1. Current user balance & account details
 * 2. Withdrawal request form with input validation and quick amount selectors
 * 3. Filterable Deposit History list (and overall transaction history)
 */
@Composable
fun WalletScreen(
    currentUser: UserAccountEntity,
    transactions: List<WalletTransactionEntity>,
    onWithdraw: (String, Double) -> Unit,
    onLogoutUser: () -> Unit,
    onBackToSports: () -> Unit
) {
    BackHandler { onBackToSports() }

    var withdrawAmountInput by remember { mutableStateOf("500") }
    var selectedHistoryFilter by remember { mutableStateOf("DEPOSIT") } // "DEPOSIT", "ALL", "WITHDRAWAL"
    var withdrawError by remember { mutableStateOf<String?>(null) }
    val quickAmounts = listOf(100, 250, 500, 1000, 2500, 5000)
    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.US) }

    val depositHistory = remember(transactions) {
        transactions.filter {
            it.type == "DEPOSIT" || it.type == "ADMIN_FUND"
        }
    }

    val displayedTransactions = remember(transactions, selectedHistoryFilter) {
        when (selectedHistoryFilter) {
            "DEPOSIT" -> transactions.filter { it.type == "DEPOSIT" || it.type == "ADMIN_FUND" }
            "WITHDRAWAL" -> transactions.filter { it.type == "WITHDRAWAL" }
            else -> transactions
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LuckyBgDark)
            .padding(14.dp)
            .testTag("wallet_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. USER PROFILE & BALANCE CARD
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, LuckyGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = LuckyGreen.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = LuckyGreenBright,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentUser.fullName.uppercase(Locale.ROOT),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "M-Pesa: ${currentUser.phoneNumber}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LuckyGreenBright
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onLogoutUser,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("user_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = LuckyCrimsonLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Logout", color = LuckyCrimsonLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = LuckyBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CURRENT WALLET BALANCE",
                        style = MaterialTheme.typography.labelMedium,
                        color = LuckyTextSecondary
                    )
                    Text(
                        text = "KES ${String.format(Locale.US, "%,.2f", currentUser.balance)}",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.testTag("wallet_current_balance_text")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = LuckySurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = LuckyGoldBright,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Account funded by Admin or Paybill 290290. All credited funds reflect instantly below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LuckyTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 2. WITHDRAWAL REQUEST FORM
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.8f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("withdrawal_request_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = LuckyGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = LuckyGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "WITHDRAWAL REQUEST FORM",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyGoldBright,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = withdrawAmountInput,
                        onValueChange = {
                            withdrawAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' }
                            withdrawError = null
                        },
                        label = { Text("Amount to Withdraw (KES)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = LuckyGoldBright
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuckyGoldBright,
                            unfocusedBorderColor = LuckyBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mpesa_amount_input")
                    )

                    if (currentUser.balance < 1000.0) {
                        Surface(
                            color = LuckyCrimson.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyCrimsonLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Notice: Please deposit KES 500 since minimum withdrawal is KES 1,000! (Current Balance: KES ${String.format(Locale.US, "%,.2f", currentUser.balance)})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    } else {
                        Surface(
                            color = LuckyGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGoldBright),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Account reached KES 1,000! Please add KES 300 for transaction fee to process M-Pesa withdrawal.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (withdrawError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = withdrawError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = LuckyCrimsonLight
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Quick Select Amount:",
                        style = MaterialTheme.typography.labelSmall,
                        color = LuckyTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickAmounts.forEach { amt ->
                            Surface(
                                color = if (withdrawAmountInput == amt.toString()) LuckyGold else LuckySurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        withdrawAmountInput = amt.toString()
                                        withdrawError = null
                                    }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "$amt",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (withdrawAmountInput == amt.toString()) Color(0xFF1E1B18) else LuckyTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val amt = withdrawAmountInput.toDoubleOrNull() ?: 0.0
                            if (amt < 50.0) {
                                withdrawError = "Minimum withdrawal is KES 50.00"
                            } else if (amt > currentUser.balance) {
                                withdrawError = "Insufficient balance! Available: KES ${String.format(Locale.US, "%,.2f", currentUser.balance)}"
                            } else {
                                onWithdraw(currentUser.phoneNumber, amt)
                                withdrawError = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuckyGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("mpesa_submit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SendToMobile,
                            contentDescription = null,
                            tint = Color(0xFF1E1B18)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUBMIT WITHDRAWAL (KES ${withdrawAmountInput.ifBlank { "0" }})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1E1B18),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 3. DEPOSIT HISTORY & TRANSACTION LIST HEADER WITH FILTERS
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTION HISTORY",
                        style = MaterialTheme.typography.titleMedium,
                        color = LuckyTextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = LuckySurfaceElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${depositHistory.size} Deposits",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuckyGreenBright,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips (Deposit History, All, Withdrawals)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedHistoryFilter == "DEPOSIT",
                        onClick = { selectedHistoryFilter = "DEPOSIT" },
                        label = { Text("Deposit History (${depositHistory.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LuckyGreen,
                            selectedLabelColor = Color(0xFF042F2E)
                        )
                    )

                    FilterChip(
                        selected = selectedHistoryFilter == "ALL",
                        onClick = { selectedHistoryFilter = "ALL" },
                        label = { Text("All (${transactions.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LuckyGreen,
                            selectedLabelColor = Color(0xFF042F2E)
                        )
                    )

                    FilterChip(
                        selected = selectedHistoryFilter == "WITHDRAWAL",
                        onClick = { selectedHistoryFilter = "WITHDRAWAL" },
                        label = { Text("Withdrawals") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LuckyGold,
                            selectedLabelColor = Color(0xFF1E1B18)
                        )
                    )
                }
            }
        }

        // 4. DEPOSIT HISTORY / TRANSACTION LIST ITEMS
        if (displayedTransactions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = LuckyTextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedHistoryFilter == "DEPOSIT") "No Deposit Records Found" else "No Transactions Found",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary
                        )
                        Text(
                            text = "Deposits made by cashier or M-Pesa reflect in this list automatically.",
                            style = MaterialTheme.typography.bodySmall,
                            color = LuckyTextSecondary
                        )
                    }
                }
            }
        } else {
            items(displayedTransactions, key = { it.id }) { tx ->
                val isDeposit = tx.type == "DEPOSIT" || tx.type == "ADMIN_FUND"
                val isWin = tx.type == "BET_WIN"
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDeposit) LuckyGreen.copy(alpha = 0.5f) else LuckyBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_item_${tx.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = when {
                                    isDeposit -> LuckyGreen.copy(alpha = 0.2f)
                                    isWin -> LuckyGold.copy(alpha = 0.2f)
                                    else -> LuckyCrimson.copy(alpha = 0.2f)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when {
                                            isDeposit -> Icons.Default.ArrowDownward
                                            isWin -> Icons.Default.EmojiEvents
                                            else -> Icons.Default.ArrowUpward
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            isDeposit -> LuckyGreenBright
                                            isWin -> LuckyGoldBright
                                            else -> LuckyCrimsonLight
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = when (tx.type) {
                                        "DEPOSIT", "ADMIN_FUND" -> "M-Pesa Deposit"
                                        "WITHDRAWAL" -> "M-Pesa Withdrawal"
                                        "BET_WIN" -> "Bet Win Payout"
                                        "BET_STAKE" -> "Bet Stake Placed"
                                        else -> tx.type
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LuckyTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ref: ${tx.mpesaCode} • ${dateFormatter.format(Date(tx.timestamp))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuckyTextSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = (if (tx.amount >= 0) "+KES " else "-KES ") +
                                    String.format(Locale.US, "%,.2f", kotlin.math.abs(tx.amount)),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (tx.amount >= 0) LuckyGreenBright else LuckyCrimsonLight,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                color = when (tx.status) {
                                    "COMPLETED" -> LuckyGreen.copy(alpha = 0.15f)
                                    "PENDING" -> LuckyGold.copy(alpha = 0.15f)
                                    else -> LuckyCrimson.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = tx.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (tx.status) {
                                        "COMPLETED" -> LuckyGreenBright
                                        "PENDING" -> LuckyGoldBright
                                        else -> LuckyCrimsonLight
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
