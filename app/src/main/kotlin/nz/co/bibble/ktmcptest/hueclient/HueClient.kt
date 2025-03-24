package nz.co.bibble.ktmcptest.hueclient

import nz.co.bibble.ktmcptest.dtos.LightDto
import nz.co.bibble.ktmcptest.dtos.RoomDto

interface HueClient {
    suspend fun getLightId(lightName: String): String

    suspend fun getLightIds(): List<String>

    suspend fun getLights(): List<LightDto>

    suspend fun getRooms(): List<RoomDto>

    suspend fun findLightIdsForRoom(roomName: String): List<String>

    suspend fun setLightState(
        lightId: String,
        on: Boolean? = null,
        brightness: Double?,
        colorGamutX: Double?,
        colorGamutY: Double?,
        dimming: Double?,
    )
}
