package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["phoneNumber"], unique = true)]
)
data class UserAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumber: String,
    val fullName: String,
    val password: String,
    val balance: Double = 0.0,
    val bonusBalance: Double = 0.0,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val league: String,
    val sport: String, // "Football", "Basketball", "Tennis", "Jackpot"
    val homeTeam: String,
    val awayTeam: String,
    val startTime: String,
    val isLive: Boolean,
    val liveMinute: String,
    val homeScore: Int,
    val awayScore: Int,
    val oddHome: Double,
    val oddDraw: Double,
    val oddAway: Double,
    val oddOver25: Double,
    val oddUnder25: Double,
    val oddBttsYes: Double,
    val status: String = "OPEN" // "OPEN", "HOME", "DRAW", "AWAY"
)

@Entity(tableName = "bets")
data class BetTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketCode: String,
    val userPhone: String,
    val summary: String, // e.g. "Gor Mahia vs AFC Leopards (1) + 2 more"
    val selectionsCount: Int,
    val totalOdds: Double,
    val stake: Double,
    val potentialPayout: Double,
    val status: String, // "OPEN", "WON", "LOST", "CASHED_OUT"
    val category: String, // "SPORTS", "AVIATOR"
    val placedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "aviator_history")
data class AviatorRoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val multiplier: Double,
    val roundHash: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mpesaCode: String,
    val phoneNumber: String,
    val type: String, // "ADMIN_FUND", "WITHDRAWAL", "BET_STAKE", "BET_WIN"
    val amount: Double,
    val status: String, // "COMPLETED", "PENDING", "REJECTED"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "site_config")
data class SiteConfigEntity(
    @PrimaryKey val id: Int = 1,
    val loggedInUserPhone: String? = null, // Persisted logged-in user phone number
    val nextForcedAviatorCrash: Double? = null,
    val aviatorHouseEdgeMode: String = "FAIR", // "FAIR", "HIGH_PAYOUT", "TIGHT"
    val minStake: Double = 10.0,
    val maxPayout: Double = 5000000.0,
    val promoBannerText: String = "SHINDA MILLIONS DAILY! PLAY AVIATOR & LIVE SPORTS ON LUCKYBET.COM!"
)

data class BetSelection(
    val matchId: Int,
    val matchTitle: String,
    val league: String,
    val marketName: String,
    val pickCode: String,
    val odd: Double
)
