package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LuckyBetRepository(private val dao: LuckyBetDao) {
    val users: Flow<List<UserAccountEntity>> = dao.getAllUsers()
    val matches: Flow<List<MatchEntity>> = dao.getAllMatches()
    val bets: Flow<List<BetTicketEntity>> = dao.getAllBets()
    val aviatorHistory: Flow<List<AviatorRoundEntity>> = dao.getAviatorHistory()
    val transactions: Flow<List<WalletTransactionEntity>> = dao.getAllTransactions()
    val siteConfig: Flow<SiteConfigEntity?> = dao.getSiteConfig()

    fun observeUserByPhone(phone: String): Flow<UserAccountEntity?> = dao.observeUserByPhone(phone)

    suspend fun getUserByPhone(phone: String): UserAccountEntity? = dao.getUserByPhone(phone)

    suspend fun insertUser(user: UserAccountEntity) = dao.insertUser(user)

    suspend fun updateUser(user: UserAccountEntity) = dao.updateUser(user)

    suspend fun setUserFrozen(phone: String, isFrozen: Boolean) = dao.setUserFrozen(phone, isFrozen)

    suspend fun setUserBanned(phone: String, isBanned: Boolean) = dao.setUserBanned(phone, isBanned)

    suspend fun setUserVerification(phone: String, code: String, isVerified: Boolean) = dao.setUserVerification(phone, code, isVerified)

    suspend fun ensureSeeded() {
        val config = dao.getSiteConfigOnce()
        if (config == null) {
            dao.saveSiteConfig(
                SiteConfigEntity(
                    id = 1,
                    loggedInUserPhone = null,
                    nextForcedAviatorCrash = null,
                    aviatorHouseEdgeMode = "FAIR",
                    minStake = 10.0,
                    maxPayout = 5000000.0,
                    promoBannerText = "SHINDA MILLIONS DAILY! PLAY AVIATOR & LIVE SPORTS ON LUCKYBET.COM!"
                )
            )
        }

        if (dao.getMatchCount() == 0) {
            val initialMatches = listOf(
                MatchEntity(
                    league = "Kenya • FKF Premier League",
                    sport = "Football",
                    homeTeam = "Gor Mahia FC",
                    awayTeam = "AFC Leopards",
                    startTime = "LIVE",
                    isLive = true,
                    liveMinute = "67'",
                    homeScore = 2,
                    awayScore = 1,
                    oddHome = 1.48,
                    oddDraw = 3.90,
                    oddAway = 6.50,
                    oddOver25 = 1.35,
                    oddUnder25 = 3.10,
                    oddBttsYes = 1.40
                ),
                MatchEntity(
                    league = "England • Premier League",
                    sport = "Football",
                    homeTeam = "Arsenal",
                    awayTeam = "Manchester City",
                    startTime = "LIVE",
                    isLive = true,
                    liveMinute = "34'",
                    homeScore = 1,
                    awayScore = 1,
                    oddHome = 2.45,
                    oddDraw = 3.25,
                    oddAway = 2.90,
                    oddOver25 = 1.62,
                    oddUnder25 = 2.28,
                    oddBttsYes = 1.50
                ),
                MatchEntity(
                    league = "England • Premier League",
                    sport = "Football",
                    homeTeam = "Liverpool",
                    awayTeam = "Chelsea",
                    startTime = "Today, 19:30",
                    isLive = false,
                    liveMinute = "",
                    homeScore = 0,
                    awayScore = 0,
                    oddHome = 1.78,
                    oddDraw = 3.85,
                    oddAway = 4.20,
                    oddOver25 = 1.55,
                    oddUnder25 = 2.42,
                    oddBttsYes = 1.58
                ),
                MatchEntity(
                    league = "Spain • LaLiga",
                    sport = "Football",
                    homeTeam = "Real Madrid",
                    awayTeam = "FC Barcelona",
                    startTime = "Today, 22:00",
                    isLive = false,
                    liveMinute = "",
                    homeScore = 0,
                    awayScore = 0,
                    oddHome = 2.05,
                    oddDraw = 3.60,
                    oddAway = 3.40,
                    oddOver25 = 1.52,
                    oddUnder25 = 2.50,
                    oddBttsYes = 1.45
                ),
                MatchEntity(
                    league = "Kenya • FKF Premier League",
                    sport = "Football",
                    homeTeam = "Tusker FC",
                    awayTeam = "Kenya Police FC",
                    startTime = "Tomorrow, 15:00",
                    isLive = false,
                    liveMinute = "",
                    homeScore = 0,
                    awayScore = 0,
                    oddHome = 2.20,
                    oddDraw = 3.05,
                    oddAway = 3.30,
                    oddOver25 = 2.10,
                    oddUnder25 = 1.72,
                    oddBttsYes = 1.95
                ),
                MatchEntity(
                    league = "Europe • UEFA Champions League",
                    sport = "Football",
                    homeTeam = "Bayern Munich",
                    awayTeam = "Paris Saint-Germain",
                    startTime = "Tomorrow, 22:00",
                    isLive = false,
                    liveMinute = "",
                    homeScore = 0,
                    awayScore = 0,
                    oddHome = 1.85,
                    oddDraw = 3.90,
                    oddAway = 3.80,
                    oddOver25 = 1.48,
                    oddUnder25 = 2.65,
                    oddBttsYes = 1.44
                ),
                MatchEntity(
                    league = "USA • NBA",
                    sport = "Basketball",
                    homeTeam = "LA Lakers",
                    awayTeam = "Golden State Warriors",
                    startTime = "LIVE",
                    isLive = true,
                    liveMinute = "Q3 04:12",
                    homeScore = 88,
                    awayScore = 84,
                    oddHome = 1.68,
                    oddDraw = 14.00,
                    oddAway = 2.25,
                    oddOver25 = 1.90,
                    oddUnder25 = 1.90,
                    oddBttsYes = 1.85
                ),
                MatchEntity(
                    league = "LuckyBet • Midweek 15M Jackpot",
                    sport = "Jackpot",
                    homeTeam = "AC Milan",
                    awayTeam = "Inter Milan",
                    startTime = "Sat, 21:45",
                    isLive = false,
                    liveMinute = "",
                    homeScore = 0,
                    awayScore = 0,
                    oddHome = 2.75,
                    oddDraw = 3.20,
                    oddAway = 2.60,
                    oddOver25 = 1.85,
                    oddUnder25 = 1.95,
                    oddBttsYes = 1.70
                ),
                MatchEntity(
                    league = "LuckyBet • Midweek 15M Jackpot",
                    sport = "Jackpot",
                    homeTeam = "Napoli",
                    awayTeam = "Juventus",
                    startTime = "Sun, 21:45",
                    isLive = false,
                    liveMinute = "",
                    homeScore = 0,
                    awayScore = 0,
                    oddHome = 2.35,
                    oddDraw = 3.15,
                    oddAway = 3.10,
                    oddOver25 = 2.00,
                    oddUnder25 = 1.80,
                    oddBttsYes = 1.82
                )
            )
            dao.insertMatches(initialMatches)
        }

        if (dao.getAviatorCount() == 0) {
            val sampleMultipliers = listOf(1.42, 3.85, 1.12, 14.60, 2.08, 1.04, 5.40, 1.88, 42.10, 1.31, 2.74, 8.95)
            sampleMultipliers.forEachIndexed { idx, mult ->
                dao.insertAviatorRound(
                    AviatorRoundEntity(
                        multiplier = mult,
                        roundHash = UUID.randomUUID().toString().replace("-", "").take(16),
                        timestamp = System.currentTimeMillis() - (sampleMultipliers.size - idx) * 20000L
                    )
                )
            }
        }
    }

    suspend fun updateUserBalance(phone: String, newBalance: Double) {
        val user = dao.getUserByPhone(phone) ?: return
        dao.updateUser(user.copy(balance = newBalance.coerceAtLeast(0.0)))
    }

    suspend fun updateSiteConfig(config: SiteConfigEntity) {
        dao.saveSiteConfig(config)
    }

    suspend fun getSiteConfigOnce(): SiteConfigEntity {
        return dao.getSiteConfigOnce() ?: SiteConfigEntity()
    }

    suspend fun recordAviatorRound(multiplier: Double) {
        dao.insertAviatorRound(
            AviatorRoundEntity(
                multiplier = multiplier,
                roundHash = "SHA256:" + UUID.randomUUID().toString().replace("-", "").take(14).uppercase()
            )
        )
    }

    suspend fun insertBetTicket(ticket: BetTicketEntity) {
        dao.insertBet(ticket)
    }

    suspend fun updateBetTicket(ticket: BetTicketEntity) {
        dao.updateBet(ticket)
    }

    suspend fun insertTransaction(tx: WalletTransactionEntity) {
        dao.insertTransaction(tx)
    }

    suspend fun updateTransaction(tx: WalletTransactionEntity) {
        dao.updateTransaction(tx)
    }

    suspend fun addOrUpdateMatch(match: MatchEntity) {
        dao.insertMatch(match)
    }

    suspend fun deleteMatch(matchId: Int) {
        dao.deleteMatch(matchId)
    }
}
