package nz.co.bibble.ktmcptest.hueclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import nz.co.bibble.ktmcptest.dtos.ColorState
import nz.co.bibble.ktmcptest.dtos.DimmingState
import nz.co.bibble.ktmcptest.dtos.HueResponse
import nz.co.bibble.ktmcptest.dtos.LightDto
import nz.co.bibble.ktmcptest.dtos.LightStateRequestDto
import nz.co.bibble.ktmcptest.dtos.OnState
import nz.co.bibble.ktmcptest.dtos.RoomDto
import nz.co.bibble.ktmcptest.dtos.XYCoordinates
import org.slf4j.LoggerFactory

class HueClientImpl(
    private val httpClient: HttpClient,
    private val bridgeIp: String,
    private val applicationKey: String,
) : HueClient {
    private val logger = LoggerFactory.getLogger(HueClientImpl::class.java)
    private val baseURL = "https://$bridgeIp/clip/v2/resource"

    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                https {
                    trustManager =
                        object : javax.net.ssl.X509TrustManager {
                            override fun checkClientTrusted(
                                chain: Array<out java.security.cert.X509Certificate>?,
                                authType: String?,
                            ) {}

                            override fun checkServerTrusted(
                                chain: Array<out java.security.cert.X509Certificate>?,
                                authType: String?,
                            ) {}

                            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? = null
                        }
                }
            }
        }

    // Helper function to perform GET requests
    private suspend inline fun <reified T> getResource(endpoint: String): T {
        logger.info("Fetching resource from $endpoint")
        val response: HttpResponse =
            client.get {
                url("$baseURL/$endpoint")
                headers { append("hue-application-key", applicationKey) }
            }
        return response.body()
    }

    // GET lightId for light name
    override suspend fun getLightId(lightName: String): String {
        val lights = getResource<HueResponse<LightDto>>("light").data
        return lights.find { it.metadata.name == lightName }?.id
            ?: error("Light not found: $lightName")
    }

    // GET all lightIds
    override suspend fun getLightIds(): List<String> {
        val lights = getResource<HueResponse<LightDto>>("light").data
        return lights.map { it.id }
    }

    // GET all lights
    override suspend fun getLights(): List<LightDto> {
        return getResource<HueResponse<LightDto>>("light").data
    }

    // GET all Rooms
    override suspend fun getRooms(): List<RoomDto> {
        return getResource<HueResponse<RoomDto>>("room").data
    }

    // Find light IDs for a specific room
    override suspend fun findLightIdsForRoom(roomName: String): List<String> {
        val rooms = getRooms()
        val room = rooms.find { it.metadata.name == roomName } ?: return emptyList()
        val deviceIds = room.children.filter { it.rtype == "device" }.mapNotNull { it.rid }
        val lights = getLights()
        return lights.filter { deviceIds.contains(it.owner?.rid) }.map { it.id }
    }

    // Set the state of a light
    override suspend fun setLightState(
        lightId: String,
        on: Boolean?,
        brightness: Double?,
        colorGamutX: Double?,
        colorGamutY: Double?,
        dimming: Double?,
    ) {
        val requestBody =
            LightStateRequestDto(
                on = on?.let { OnState(it) },
                color =
                    if (colorGamutX != null && colorGamutY != null) {
                        ColorState(xy = XYCoordinates(colorGamutX, colorGamutY))
                    } else {
                        null
                    },
                dimming = brightness?.let { DimmingState(it) },
            )

        logger.info("Setting light state for lightId=$lightId with requestBody=$requestBody")

        try {
            client.put {
                url("$baseURL/light/$lightId")
                headers { append("hue-application-key", applicationKey) }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            logger.info("Successfully updated light state for lightId=$lightId")
        } catch (e: Exception) {
            logger.error("Failed to update light state for lightId=$lightId", e)
            throw e
        }
    }
}
