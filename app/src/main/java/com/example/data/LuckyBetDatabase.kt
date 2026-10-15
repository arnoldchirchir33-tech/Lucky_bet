package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LuckyBetDao {
    // Users
    @Query("SELECT * FROM users ORDER BY registeredAt DESC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserAccountEntity?

    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    fun observeUserByPhone(phone: String): Flow<UserAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("UPDATE users SET isFrozen = :isFrozen WHERE phoneNumber = :phone")
    suspend fun setUserFrozen(phone: String, isFrozen: Boolean)

    @Query("UPDATE users SET isBanned = :isBanned WHERE phoneNumber = :phone")
    suspend fun setUserBanned(phone: String, isBanned: Boolean)

    @Query("UPDATE users SET verificationCode = :code, isVerified = :isVerified WHERE phoneNumber = :phone")
    suspend fun setUserVerification(phone: String, code: String, isVerified: Boolean)

    // Matches
    @Query("SELECT * FROM matches ORDER BY isLive DESC, id ASC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT COUNT(*) FROM matches")
    suspend fun getMatchCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("DELETE FROM matches WHERE id = :matchId")
    suspend fun deleteMatch(matchId: Int)

    // Bets
    @Query("SELECT * FROM bets ORDER BY placedAt DESC")
    fun getAllBets(): Flow<List<BetTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBet(bet: BetTicketEntity)

    @Update
    suspend fun updateBet(bet: BetTicketEntity)

    // Aviator History
    @Query("SELECT * FROM aviator_history ORDER BY timestamp DESC LIMIT 25")
    fun getAviatorHistory(): Flow<List<AviatorRoundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAviatorRound(round: AviatorRoundEntity)

    @Query("SELECT COUNT(*) FROM aviator_history")
    suspend fun getAviatorCount(): Int

    // Wallet Transactions
    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: WalletTransactionEntity)

    @Update
    suspend fun updateTransaction(tx: WalletTransactionEntity)

    // Site Config
    @Query("SELECT * FROM site_config WHERE id = 1")
    fun getSiteConfig(): Flow<SiteConfigEntity?>

    @Query("SELECT * FROM site_config WHERE id = 1")
    suspend fun getSiteConfigOnce(): SiteConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSiteConfig(config: SiteConfigEntity)
}

@Database(
    entities = [
        UserAccountEntity::class,
        MatchEntity::class,
        BetTicketEntity::class,
        AviatorRoundEntity::class,
        WalletTransactionEntity::class,
        SiteConfigEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class LuckyBetDatabase : RoomDatabase() {
    abstract fun dao(): LuckyBetDao

    companion object {
        @Volatile
        private var INSTANCE: LuckyBetDatabase? = null

        fun getDatabase(context: Context): LuckyBetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuckyBetDatabase::class.java,
                    "luckybet_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
