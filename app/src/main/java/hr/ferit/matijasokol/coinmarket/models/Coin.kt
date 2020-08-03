package hr.ferit.matijasokol.coinmarket.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "coins")
data class Coin(
    @PrimaryKey
    val id: String,
    val name: String,
    @SerializedName("image") val imageUrl: String,
    @SerializedName("current_price") val currentPrice: Float,
    @SerializedName("market_cap") val marketCap: Float,
    @SerializedName("market_cap_rank") val marketCapRank: Float,
    @SerializedName("total_volume") val totalVolume: Float,
    @SerializedName("high_24h") val maxPrice: Float,
    @SerializedName("low_24h") val minPrice: Float,
    @SerializedName("price_change_24h") val priceChange: Float,
    @SerializedName("price_change_percentage_24h") val priceChangePercentage: Float,
    @SerializedName("market_cap_change_24h") val marketCapChange: Float,
    @SerializedName("market_cap_change_percentage_24h") val marketCapChangePercentage: Float,
    @SerializedName("last_updated") val lastUpdate: String
) : Parcelable