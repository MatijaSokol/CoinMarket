package hr.ferit.matijasokol.coinmarket.repository

import hr.ferit.matijasokol.coinmarket.db.CoinDatabase
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.networking.RetrofitInstance

class CoinMarketRepository(private val db: CoinDatabase) {

    suspend fun getCoins() = RetrofitInstance.api.getCoins()

    suspend fun getYearCoinDetails(id: String) = RetrofitInstance.api.getYearCoinDetails(id)

    suspend fun getLastDayCoinDetails(id: String) = RetrofitInstance.api.getLastDayCoinDetails(id)

    suspend fun getCoinInfo(id: String) = RetrofitInstance.api.getCoinInfo(id)

    suspend fun upsertList(coins: List<Coin>) = coins.forEach { db.getCoinDao().upsert(it) }

    suspend fun getSavedCoins() = db.getCoinDao().getAllCoins()
}