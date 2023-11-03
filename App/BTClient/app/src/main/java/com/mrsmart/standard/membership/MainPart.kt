package com.mrsmart.standard.membership

import com.google.gson.annotations.SerializedName

data class MainPart(
    val id: Long,
    val name: String,
    @SerializedName("latitude") val unusedLatitude: String,
    @SerializedName("longitude") val unusedLongitude: String,
    @SerializedName("mapScale") val unusedMapScale: String
)