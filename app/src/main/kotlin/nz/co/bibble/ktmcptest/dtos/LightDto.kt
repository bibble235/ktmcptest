package nz.co.bibble.ktmcptest.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class HueResponse<T>(val data: List<T>)

@Serializable data class LightMetadata(val name: String)

@Serializable data class LightOn(val on: Boolean)

@Serializable data class Dimming(val brightness: Double)

@Serializable data class Owner(val rid: String, val rtype: String)

@Serializable
data class LightDto(
    val id: String,
    @SerialName("id_v1") val idV1: String,
    val owner: Owner?,
    @SerialName("metadata") val metadata: LightMetadata,
    val on: LightOn,
    val dimming: Dimming?,
)
