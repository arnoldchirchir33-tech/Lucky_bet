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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.BetTicketEntity
import com.example.data.MatchEntity
import com.example.data.SiteConfigEntity
import com.example.data.UserAccountEntity
import com.example.data.WalletTransactionEntity
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun AdminLoginScreen(
    loginError: String?,
    onLogin: (String, String) -> Unit,
    onBackToSite: () -> Unit
) {
    BackHandler { onBackToSite() }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuckyBgDark)
            .padding(18.dp)
            .testTag("admin_login_screen"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, LuckyGold),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = LuckyGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Portal",
                            tint = LuckyGoldBright
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "luckybet.com/admin_login",
                            style = MaterialTheme.typography.labelLarge,
                            color = LuckyGoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "LUCKYBET ADMIN PORTAL",
                    style = MaterialTheme.typography.headlineMedium,
                    color = LuckyTextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Restricted Access • Sign in to fund user accounts & manage Aviator/Sports",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LuckyTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Admin Username") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = LuckyGoldBright)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_username_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Admin Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = LuckyGoldBright)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input")
                )

                if (loginError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loginError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuckyCrimsonLight,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag("admin_login_error")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onLogin(username, password) },
                    colors = ButtonDefaults.buttonColors(containerColor = LuckyGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("admin_login_submit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color(0xFF1E1B18)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOGIN TO ADMIN PANEL",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1E1B18),
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onBackToSite) {
                    Text("← Return to luckybet.com", color = LuckyTextSecondary)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    config: SiteConfigEntity,
    users: List<UserAccountEntity>,
    matches: List<MatchEntity>,
    bets: List<BetTicketEntity>,
    transactions: List<WalletTransactionEntity>,
    onFundUserByPhone: (String, Double) -> Unit,
    onSetUserExactBalance: (String, Double) -> Unit,
    onGenerateVerificationCode: (String) -> Unit = {},
    onVerifyUserDirectly: (String) -> Unit = {},
    onToggleFreezeUser: (String, Boolean) -> Unit = { _, _ -> },
    onToggleBanUser: (String, Boolean) -> Unit = { _, _ -> },
    onSetNextCrash: (Double?) -> Unit,
    onSetHouseMode: (String) -> Unit,
    onUpdatePromo: (String) -> Unit,
    onSaveMatch: (MatchEntity) -> Unit,
    onDeleteMatch: (Int) -> Unit,
    onSettleTicket: (BetTicketEntity, String) -> Unit,
    onResolveWithdrawal: (WalletTransactionEntity, Boolean) -> Unit,
    onLogout: () -> Unit
) {
    BackHandler { onLogout() }

    var selectedAdminSection by remember { mutableStateOf(0) }
    val sections = listOf("Users & Verification", "Aviator Control", "Sports & Odds", "Bets & Withdrawals")

    val totalStaked = remember(bets) { bets.sumOf { it.stake } }
    val totalPaid = remember(bets) {
        bets.filter { it.status == "WON" || it.status == "CASHED_OUT" }.sumOf { it.potentialPayout }
    }
    val houseGgr = totalStaked - totalPaid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LuckyBgDark)
            .testTag("admin_dashboard_screen")
    ) {
        // Admin Header Bar
        Surface(
            color = LuckySurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Admin",
                            tint = LuckyGoldBright
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "LUCKYBET ADMIN • kenya254",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyGoldBright,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "luckybet.com/admin_login • Registered Users: ${users.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuckyTextSecondary
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onLogout,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = LuckyCrimsonLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exit Admin", color = LuckyCrimsonLight)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // KPI Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminKpiTile(
                        title = "Users",
                        value = "${users.size}",
                        accent = LuckyGoldBright,
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiTile(
                        title = "Total Staked",
                        value = "KES ${String.format(Locale.US, "%,.0f", totalStaked)}",
                        accent = LuckyTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiTile(
                        title = "House GGR",
                        value = "KES ${String.format(Locale.US, "%,.0f", houseGgr)}",
                        accent = if (houseGgr >= 0) LuckyGreenBright else LuckyCrimsonLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sub-navigation tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedAdminSection,
                    containerColor = LuckySurfaceElevated,
                    contentColor = LuckyGoldBright,
                    edgePadding = 4.dp
                ) {
                    sections.forEachIndexed { idx, label ->
                        Tab(
                            selected = selectedAdminSection == idx,
                            onClick = { selectedAdminSection = idx },
                            text = {
                                Text(
                                    text = label,
                                    fontWeight = if (selectedAdminSection == idx) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("admin_tab_$idx")
                        )
                    }
                }
            }
        }

        // Section Content
        when (selectedAdminSection) {
            0 -> AdminUserAccountsAndVerificationTab(
                users = users,
                promoBannerText = config.promoBannerText,
                onFundUserByPhone = onFundUserByPhone,
                onSetUserExactBalance = onSetUserExactBalance,
                onGenerateVerificationCode = onGenerateVerificationCode,
                onVerifyUserDirectly = onVerifyUserDirectly,
                onToggleFreezeUser = onToggleFreezeUser,
                onToggleBanUser = onToggleBanUser,
                onUpdatePromo = onUpdatePromo
            )
            1 -> AdminAviatorControlTab(
                config = config,
                onSetNextCrash = onSetNextCrash,
                onSetHouseMode = onSetHouseMode
            )
            2 -> AdminSportsManagerTab(
                matches = matches,
                onSaveMatch = onSaveMatch,
                onDeleteMatch = onDeleteMatch
            )
            3 -> AdminBetsAndMpesaTab(
                bets = bets,
                transactions = transactions,
                onSettleTicket = onSettleTicket,
                onResolveWithdrawal = onResolveWithdrawal
            )
        }
    }
}

@Composable
private fun AdminKpiTile(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LuckySurfaceElevated,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = LuckyTextSecondary)
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminUserAccountsAndVerificationTab(
    users: List<UserAccountEntity>,
    promoBannerText: String,
    onFundUserByPhone: (String, Double) -> Unit,
    onSetUserExactBalance: (String, Double) -> Unit,
    onGenerateVerificationCode: (String) -> Unit,
    onVerifyUserDirectly: (String) -> Unit,
    onToggleFreezeUser: (String, Boolean) -> Unit,
    onToggleBanUser: (String, Boolean) -> Unit,
    onUpdatePromo: (String) -> Unit
) {
    var phoneToFund by remember { mutableStateOf("") }
    var amountToFund by remember { mutableStateOf("500") }
    var promoInput by remember(promoBannerText) { mutableStateOf(promoBannerText) }
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("ALL") } // ALL, PENDING, ACTIVE, FROZEN, BANNED
    val quickFundAmounts = listOf(100, 250, 500, 1000, 2500, 5000)

    val pendingCount = remember(users) { users.count { !it.isVerified } }
    val frozenCount = remember(users) { users.count { it.isFrozen } }
    val bannedCount = remember(users) { users.count { it.isBanned } }
    val activeCount = remember(users) { users.count { it.isVerified && !it.isFrozen && !it.isBanned } }

    val filteredUsers = remember(users, searchQuery, filterMode) {
        users.filter { u ->
            val matchesSearch = searchQuery.isBlank() ||
                u.phoneNumber.contains(searchQuery, ignoreCase = true) ||
                u.fullName.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (filterMode) {
                "PENDING" -> !u.isVerified
                "ACTIVE" -> u.isVerified && !u.isFrozen && !u.isBanned
                "FROZEN" -> u.isFrozen
                "BANNED" -> u.isBanned
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top User Status Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = LuckySurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Active", style = MaterialTheme.typography.bodySmall, color = LuckyTextSecondary)
                        Text("$activeCount", style = MaterialTheme.typography.titleMedium, color = LuckyGreenBright, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    color = LuckySurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Pending Code", style = MaterialTheme.typography.bodySmall, color = LuckyTextSecondary)
                        Text("$pendingCount", style = MaterialTheme.typography.titleMedium, color = LuckyGoldBright, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    color = LuckySurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Frozen", style = MaterialTheme.typography.bodySmall, color = LuckyTextSecondary)
                        Text("$frozenCount", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF97316), fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    color = LuckySurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Banned", style = MaterialTheme.typography.bodySmall, color = LuckyTextSecondary)
                        Text("$bannedCount", style = MaterialTheme.typography.titleMedium, color = LuckyCrimsonLight, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Fund Tool Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, LuckyGreen)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = LuckyGreenBright
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DIRECT ACCOUNT FUNDING",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyGreenBright,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Credit any user account by phone number, or select a user from the list below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuckyTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneToFund,
                        onValueChange = { phoneToFund = it },
                        label = { Text("User Phone (e.g. 0712345678)") },
                        leadingIcon = {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = LuckyGoldBright)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_fund_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = amountToFund,
                        onValueChange = { amountToFund = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Amount (KES)") },
                        leadingIcon = {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = LuckyGreenBright)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_fund_amount_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickFundAmounts.forEach { amt ->
                            Surface(
                                color = LuckySurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { amountToFund = amt.toString() }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "+$amt",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LuckyTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val amt = amountToFund.toDoubleOrNull() ?: 0.0
                                onFundUserByPhone(phoneToFund, amt)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(50.dp)
                                .testTag("admin_fund_account_button")
                        ) {
                            Icon(Icons.Default.AddCard, contentDescription = null, tint = Color(0xFF042F2E))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FUND (+KES ${amountToFund.ifBlank { "0" }})",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF042F2E),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val amt = amountToFund.toDoubleOrNull() ?: 0.0
                                onSetUserExactBalance(phoneToFund, amt)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(50.dp)
                                .testTag("admin_set_exact_balance_button")
                        ) {
                            Text(
                                text = "SET EXACT",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuckyGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Search and Filter Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "USER MANAGEMENT & VERIFICATION (${users.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = LuckyGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search users by phone or name...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = LuckyTextSecondary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = LuckyTextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter chips row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "ALL" to "All (${users.size})",
                            "PENDING" to "Pending ($pendingCount)",
                            "ACTIVE" to "Active ($activeCount)",
                            "FROZEN" to "Frozen ($frozenCount)",
                            "BANNED" to "Banned ($bannedCount)"
                        ).forEach { (key, label) ->
                            val isSelected = filterMode == key
                            Surface(
                                color = if (isSelected) LuckyGold else LuckySurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { filterMode = key }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color(0xFF1E1B18) else LuckyTextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // List of Users
        if (filteredUsers.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (users.isEmpty()) {
                            "No users have registered yet. When a user registers on luckybet.com, their profile will appear here with an admin-generated verification code."
                        } else {
                            "No users matching search or filter '$filterMode'."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuckyTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(filteredUsers, key = { it.id }) { u ->
                val isSelected = phoneToFund == u.phoneNumber

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) LuckySurfaceElevated else LuckySurfaceDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        when {
                            u.isBanned -> LuckyCrimson
                            u.isFrozen -> Color(0xFFF97316)
                            isSelected -> LuckyGreen
                            !u.isVerified -> LuckyGold
                            else -> LuckyBorder
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_user_card_${u.phoneNumber}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // User Header Row (Name, Phone, Balance, Status Badges)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = u.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Phone: ${u.phoneNumber}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = LuckyGoldBright
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "KES ${String.format(Locale.US, "%,.2f", u.balance)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = LuckyGreenBright,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Bonus: KES ${String.format(Locale.US, "%,.0f", u.bonusBalance)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuckyTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Badges Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Verification status badge
                            Surface(
                                color = if (u.isVerified) LuckyGreen.copy(alpha = 0.2f) else LuckyGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (u.isVerified) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        tint = if (u.isVerified) LuckyGreenBright else LuckyGoldBright,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (u.isVerified) "VERIFIED" else "PENDING VERIFICATION",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (u.isVerified) LuckyGreenBright else LuckyGoldBright,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Ban badge
                            if (u.isBanned) {
                                Surface(
                                    color = LuckyCrimson.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "BANNED",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LuckyCrimsonLight,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else if (u.isFrozen) {
                                Surface(
                                    color = Color(0x33F97316),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "FROZEN",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFF97316),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    color = LuckyGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LuckyGreenBright,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Verification Code Section
                        Surface(
                            color = LuckyBgDark,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "VERIFICATION CODE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LuckyTextSecondary
                                    )
                                    Text(
                                        text = u.verificationCode.ifBlank { "NOT GENERATED" },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = LuckyGoldBright,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { onGenerateVerificationCode(u.phoneNumber) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("New Code", style = MaterialTheme.typography.labelSmall)
                                    }

                                    if (!u.isVerified) {
                                        Button(
                                            onClick = { onVerifyUserDirectly(u.phoneNumber) },
                                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF042F2E), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve", style = MaterialTheme.typography.labelSmall, color = Color(0xFF042F2E), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Admin Action Buttons Row: Freeze, Ban, Select Phone
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Freeze / Unfreeze
                            OutlinedButton(
                                onClick = { onToggleFreezeUser(u.phoneNumber, u.isFrozen) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (u.isFrozen) LuckyGreenBright else Color(0xFFF97316)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (u.isFrozen) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (u.isFrozen) "Unfreeze" else "Freeze",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Ban / Unban
                            OutlinedButton(
                                onClick = { onToggleBanUser(u.phoneNumber, u.isBanned) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (u.isBanned) LuckyGreenBright else LuckyCrimsonLight
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (u.isBanned) Icons.Default.CheckCircle else Icons.Default.Block,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (u.isBanned) "Unban" else "Ban User",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Select for Funding
                            Button(
                                onClick = { phoneToFund = u.phoneNumber },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) LuckyGold else LuckySurfaceElevated
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.1f)
                            ) {
                                Text(
                                    text = if (isSelected) "Selected" else "Select Phone",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color(0xFF1E1B18) else LuckyTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Promo Ticker Settings
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HOMEPAGE PROMO ANNOUNCEMENT",
                        style = MaterialTheme.typography.titleMedium,
                        color = LuckyGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = promoInput,
                        onValueChange = { promoInput = it },
                        label = { Text("Banner Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onUpdatePromo(promoInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = LuckyGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SAVE BANNER", color = Color(0xFF1E1B18), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


@Composable
private fun AdminAviatorControlTab(
    config: SiteConfigEntity,
    onSetNextCrash: (Double?) -> Unit,
    onSetHouseMode: (String) -> Unit
) {
    var customMultInput by remember { mutableStateOf("10.00") }
    val presets = listOf(1.01, 1.50, 2.50, 5.00, 15.00, 50.00, 100.00)
    val modes = listOf("FAIR", "TIGHT", "HIGH_PAYOUT")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyCrimson)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = LuckyCrimsonLight
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FORCE NEXT AVIATOR CRASH MULTIPLIER",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (config.nextForcedAviatorCrash != null) {
                            "ACTIVE OVERRIDE: Next flight will crash at ${String.format(Locale.US, "%.2fx", config.nextForcedAviatorCrash)}"
                        } else {
                            "Current Status: Provably Fair RNG (${config.aviatorHouseEdgeMode} Mode)"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = if (config.nextForcedAviatorCrash != null) LuckyGoldBright else LuckyGreenBright
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.take(4).forEach { mult ->
                            Button(
                                onClick = { onSetNextCrash(mult) },
                                colors = ButtonDefaults.buttonColors(containerColor = LuckySurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${mult}x",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyGoldBright
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.drop(4).forEach { mult ->
                            Button(
                                onClick = { onSetNextCrash(mult) },
                                colors = ButtonDefaults.buttonColors(containerColor = LuckySurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${mult.toInt()}x",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyCrimsonLight
                                )
                            }
                        }
                        Button(
                            onClick = { onSetNextCrash(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "AUTO RNG",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF042F2E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customMultInput,
                            onValueChange = { customMultInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Custom Crash Multiplier (e.g. 25.50)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_custom_crash_input")
                        )
                        Button(
                            onClick = {
                                val v = customMultInput.toDoubleOrNull()
                                if (v != null && v >= 1.00) {
                                    onSetNextCrash(v)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyCrimson),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("admin_set_crash_button")
                        ) {
                            Text("SET CRASH", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AVIATOR HOUSE EDGE DISTRIBUTION",
                        style = MaterialTheme.typography.titleMedium,
                        color = LuckyTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        modes.forEach { mode ->
                            val selected = config.aviatorHouseEdgeMode == mode
                            Button(
                                onClick = { onSetHouseMode(mode) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) LuckyGold else LuckySurfaceElevated
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = mode,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) Color(0xFF1E1B18) else LuckyTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminSportsManagerTab(
    matches: List<MatchEntity>,
    onSaveMatch: (MatchEntity) -> Unit,
    onDeleteMatch: (Int) -> Unit
) {
    var league by remember { mutableStateOf("Kenya • FKF Premier League") }
    var homeTeam by remember { mutableStateOf("") }
    var awayTeam by remember { mutableStateOf("") }
    var odd1 by remember { mutableStateOf("1.95") }
    var oddX by remember { mutableStateOf("3.40") }
    var odd2 by remember { mutableStateOf("3.80") }
    var isLive by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGreen)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ADD NEW SPORTSBOOK FIXTURE",
                        style = MaterialTheme.typography.titleMedium,
                        color = LuckyGreenBright,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = league,
                        onValueChange = { league = it },
                        label = { Text("League / Tournament") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = homeTeam,
                            onValueChange = { homeTeam = it },
                            label = { Text("Home Team") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_home_team_input")
                        )
                        OutlinedTextField(
                            value = awayTeam,
                            onValueChange = { awayTeam = it },
                            label = { Text("Away Team") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_away_team_input")
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = odd1,
                            onValueChange = { odd1 = it },
                            label = { Text("Odd 1") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = oddX,
                            onValueChange = { oddX = it },
                            label = { Text("Odd X") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = odd2,
                            onValueChange = { odd2 = it },
                            label = { Text("Odd 2") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isLive, onCheckedChange = { isLive = it })
                            Text("Mark as LIVE In-Play Match", color = LuckyTextPrimary)
                        }
                        Button(
                            onClick = {
                                if (homeTeam.isNotBlank() && awayTeam.isNotBlank()) {
                                    onSaveMatch(
                                        MatchEntity(
                                            league = league.ifBlank { "International Friendly" },
                                            sport = "Football",
                                            homeTeam = homeTeam.trim(),
                                            awayTeam = awayTeam.trim(),
                                            startTime = if (isLive) "LIVE" else "Today, 20:00",
                                            isLive = isLive,
                                            liveMinute = if (isLive) "12'" else "",
                                            homeScore = 0,
                                            awayScore = 0,
                                            oddHome = odd1.toDoubleOrNull() ?: 2.0,
                                            oddDraw = oddX.toDoubleOrNull() ?: 3.2,
                                            oddAway = odd2.toDoubleOrNull() ?: 3.5,
                                            oddOver25 = 1.75,
                                            oddUnder25 = 2.05,
                                            oddBttsYes = 1.68
                                        )
                                    )
                                    homeTeam = ""
                                    awayTeam = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                            modifier = Modifier.testTag("admin_publish_match_button")
                        ) {
                            Text("PUBLISH FIXTURE", color = Color(0xFF042F2E), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(matches, key = { it.id }) { match ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
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
                            text = "${match.homeTeam} vs ${match.awayTeam}",
                            style = MaterialTheme.typography.titleMedium,
                            color = LuckyTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${match.league} • 1: ${match.oddHome} | X: ${match.oddDraw} | 2: ${match.oddAway}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LuckyTextSecondary
                        )
                    }
                    IconButton(onClick = { onDeleteMatch(match.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Fixture",
                            tint = LuckyCrimsonLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminBetsAndMpesaTab(
    bets: List<BetTicketEntity>,
    transactions: List<WalletTransactionEntity>,
    onSettleTicket: (BetTicketEntity, String) -> Unit,
    onResolveWithdrawal: (WalletTransactionEntity, Boolean) -> Unit
) {
    val pendingWithdrawals = transactions.filter { it.type == "WITHDRAWAL" && it.status == "PENDING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "PENDING M-PESA WITHDRAWALS (${pendingWithdrawals.size})",
                style = MaterialTheme.typography.titleMedium,
                color = LuckyGoldBright,
                fontWeight = FontWeight.Bold
            )
        }

        if (pendingWithdrawals.isEmpty()) {
            item {
                Text(
                    text = "No pending M-Pesa withdrawal requests.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LuckyTextSecondary
                )
            }
        } else {
            items(pendingWithdrawals, key = { it.id }) { tx ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold)
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
                                text = "${tx.mpesaCode} • KES ${String.format(Locale.US, "%,.0f", kotlin.math.abs(tx.amount))}",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuckyTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Phone: ${tx.phoneNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuckyTextSecondary
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { onResolveWithdrawal(tx, true) },
                                colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen)
                            ) {
                                Text("APPROVE", color = Color(0xFF042F2E), fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(onClick = { onResolveWithdrawal(tx, false) }) {
                                Text("REJECT", color = LuckyCrimsonLight)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "USER BET TICKETS SETTLEMENT (${bets.size})",
                style = MaterialTheme.typography.titleMedium,
                color = LuckyGreenBright,
                fontWeight = FontWeight.Bold
            )
        }

        items(bets, key = { it.id }) { ticket ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LuckySurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "#${ticket.ticketCode} • User: ${ticket.userPhone}",
                            style = MaterialTheme.typography.labelLarge,
                            color = LuckyGoldBright
                        )
                        Text(
                            text = "Status: ${ticket.status}",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuckyTextPrimary
                        )
                    }
                    Text(
                        text = ticket.summary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = LuckyTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Stake: KES ${ticket.stake.toInt()} • Potential Win: KES ${String.format(Locale.US, "%,.0f", ticket.potentialPayout)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuckyTextSecondary
                    )
                    if (ticket.status == "OPEN") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onSettleTicket(ticket, "WON") },
                                colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("SETTLE AS WON", color = Color(0xFF042F2E), fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onSettleTicket(ticket, "LOST") },
                                colors = ButtonDefaults.buttonColors(containerColor = LuckyCrimson),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("SETTLE AS LOST", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
