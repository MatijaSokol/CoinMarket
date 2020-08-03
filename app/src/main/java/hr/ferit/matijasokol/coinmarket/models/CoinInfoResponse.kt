package hr.ferit.matijasokol.coinmarket.models

import com.google.gson.annotations.SerializedName

data class CoinInfoResponse(
    @SerializedName("hashing_algorithm") val hashAlgorithm: String?,
    val description: Description
)