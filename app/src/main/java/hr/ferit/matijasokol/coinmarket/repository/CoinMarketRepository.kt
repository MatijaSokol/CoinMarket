package hr.ferit.matijasokol.coinmarket.repository

import hr.ferit.matijasokol.coinmarket.db.CoinDao
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.networking.CoinsMarketApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinMarketRepository @Inject constructor(
    private val coinDao: CoinDao,
    private val coinApi: CoinsMarketApi
) {

    suspend fun getCoins() = coinApi.getCoins()

    suspend fun getYearCoinDetails(id: String) = coinApi.getYearCoinDetails(id)

    suspend fun getLastDayCoinDetails(id: String) = coinApi.getLastDayCoinDetails(id)

    suspend fun getCoinInfo(id: String) = coinApi.getCoinInfo(id)

    suspend fun upsertList(coins: List<Coin>) = coins.forEach { coinDao.upsert(it) }

    suspend fun getSavedCoins() = coinDao.getAllCoins()
}