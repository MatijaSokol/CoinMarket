package hr.ferit.matijasokol.coinmarket.db

import androidx.room.Database
import androidx.room.RoomDatabase
import hr.ferit.matijasokol.coinmarket.models.Coin

@Database(entities = [Coin::class], version = 1)
abstract class CoinDatabase : RoomDatabase() {

    abstract fun getCoinDao(): CoinDao
}