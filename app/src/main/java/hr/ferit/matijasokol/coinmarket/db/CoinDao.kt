package hr.ferit.matijasokol.coinmarket.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hr.ferit.matijasokol.coinmarket.models.Coin

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(coin: Coin)

    @Query("SELECT * FROM coins ORDER BY marketCap DESC")
    suspend fun getAllCoins(): List<Coin>
}