package hr.ferit.matijasokol.coinmarket.repository

import hr.ferit.matijasokol.coinmarket.db.CoinDao
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.networking.CoinsMarketApi
import javax.inject.Inject

class CoinMarketRepository @Inject constructor(
    private val coinDao: CoinDao,
    private val api: CoinsMarketApi
) {

    suspend fun getCoins() = api.getCoins()

    suspend fun getYearCoinDetails(id: String) = api.getYearCoinDetails(id)

    suspend fun getLastDayCoinDetails(id: String) = api.getLastDayCoinDetails(id)

    suspend fun getCoinInfo(id: String) = api.getCoinInfo(id)

    suspend fun upsertList(coins: List<Coin>) = coins.forEach { coinDao.upsert(it) }

    suspend fun getSavedCoins() = coinDao.getAllCoins()
}