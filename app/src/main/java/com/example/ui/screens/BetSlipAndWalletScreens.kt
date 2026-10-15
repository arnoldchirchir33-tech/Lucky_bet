package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.BetSelection
import com.example.data.BetTicketEntity
import com.example.data.UserAccountEntity
import com.example.data.WalletTransactionEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BetSlipScreen(
    selections: List<BetSelection>,
    stakeInput: String,
    userBalance: Double,
    onStakeChange: (String) -> Unit,
    onRemoveSelection: (Int) -> Unit,
    onClearSlip: () -> Unit,
    onPlaceBet: () -> Unit,
    onGoToSports: () -> Unit
) {
    BackHandler { onGoToSports() }

    val stakeVal = stakeInput.toDoubleOrNull() ?: 0.0
    val combinedOdds = selections.fold(1.0) { acc, s -> acc * s.odd }
    val boostFactor = when {
        selections.size >= 5 -> 1.15
        selections.size >= 3 -> 1.08
        else -> 1.0
    }
    val grossPayout = stakeVal * combinedOdds * boostFactor
    val quickStakes = listOf(50, 100, 250, 500, 1000, 2500)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LuckyBgDark)
            .padding(14.dp)
            .testTag("betslip_screen")
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "Bet Slip",
                    tint = LuckyGreenBright
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LUCKYBET SLIP (${selections.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = LuckyTextPrimary
                )
            }
            if (selections.isNotEmpty()) {
                TextButton(
                    onClick = onClearSlip,
                    modifier = Modifier.testTag("clear_slip_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear",
                        tint = LuckyCrimsonLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All", color = LuckyCrimsonLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selections.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Empty Bet Slip",
                        tint = LuckyTextSecondary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your Bet Slip is Empty",
                        style = MaterialTheme.typography.titleLarge,
                        color = LuckyTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap any 1X2, Over/Under, or GG odds on luckybet.com to build a multi-bet and unlock up to 15% Win Boost!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuckyTextSecondary
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onGoToSports,
                        colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "BROWSE LIVE MATCHES",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF042F2E)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selections, key = { it.matchId }) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.matchTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LuckyTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.league,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LuckyTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pick: ${item.marketName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyGreenBright
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = LuckySurfaceElevated,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.2f", item.odd),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = LuckyGoldBright,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                                IconButton(onClick = { onRemoveSelection(item.matchId) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove selection",
                                        tint = LuckyTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Summary & Stake Panel
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGreen.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Combined Odds", style = MaterialTheme.typography.bodyMedium, color = LuckyTextSecondary)
                        Text(
                            text = String.format(Locale.US, "%.2fx", combinedOdds),
                            style = MaterialTheme.typography.labelLarge,
                            color = LuckyGoldBright
                        )
                    }

                    if (boostFactor > 1.0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Multi-Bet Win Boost", style = MaterialTheme.typography.bodyMedium, color = LuckyGreenBright)
                            Text(
                                text = "+${((boostFactor - 1.0) * 100).toInt()}% BONUS",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyGreenBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = stakeInput,
                        onValueChange = onStakeChange,
                        label = { Text("Enter Stake (KES) • Balance: KES ${String.format(Locale.US, "%,.2f", userBalance)}") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuckyGreen,
                            unfocusedBorderColor = LuckyBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("betslip_stake_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickStakes.forEach { q ->
                            Surface(
                                color = LuckySurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onStakeChange(q.toString()) }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$q",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LuckyTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "POTENTIAL WIN",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "KES ${String.format(Locale.US, "%,.2f", grossPayout)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = LuckyGreenBright,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onPlaceBet,
                        colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("place_sports_bet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Place Bet",
                            tint = Color(0xFF042F2E)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLACE BET NOW (KES ${stakeInput.ifBlank { "0" }})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF042F2E),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyBetsScreen(
    bets: List<BetTicketEntity>,
    onCashoutTicket: (BetTicketEntity) -> Unit,
    onBackToSports: () -> Unit
) {
    BackHandler { onBackToSports() }

    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.US) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LuckyBgDark)
            .padding(14.dp)
            .testTag("my_bets_screen")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "My Bets",
                tint = LuckyGoldBright
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MY BETS & AVIATOR HISTORY (${bets.size})",
                style = MaterialTheme.typography.headlineMedium,
                color = LuckyTextPrimary
            )
        }

        if (bets.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HistoryToggleOff,
                        contentDescription = null,
                        tint = LuckyTextSecondary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No Active or Settled Bets Yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = LuckyTextPrimary
                    )
                    Text(
                        text = "Place a Sportsbook bet or cash out in Aviator to see your tickets here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuckyTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(bets, key = { it.id }) { ticket ->
                    val badgeColor = when (ticket.status) {
                        "WON", "CASHED_OUT" -> LuckyGreen
                        "LOST" -> LuckyCrimson
                        else -> LuckyGold
                    }
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${ticket.ticketCode}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = LuckyGoldBright
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dateFormatter.format(Date(ticket.placedAt)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LuckyTextSecondary
                                    )
                                }
                                Surface(
                                    color = badgeColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = ticket.status,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = badgeColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = ticket.summary,
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyTextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Stake", style = MaterialTheme.typography.bodyMedium, color = LuckyTextSecondary)
                                    Text(
                                        "KES ${String.format(Locale.US, "%,.0f", ticket.stake)}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = LuckyTextPrimary
                                    )
                                }
                                Column {
                                    Text("Odds", style = MaterialTheme.typography.bodyMedium, color = LuckyTextSecondary)
                                    Text(
                                        "${String.format(Locale.US, "%.2f", ticket.totalOdds)}x",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = LuckyGoldBright
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Payout", style = MaterialTheme.typography.bodyMedium, color = LuckyTextSecondary)
                                    Text(
                                        "KES ${String.format(Locale.US, "%,.2f", ticket.potentialPayout)}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = LuckyGreenBright
                                    )
                                }
                            }

                            if (ticket.status == "OPEN") {
                                val instantCashoutOffer = ticket.stake * 0.85
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { onCashoutTicket(ticket) },
                                    colors = ButtonDefaults.buttonColors(containerColor = LuckyGold),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("cashout_ticket_${ticket.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Instant Cashout",
                                        tint = Color(0xFF1E1B18)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "INSTANT CASHOUT KES ${String.format(Locale.US, "%,.2f", instantCashoutOffer)}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color(0xFF1E1B18)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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
    val quickAmounts = listOf(100, 250, 500, 1000, 2500, 5000)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LuckyBgDark)
            .padding(14.dp)
            .testTag("wallet_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // User Profile & Account Balance Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, LuckyGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentUser.fullName.uppercase(Locale.ROOT),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Registered Phone: ${currentUser.phoneNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuckyGreenBright
                            )
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
                        text = "AVAILABLE WALLET BALANCE",
                        style = MaterialTheme.typography.labelMedium,
                        color = LuckyTextSecondary
                    )
                    Text(
                        text = "KES ${String.format(Locale.US, "%,.2f", currentUser.balance)}",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = LuckySurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = LuckyGoldBright
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Account Funding via Paybill / Cashier",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LuckyGoldBright,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Provide your registered phone number (${currentUser.phoneNumber}) to the LuckyBet Cashier/Admin to credit your wallet balance.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LuckyTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // M-Pesa Withdrawal Form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = LuckyGoldBright
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WITHDRAW WINNINGS TO M-PESA (${currentUser.phoneNumber})",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = withdrawAmountInput,
                        onValueChange = { withdrawAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Withdrawal Amount (KES)") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mpesa_amount_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                                    modifier = Modifier.padding(vertical = 6.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val amt = withdrawAmountInput.toDoubleOrNull() ?: 0.0
                            onWithdraw(currentUser.phoneNumber, amt)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuckyGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("mpesa_submit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SendToMobile,
                            contentDescription = null,
                            tint = Color(0xFF1E1B18)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REQUEST M-PESA WITHDRAWAL (KES ${withdrawAmountInput.ifBlank { "0" }})",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF1E1B18)
                        )
                    }
                }
            }
        }

        // Recent Account Statement
        item {
            Text(
                text = "MY WALLET TRANSACTIONS (${transactions.size})",
                style = MaterialTheme.typography.titleMedium,
                color = LuckyTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        if (transactions.isEmpty()) {
            item {
                Text(
                    text = "No transactions yet. Once funded, your credits and winnings will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LuckyTextSecondary
                )
            }
        } else {
            items(transactions, key = { it.id }) { tx ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${tx.type} • ${tx.mpesaCode}",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Phone: ${tx.phoneNumber} • Status: ${tx.status}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuckyTextSecondary
                            )
                        }
                        Text(
                            text = (if (tx.amount >= 0) "+KES " else "-KES ") +
                                String.format(Locale.US, "%,.2f", kotlin.math.abs(tx.amount)),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (tx.amount >= 0) LuckyGreenBright else LuckyCrimsonLight
                        )
                    }
                }
            }
        }
    }
}
