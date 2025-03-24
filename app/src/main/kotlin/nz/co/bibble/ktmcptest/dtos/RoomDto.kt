package nz.co.bibble.ktmcptest.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val id: String,
    val id_v1: String,
    val children: List<Child>,
    val services: List<Service>,
    @SerialName("metadata") val metadata: RoomMetadata,
    val type: String,
)

@Serializable
data class Child(
    val rid: String,
    val rtype: String,
)

@Serializable
data class Service(
    val rid: String,
    val rtype: String,
)

@Serializable
data class RoomMetadata(
    val name: String,
    val archetype: String,
)
