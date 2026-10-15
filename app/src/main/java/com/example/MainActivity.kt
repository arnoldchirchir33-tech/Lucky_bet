package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.LuckyBetDatabase
import com.example.data.LuckyBetRepository
import com.example.data.UserAccountEntity
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.LuckyBetViewModel
import com.example.viewmodel.LuckyTab
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = LuckyBetDatabase.getDatabase(applicationContext)
        val repository = LuckyBetRepository(database.dao())

        setContent {
            MyApplicationTheme {
                val viewModel: LuckyBetViewModel = viewModel(
                    factory = LuckyBetViewModel.Factory(repository)
                )
                LuckyBetApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LuckyBetApp(viewModel: LuckyBetViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val addressBarInput by viewModel.addressBarInput.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val allBets by viewModel.allBets.collectAsStateWithLifecycle()
    val userBets by viewModel.userBets.collectAsStateWithLifecycle()
    val aviatorHistory by viewModel.aviatorHistory.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val userTransactions by viewModel.userTransactions.collectAsStateWithLifecycle()
    val siteConfig by viewModel.siteConfig.collectAsStateWithLifecycle()
    val selectedSport by viewModel.selectedSportFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val betSlip by viewModel.betSlip.collectAsStateWithLifecycle()
    val slipStakeInput by viewModel.slipStakeInput.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val adminLoginError by viewModel.adminLoginError.collectAsStateWithLifecycle()
    val userAuthError by viewModel.userAuthError.collectAsStateWithLifecycle()

    val aviatorPhase by viewModel.aviatorPhase.collectAsStateWithLifecycle()
    val currentMultiplier by viewModel.currentMultiplier.collectAsStateWithLifecycle()
    val countdownSeconds by viewModel.countdownSeconds.collectAsStateWithLifecycle()
    val slot1 by viewModel.slot1.collectAsStateWithLifecycle()
    val slot2 by viewModel.slot2.collectAsStateWithLifecycle()
    val liveLobby by viewModel.liveLobbyPlayers.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearMessage()
        }
    }

    val isAdminRoute = currentTab == LuckyTab.ADMIN_LOGIN || currentTab == LuckyTab.ADMIN_DASHBOARD
    val activeUser = currentUser

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LuckyBgDark,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = LuckySurfaceElevated,
                    contentColor = LuckyGreenBright,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            LuckyBetTopBrowserHeader(
                addressBarValue = addressBarInput,
                currentUser = activeUser,
                onAddressChange = viewModel::onAddressBarChanged,
                onNavigateUrl = { viewModel.submitAddressBarUrl(it) },
                onAccountClick = {
                    if (activeUser != null) {
                        viewModel.navigateToTab(LuckyTab.WALLET)
                    }
                },
                onLogoClick = { viewModel.navigateToTab(LuckyTab.SPORTS) }
            )
        },
        bottomBar = {
            // Show bottom navigation bar only when a user is logged in and not inside the secret admin portal
            if (activeUser != null && !isAdminRoute) {
                LuckyBetBottomNavigationBar(
                    currentTab = currentTab,
                    betSlipCount = betSlip.size,
                    openBetsCount = userBets.count { it.status == "OPEN" },
                    onSelectTab = { viewModel.navigateToTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // 1. Direct secret search to luckybet.com/admin_login works anytime
                currentTab == LuckyTab.ADMIN_LOGIN -> AdminLoginScreen(
                    loginError = adminLoginError,
                    onLogin = viewModel::loginAdmin,
                    onBackToSite = { viewModel.navigateToTab(LuckyTab.SPORTS) }
                )

                currentTab == LuckyTab.ADMIN_DASHBOARD -> AdminDashboardScreen(
                    config = siteConfig,
                    users = allUsers,
                    matches = matches,
                    bets = allBets,
                    transactions = allTransactions,
                    onFundUserByPhone = viewModel::adminFundUserByPhone,
                    onSetUserExactBalance = viewModel::adminSetUserExactBalance,
                    onSetNextCrash = viewModel::adminSetNextAviatorCrash,
                    onSetHouseMode = viewModel::adminSetAviatorHouseMode,
                    onUpdatePromo = viewModel::adminUpdatePromoBanner,
                    onSaveMatch = viewModel::adminSaveMatch,
                    onDeleteMatch = viewModel::adminDeleteMatch,
                    onSettleTicket = viewModel::adminSettleTicket,
                    onResolveWithdrawal = viewModel::adminResolveWithdrawal,
                    onLogout = viewModel::logoutAdmin
                )

                // 2. Gate entry to the app behind Registration & Login if no user is logged in
                activeUser == null -> UserAuthScreen(
                    authError = userAuthError,
                    onClearError = viewModel::clearUserAuthError,
                    onLogin = viewModel::loginUserAccount,
                    onRegister = viewModel::registerUserAccount
                )

                // 3. Authenticated User Screens
                currentTab == LuckyTab.SPORTS -> SportsbookScreen(
                    matches = matches,
                    selectedSport = selectedSport,
                    searchQuery = searchQuery,
                    promoBannerText = siteConfig.promoBannerText,
                    betSlip = betSlip,
                    onSelectSport = viewModel::selectSportFilter,
                    onSearchChange = viewModel::updateSearchQuery,
                    onToggleSelection = viewModel::toggleSelection,
                    onLaunchAviator = { viewModel.navigateToTab(LuckyTab.AVIATOR) },
                    onOpenBetSlip = { viewModel.navigateToTab(LuckyTab.BETSLIP) }
                )

                currentTab == LuckyTab.AVIATOR -> AviatorScreen(
                    phase = aviatorPhase,
                    currentMultiplier = currentMultiplier,
                    countdownSeconds = countdownSeconds,
                    history = aviatorHistory,
                    slot1 = slot1,
                    slot2 = slot2,
                    liveLobby = liveLobby,
                    onUpdateSlotStake = viewModel::updateSlotStake,
                    onToggleAutoCashout = viewModel::toggleSlotAutoCashout,
                    onUpdateAutoTarget = viewModel::updateSlotAutoTarget,
                    onSlotAction = viewModel::onAviatorSlotAction,
                    onBackToSports = { viewModel.navigateToTab(LuckyTab.SPORTS) }
                )

                currentTab == LuckyTab.BETSLIP -> BetSlipScreen(
                    selections = betSlip,
                    stakeInput = slipStakeInput,
                    userBalance = activeUser.balance,
                    onStakeChange = viewModel::updateSlipStake,
                    onRemoveSelection = viewModel::removeSelectionFromSlip,
                    onClearSlip = viewModel::clearBetSlip,
                    onPlaceBet = viewModel::placeSportsBet,
                    onGoToSports = { viewModel.navigateToTab(LuckyTab.SPORTS) }
                )

                currentTab == LuckyTab.MY_BETS -> MyBetsScreen(
                    bets = userBets,
                    onCashoutTicket = viewModel::cashoutOpenSportsTicket,
                    onBackToSports = { viewModel.navigateToTab(LuckyTab.SPORTS) }
                )

                currentTab == LuckyTab.WALLET -> WalletScreen(
                    currentUser = activeUser,
                    transactions = userTransactions,
                    onWithdraw = viewModel::requestWithdrawal,
                    onLogoutUser = viewModel::logoutUserAccount,
                    onBackToSports = { viewModel.navigateToTab(LuckyTab.SPORTS) }
                )

                currentTab == LuckyTab.DEPOSIT -> DepositScreen(
                    currentUser = activeUser,
                    onBack = { viewModel.navigateToTab(LuckyTab.SPORTS) }
                )

                currentTab == LuckyTab.WITHDRAW -> WithdrawalScreen(
                    currentUser = activeUser,
                    onRequestWithdrawal = viewModel::requestWithdrawal,
                    onBack = { viewModel.navigateToTab(LuckyTab.SPORTS) }
                )
            }
        }
    }
}

@Composable
fun LuckyBetTopBrowserHeader(
    addressBarValue: String,
    currentUser: UserAccountEntity?,
    onAddressChange: (String) -> Unit,
    onNavigateUrl: (String) -> Unit,
    onAccountClick: () -> Unit,
    onLogoClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Surface(
        color = LuckySurfaceDark,
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Clean Browser URL / Direct Search Bar (No Admin shortcut button visible!)
            OutlinedTextField(
                value = addressBarValue,
                onValueChange = onAddressChange,
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search URL (e.g. luckybet.com)",
                        style = MaterialTheme.typography.labelMedium,
                        color = LuckyTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "HTTPS Secure",
                        tint = LuckyGreenBright,
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onNavigateUrl(addressBarValue)
                        },
                        modifier = Modifier.testTag("url_go_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Go to URL",
                            tint = LuckyGoldBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        focusManager.clearFocus()
                        onNavigateUrl(addressBarValue)
                    }
                ),
                textStyle = MaterialTheme.typography.labelMedium.copy(color = LuckyTextPrimary),
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuckyGreen,
                    unfocusedBorderColor = LuckyBorder,
                    focusedContainerColor = LuckyBgDark,
                    unfocusedContainerColor = LuckyBgDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("browser_url_input")
            )

            // Brand Identity + Logged-In User Balance Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onLogoClick() }
                        .testTag("luckybet_brand_logo")
                ) {
                    Surface(
                        color = LuckyCrimson,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = "LuckyBet",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LUCKY",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "BET",
                            style = MaterialTheme.typography.titleLarge,
                            color = LuckyGreenBright,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = ".COM",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuckyGoldBright,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                if (currentUser != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = LuckySurfaceElevated,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyBorder),
                            modifier = Modifier
                                .clickable { onAccountClick() }
                                .testTag("header_balance_badge")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = currentUser.phoneNumber,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LuckyTextSecondary
                                )
                                Text(
                                    text = "KES ${String.format(Locale.US, "%,.2f", currentUser.balance)}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = LuckyGreenBright,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onAccountClick,
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGreen),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier
                                .height(42.dp)
                                .testTag("header_account_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = Color(0xFF042F2E),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WALLET",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF042F2E),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                } else {
                    Surface(
                        color = LuckySurfaceElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "LOGIN / REGISTER",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuckyGoldBright,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LuckyBetBottomNavigationBar(
    currentTab: LuckyTab,
    betSlipCount: Int,
    openBetsCount: Int,
    onSelectTab: (LuckyTab) -> Unit
) {
    NavigationBar(
        containerColor = LuckySurfaceDark,
        contentColor = LuckyTextPrimary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == LuckyTab.SPORTS,
            onClick = { onSelectTab(LuckyTab.SPORTS) },
            icon = {
                Icon(imageVector = Icons.Default.SportsSoccer, contentDescription = "Sports")
            },
            label = { Text("Sports") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF042F2E),
                selectedTextColor = LuckyGreenBright,
                indicatorColor = LuckyGreen
            ),
            modifier = Modifier.testTag("nav_tab_sports")
        )

        NavigationBarItem(
            selected = currentTab == LuckyTab.AVIATOR,
            onClick = { onSelectTab(LuckyTab.AVIATOR) },
            icon = {
                Icon(imageVector = Icons.Default.FlightTakeoff, contentDescription = "Aviator")
            },
            label = { Text("Aviator") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = LuckyCrimsonLight,
                indicatorColor = LuckyCrimson
            ),
            modifier = Modifier.testTag("nav_tab_aviator")
        )

        NavigationBarItem(
            selected = currentTab == LuckyTab.BETSLIP,
            onClick = { onSelectTab(LuckyTab.BETSLIP) },
            icon = {
                BadgedBox(
                    badge = {
                        if (betSlipCount > 0) {
                            Badge(
                                containerColor = LuckyGold,
                                contentColor = Color(0xFF1E1B18)
                            ) {
                                Text("$betSlipCount")
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Bet Slip")
                }
            },
            label = { Text("Bet Slip") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF042F2E),
                selectedTextColor = LuckyGreenBright,
                indicatorColor = LuckyGreen
            ),
            modifier = Modifier.testTag("nav_tab_betslip")
        )

        NavigationBarItem(
            selected = currentTab == LuckyTab.MY_BETS,
            onClick = { onSelectTab(LuckyTab.MY_BETS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (openBetsCount > 0) {
                            Badge(
                                containerColor = LuckyGreen,
                                contentColor = Color(0xFF042F2E)
                            ) {
                                Text("$openBetsCount")
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = "My Bets")
                }
            },
            label = { Text("My Bets") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF042F2E),
                selectedTextColor = LuckyGreenBright,
                indicatorColor = LuckyGreen
            ),
            modifier = Modifier.testTag("nav_tab_my_bets")
        )

        NavigationBarItem(
            selected = currentTab == LuckyTab.WALLET,
            onClick = { onSelectTab(LuckyTab.WALLET) },
            icon = {
                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Account")
            },
            label = { Text("Account") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF042F2E),
                selectedTextColor = LuckyGreenBright,
                indicatorColor = LuckyGreen
            ),
            modifier = Modifier.testTag("nav_tab_wallet")
        )
    }
}
