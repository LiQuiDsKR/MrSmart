package com.mrsmart.standard.membership

import com.google.gson.annotations.SerializedName

data class SubPart(
    val id: Long,
    val name: String,
    val mainPartDto: MainPart,
    @SerializedName("latitude") val unusedLatitude: String,
    @SerializedName("longitude") val unusedLongitude: String,
    @SerializedName("mapScale") val unusedMapScale: String
)