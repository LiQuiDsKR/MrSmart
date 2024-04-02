package com.mrsmart.standard.membership.part

import com.google.gson.annotations.SerializedName

data class MainPartDto(
    val id: Long,
    val name: String,
    @SerializedName("latitude") val unusedLatitude: String,
    @SerializedName("longitude") val unusedLongitude: String,
    @SerializedName("mapScale") val unusedMapScale: String
)