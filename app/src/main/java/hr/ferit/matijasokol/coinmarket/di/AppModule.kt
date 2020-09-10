package hr.ferit.matijasokol.coinmarket.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.matijasokol.coinmarket.db.CoinDatabase
import hr.ferit.matijasokol.coinmarket.networking.CoinsMarketApi
import hr.ferit.matijasokol.coinmarket.other.Constants
import hr.ferit.matijasokol.coinmarket.other.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCoinDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context.applicationContext,
        CoinDatabase::class.java,
        Constants.DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideCoinDao(coinDatabase: CoinDatabase) = coinDatabase.getCoinDao()

    @Singleton
    @Provides
    fun provideGsonConverterFactory() = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(gsonConverterFactory: GsonConverterFactory): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit) = retrofit.create(CoinsMarketApi::class.java)
}