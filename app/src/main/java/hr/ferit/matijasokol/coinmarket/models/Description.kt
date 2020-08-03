package hr.ferit.matijasokol.coinmarket.models

import com.google.gson.annotations.SerializedName

data class Description(
    @SerializedName("en") val english: String
)