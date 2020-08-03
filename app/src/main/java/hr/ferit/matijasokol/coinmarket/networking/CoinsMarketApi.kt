package hr.ferit.matijasokol.coinmarket.networking

import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.models.CoinDetailsReponse
import hr.ferit.matijasokol.coinmarket.models.CoinInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinsMarketApi {

    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") valute: String = "eur",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ) : Response<List<Coin>>

    @GET("coins/{id}/market_chart")
    suspend fun getYearCoinDetails(
        @Path("id") id: String,
        @Query("vs_currency") valute: String = "eur",
        @Query("days") days: Int = 365
    ) : Response<CoinDetailsReponse>

    @GET("coins/{id}/market_chart")
    suspend fun getLastDayCoinDetails(
        @Path("id") id: String,
        @Query("vs_currency") valute: String = "eur",
        @Query("days") days: Int = 2
    ) : Response<CoinDetailsReponse>

    @GET("coins/{id}")
    suspend fun getCoinInfo(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false
    ) : Response<CoinInfoResponse>
}